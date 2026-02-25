package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.dto.StockAdjustment;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.service.ProductService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts(
		@RequestParam(required = false) String category
	) {
		return ResponseEntity.ok(productService.getProducts(category));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable String id) {
		return ResponseEntity.ok(productService.getProduct(id));
	}

	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		Product created = productService.createProduct(product);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.getId())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(
		@PathVariable String id,
		@RequestBody Product product
	) {
		return ResponseEntity.ok(productService.updateProduct(id, product));
	}

	@PatchMapping("/{id}/stock")
	public ResponseEntity<Product> adjustStock(
		@PathVariable String id,
		@RequestBody StockAdjustment adjustment
	) {
		return ResponseEntity.ok(
			productService.updateStock(id, adjustment.quantity())
		);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}
