package com.formations.spring_products_api.service;

import com.formations.spring_products_api.exception.InvalidProductException;
import com.formations.spring_products_api.exception.ProductNotFoundException;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.repository.IProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

	private final IProductRepository productRepository;

	public ProductService(IProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public Product createProduct(Product product) {
		if (product.getName() == null || product.getName().isBlank()) {
			throw new InvalidProductException("Product name cannot be empty");
		}
		if (
			product.getPrice() == null ||
			product.getPrice().compareTo(BigDecimal.ZERO) <= 0
		) {
			throw new InvalidProductException("Product price must be positive");
		}
		return productRepository.save(product);
	}

	public Product getProduct(String id) {
		return productRepository
			.findById(id)
			.orElseThrow(() -> new ProductNotFoundException(id));
	}

	public List<Product> getProducts(String category) {
		if (category != null && !category.isBlank()) {
			return productRepository.findByCategory(category);
		}
		return productRepository.findAll();
	}

	public Product updateProduct(String id, Product updatedProduct) {
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
			.orElseThrow(() -> new ProductNotFoundException(id));
	}

	public void deleteProduct(String id) {
		if (!productRepository.exists(id)) {
			throw new ProductNotFoundException(id);
		}
		productRepository.delete(id);
	}

	public Product updateStock(String id, int quantity) {
		return productRepository
			.findById(id)
			.map(product -> {
				product.setStock(product.getStock() + quantity);
				return productRepository.save(product);
			})
			.orElseThrow(() -> new ProductNotFoundException(id));
	}
}
