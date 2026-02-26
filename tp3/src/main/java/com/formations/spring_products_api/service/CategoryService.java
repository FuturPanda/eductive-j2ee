package com.formations.spring_products_api.service;

import com.formations.spring_products_api.exception.CategoryHasProductsException;
import com.formations.spring_products_api.exception.CategoryNotFoundException;
import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.repository.ICategoryRepository;
import com.formations.spring_products_api.repository.IProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

	private final ICategoryRepository categoryRepository;
	private final IProductRepository productRepository;

	public CategoryService(ICategoryRepository categoryRepository, IProductRepository productRepository) {
		this.categoryRepository = categoryRepository;
		this.productRepository = productRepository;
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
			.orElseThrow(() -> new CategoryNotFoundException(id.toString()));
	}

	public void deleteCategory(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new CategoryNotFoundException(id.toString());
		}
		
		List<?> products = productRepository.findByCategoryId(id);
		if (!products.isEmpty()) {
			throw new CategoryHasProductsException(id, products.size());
		}
		
		categoryRepository.deleteById(id);
	}

	public Category updateCategory(Long id, Category updatedCategory) {
		return categoryRepository.findById(id)
			.map(existing -> {
				existing.setName(updatedCategory.getName());
				existing.setDescription(updatedCategory.getDescription());
				return categoryRepository.save(existing);
			})
			.orElseThrow(() -> new CategoryNotFoundException(id.toString()));
	}
}
