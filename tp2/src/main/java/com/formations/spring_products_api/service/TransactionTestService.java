package com.formations.spring_products_api.service;

import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.repository.IProductRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionTestService {

	private final IProductRepository productRepository;

	public TransactionTestService(IProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Transactional
	public Product createProductSuccess() {
		Product p = new Product();
		p.setName("Transaction Test Product");
		p.setDescription("This product should be saved");
		p.setPrice(new BigDecimal("99.99"));
		p.setStock(10);
		return productRepository.save(p);
	}

	@Transactional
	public void testRollback() {
		Product p = new Product();
		p.setName("Rollback Test Product");
		p.setDescription("This product should NOT be saved due to rollback");
		p.setPrice(new BigDecimal("49.99"));
		p.setStock(5);
		
		productRepository.save(p);
		
		throw new RuntimeException("Test rollback - this exception should cause a rollback");
	}

	@Transactional
	public Product testAutoUpdate(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new RuntimeException("Product not found: " + productId));
		
		product.setName(product.getName() + " - UPDATED");
		product.setStock(product.getStock() + 100);
		
		return product;
	}

	public long countProducts() {
		return productRepository.count();
	}
}
