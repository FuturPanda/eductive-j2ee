package com.formations.spring_products_api.exception;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException(String id) {
		super("Order not found: " + id);
	}
}
