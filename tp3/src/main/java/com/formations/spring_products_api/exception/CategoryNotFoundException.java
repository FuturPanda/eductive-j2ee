package com.formations.spring_products_api.exception;

public class CategoryNotFoundException extends RuntimeException {

	public CategoryNotFoundException(String identifier) {
		super("Catégorie non trouvée: " + identifier);
	}
}
