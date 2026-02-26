package com.formations.spring_products_api.exception;

public class CategoryHasProductsException extends RuntimeException {

	public CategoryHasProductsException(Long categoryId, int productCount) {
		super("Impossible de supprimer la catégorie " + categoryId + 
			": elle contient " + productCount + " produit(s)");
	}
}
