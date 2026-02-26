package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.dto.CreateProductRequest;
import com.formations.spring_products_api.dto.PaginatedResponse;
import com.formations.spring_products_api.dto.StockAdjustment;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<PaginatedResponse<Product>> getAllProducts(
		@PageableDefault Pageable pageable
	) {
		return ResponseEntity.ok(
			PaginatedResponse.from(productService.getAllProducts(pageable))
		);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProduct(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProduct(id));
	}

	@PostMapping
	public ResponseEntity<Product> createProduct(
		@Valid @RequestBody CreateProductRequest request
	) {
		Product created = productService.createProduct(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.getId())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(
		@PathVariable Long id,
		@Valid @RequestBody Product product
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
}
