package com.formations.spring_products_api.exception;

public class InsufficientStockException extends RuntimeException {

	public InsufficientStockException(Long productId, int currentStock, int requestedDecrease) {
		super("Stock insuffisant pour le produit " + productId + 
			": stock actuel = " + currentStock + ", demandé = " + Math.abs(requestedDecrease));
	}
}
