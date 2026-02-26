package com.formations.spring_products_api.service;

import com.formations.spring_products_api.exception.InvalidProductException;
import com.formations.spring_products_api.exception.ProductNotFoundException;
import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.repository.ICategoryRepository;
import com.formations.spring_products_api.repository.IProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

	private final IProductRepository productRepository;
	private final ICategoryRepository categoryRepository;

	public ProductService(
		IProductRepository productRepository,
		ICategoryRepository categoryRepository
	) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	public Product createProduct(Product product) {
		validateProduct(product);
		return productRepository.save(product);
	}

	@Transactional
	public Product createProductWithCategory(Product product, String categoryName) {
		validateProduct(product);
		
		Category category = categoryRepository.findByName(categoryName)
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

	public Product updateProduct(Long id, Product updatedProduct) {
		return productRepository
			.findById(id)
			.map(existing -> {
				existing.setName(updatedProduct.getName());
				existing.setDescription(updatedProduct.getDescription());
				existing.setPrice(updatedProduct.getPrice());
				existing.setCategory(updatedProduct.getCategory());
				existing.setStock(updatedProduct.getStock());
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
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new ProductNotFoundException(productId.toString()));
		
		int newStock = product.getStock() + quantity;
		if (newStock < 0) {
			throw new InvalidProductException("Stock cannot be negative");
		}
		
		product.setStock(newStock);
		return productRepository.save(product);
	}

	@Transactional
	public void transferProducts(Long fromCategoryId, Long toCategoryId) {
		Category fromCategory = categoryRepository.findById(fromCategoryId)
			.orElseThrow(() -> new RuntimeException("Source category not found: " + fromCategoryId));
		
		Category toCategory = categoryRepository.findById(toCategoryId)
			.orElseThrow(() -> new RuntimeException("Target category not found: " + toCategoryId));
		
		List<Product> products = productRepository.findByCategoryId(fromCategoryId);
		
		for (Product product : products) {
			product.setCategory(toCategory);
		}
		
		productRepository.saveAll(products);
	}

	@Transactional
	public Product testRollback(String productName, BigDecimal price) {
		Product p = new Product(productName, price);
		productRepository.save(p);
		productRepository.flush();
		throw new RuntimeException("Test rollback - this product should NOT be in database");
	}

	private void validateProduct(Product product) {
		if (product.getName() == null || product.getName().isBlank()) {
			throw new InvalidProductException("Product name cannot be empty");
		}
		if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidProductException("Product price must be positive");
		}
	}
}
