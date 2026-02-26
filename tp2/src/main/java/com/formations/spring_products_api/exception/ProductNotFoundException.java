package com.formations.spring_products_api.exception;

public class ProductNotFoundException extends RuntimeException {

	public ProductNotFoundException(String id) {
		super("Produit non trouvé: " + id);
	}
}
