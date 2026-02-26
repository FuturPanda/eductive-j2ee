package com.formations.spring_products_api.exception;

public class DuplicateSkuException extends RuntimeException {

	public DuplicateSkuException(String sku) {
		super("Un produit avec le SKU '" + sku + "' existe déjà");
	}
}
