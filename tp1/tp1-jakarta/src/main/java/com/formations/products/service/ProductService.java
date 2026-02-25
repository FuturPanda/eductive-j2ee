package com.formations.products.service;

import com.formations.products.interfaces.IProductRepository;
import com.formations.products.model.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProductService {

	@Inject
	private IProductRepository productRepository;

	public Product createProduct(Product product) {
		if (product.getName() == null || product.getName().isBlank()) {
			throw new IllegalArgumentException("Product name cannot be empty");
		}
		if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Product price must be positive");
		}
		return productRepository.save(product);
	}

	public Optional<Product> getProduct(String id) {
		return productRepository.findById(id);
	}

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public List<Product> getProductsByCategory(String category) {
		return productRepository.findByCategory(category);
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
			.orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
	}

	public void deleteProduct(String id) {
		if (!productRepository.exists(id)) {
			throw new IllegalArgumentException("Product not found: " + id);
		}
		productRepository.delete(id);
	}

	public void updateStock(String id, int quantity) {
		productRepository
			.findById(id)
			.ifPresentOrElse(
				product -> {
					product.setStock(product.getStock() + quantity);
					productRepository.save(product);
				},
				() -> { throw new IllegalArgumentException("Product not found: " + id); }
			);
	}
}
