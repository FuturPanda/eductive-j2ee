package com.formations.spring_products_api.service;

import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.repository.ICategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

	private final ICategoryRepository categoryRepository;

	public CategoryService(ICategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public Category createCategory(String name, String description) {
		Category category = new Category();
		category.setName(name);
		category.setDescription(description);
		return categoryRepository.save(category);
	}

	@Transactional(readOnly = true)
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Category getCategoryWithProducts(Long id) {
		return categoryRepository.findByIdWithProducts(id)
			.orElseThrow(() -> new RuntimeException("Category not found: " + id));
	}

	public void deleteCategory(Long id) {
		categoryRepository.deleteById(id);
	}

	public Category updateCategory(Long id, Category updatedCategory) {
		return categoryRepository.findById(id)
			.map(existing -> {
				existing.setName(updatedCategory.getName());
				existing.setDescription(updatedCategory.getDescription());
				return categoryRepository.save(existing);
			})
			.orElseThrow(() -> new RuntimeException("Category not found: " + id));
	}
}
