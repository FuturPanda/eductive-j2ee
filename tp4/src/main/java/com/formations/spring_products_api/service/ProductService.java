package com.formations.spring_products_api.service;

import com.formations.spring_products_api.dto.CreateProductRequest;
import com.formations.spring_products_api.dto.UpdateProductRequest;
import com.formations.spring_products_api.exception.InsufficientStockException;
import com.formations.spring_products_api.exception.ProductNotFoundException;
import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.model.Supplier;
import com.formations.spring_products_api.repository.ICategoryRepository;
import com.formations.spring_products_api.repository.IProductRepository;
import com.formations.spring_products_api.repository.ISupplierRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

	private final IProductRepository productRepository;
	private final ICategoryRepository categoryRepository;
	private final ISupplierRepository supplierRepository;

	public ProductService(
		IProductRepository productRepository,
		ICategoryRepository categoryRepository,
		ISupplierRepository supplierRepository
	) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
		this.supplierRepository = supplierRepository;
	}

	public Product createProduct(CreateProductRequest request) {
		Product product = new Product();
		product.setName(request.name());
		product.setDescription(request.description());
		product.setPrice(request.price());
		product.setStock(request.stock());
		product.setSku(request.sku());
		product.setCategory(resolveCategory(request.category()));
		if (request.supplier() != null && !request.supplier().isBlank()) {
			product.setSupplier(resolveSupplier(request.supplier()));
		}

		return productRepository.save(product);
	}

	private Category resolveCategory(String categoryRef) {
		try {
			Long id = Long.parseLong(categoryRef);
			return categoryRepository
				.findById(id)
				.orElseThrow(() ->
					new RuntimeException("Category not found: " + categoryRef)
				);
		} catch (NumberFormatException e) {
			return categoryRepository
				.findByName(categoryRef)
				.orElseGet(() -> {
					Category newCategory = new Category();
					newCategory.setName(categoryRef);
					return categoryRepository.save(newCategory);
				});
		}
	}

	private Supplier resolveSupplier(String supplierRef) {
		try {
			Long id = Long.parseLong(supplierRef);
			return supplierRepository
				.findById(id)
				.orElseThrow(() ->
					new RuntimeException("Supplier not found: " + supplierRef)
				);
		} catch (NumberFormatException e) {
			return supplierRepository
				.findByName(supplierRef)
				.orElseThrow(() ->
					new RuntimeException("Supplier not found: " + supplierRef)
				);
		}
	}

	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	@Transactional
	public Product createProductWithCategory(
		Product product,
		String categoryName
	) {
		Category category = categoryRepository
			.findByName(categoryName)
			.orElseGet(() -> {
				Category newCategory = new Category();
				newCategory.setName(categoryName);
				return categoryRepository.save(newCategory);
			});

		product.setCategory(category);
		return productRepository.save(product);
	}

	@Transactional(readOnly = true)
	public Product getProduct(Long id) {
		return productRepository
			.findByIdWithRelations(id)
			.orElseThrow(() -> new ProductNotFoundException(id.toString()));
	}

	@Transactional(readOnly = true)
	public List<Product> getAllProducts() {
		return productRepository.findAllWithRelations();
	}

	@Transactional(readOnly = true)
	public Page<Product> getAllProducts(Pageable pageable) {
		return productRepository.findAllWithRelations(pageable);
	}

	@Transactional(readOnly = true)
	public List<Product> getAllProductsSlow() {
		return productRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Product> getAllProductsWithCategoryGraph() {
		return productRepository.findAllWithCategoryGraph();
	}

	@Transactional(readOnly = true)
	public List<Product> getAllProductsWithFullGraph() {
		return productRepository.findAllWithFullGraph();
	}

	@Transactional(readOnly = true)
	public List<Product> searchProducts(String keyword) {
		return productRepository.searchByName(keyword);
	}

	@Transactional(readOnly = true)
	public Page<Product> searchProducts(String keyword, Pageable pageable) {
		return productRepository.searchByName(keyword, pageable);
	}

	public Product updateProduct(Long id, Product updatedProduct) {
		return productRepository
			.findById(id)
			.map(existing -> {
				existing.setName(updatedProduct.getName());
				existing.setDescription(updatedProduct.getDescription());
				existing.setPrice(updatedProduct.getPrice());
				existing.setCategory(updatedProduct.getCategory());
				existing.setStock(updatedProduct.getStock());
				existing.setSku(updatedProduct.getSku());
				return productRepository.save(existing);
			})
			.orElseThrow(() -> new ProductNotFoundException(id.toString()));
	}

	public Product updateProduct(Long id, UpdateProductRequest request) {
		return productRepository
			.findById(id)
			.map(existing -> {
				existing.setName(request.name());
				existing.setDescription(request.description());
				existing.setPrice(request.price());
				existing.setCategory(resolveCategory(request.category()));
				existing.setStock(request.stock());
				existing.setSku(request.sku());
				if (
					request.supplier() != null && !request.supplier().isBlank()
				) {
					existing.setSupplier(resolveSupplier(request.supplier()));
				}
				return productRepository.save(existing);
			})
			.orElseThrow(() -> new ProductNotFoundException(id.toString()));
	}

	public void deleteProduct(Long id) {
		if (!productRepository.existsById(id)) {
			throw new ProductNotFoundException(id.toString());
		}
		productRepository.deleteById(id);
	}

	@Transactional
	public Product updateStock(Long productId, int quantity) {
		Product product = productRepository
			.findById(productId)
			.orElseThrow(() ->
				new ProductNotFoundException(productId.toString())
			);

		int newStock = product.getStock() + quantity;
		if (newStock < 0) {
			throw new InsufficientStockException(
				productId,
				product.getStock(),
				quantity
			);
		}

		product.setStock(newStock);
		return productRepository.save(product);
	}

	@Transactional
	public void transferProducts(Long fromCategoryId, Long toCategoryId) {
		Category toCategory = categoryRepository
			.findById(toCategoryId)
			.orElseThrow(() ->
				new RuntimeException(
					"Target category not found: " + toCategoryId
				)
			);

		List<Product> products = productRepository.findByCategoryId(
			fromCategoryId
		);

		for (Product product : products) {
			product.setCategory(toCategory);
		}

		productRepository.saveAll(products);
	}
}
