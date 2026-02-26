package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.dto.StockAdjustment;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.service.ProductService;
import java.math.BigDecimal;
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
		@RequestParam(required = false) String keyword
	) {
		if (keyword != null && !keyword.isBlank()) {
			return ResponseEntity.ok(productService.searchProducts(keyword));
		}
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping("/slow")
	public ResponseEntity<List<Product>> getAllProductsSlow() {
		return ResponseEntity.ok(productService.getAllProductsSlow());
	}

	@GetMapping("/fast")
	public ResponseEntity<List<Product>> getAllProductsFast(
		@RequestParam(defaultValue = "full") String graph
	) {
		if ("category".equals(graph)) {
			return ResponseEntity.ok(productService.getAllProductsWithCategoryGraph());
		}
		return ResponseEntity.ok(productService.getAllProductsWithFullGraph());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable Long id) {
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

	@PostMapping("/with-category")
	public ResponseEntity<Product> createProductWithCategory(
		@RequestBody Product product,
		@RequestParam String categoryName
	) {
		Product created = productService.createProductWithCategory(product, categoryName);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.getId())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(
		@PathVariable Long id,
		@RequestBody Product product
	) {
		return ResponseEntity.ok(productService.updateProduct(id, product));
	}

	@PatchMapping("/{id}/stock")
	public ResponseEntity<Product> adjustStock(
		@PathVariable Long id,
		@RequestBody StockAdjustment adjustment
	) {
		return ResponseEntity.ok(
			productService.updateStock(id, adjustment.quantity())
		);
	}

	@PostMapping("/transfer")
	public ResponseEntity<Void> transferProducts(
		@RequestParam Long fromCategoryId,
		@RequestParam Long toCategoryId
	) {
		productService.transferProducts(fromCategoryId, toCategoryId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/test-rollback")
	public ResponseEntity<Product> testRollback(
		@RequestParam String name,
		@RequestParam BigDecimal price
	) {
		return ResponseEntity.ok(productService.testRollback(name, price));
	}
}
