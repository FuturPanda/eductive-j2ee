package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.service.CategoryService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		return ResponseEntity.ok(categoryService.getAllCategories());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Category> getCategoryWithProducts(@PathVariable Long id) {
		return ResponseEntity.ok(categoryService.getCategoryWithProducts(id));
	}

	@PostMapping
	public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
		Category created = categoryService.createCategory(category.getName(), category.getDescription());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.getId())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Category> updateCategory(
		@PathVariable Long id,
		@Valid @RequestBody Category category
	) {
		return ResponseEntity.ok(categoryService.updateCategory(id, category));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
		categoryService.deleteCategory(id);
		return ResponseEntity.noContent().build();
	}
}
