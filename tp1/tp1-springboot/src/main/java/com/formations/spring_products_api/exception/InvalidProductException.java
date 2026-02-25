package com.formations.spring_products_api.exception;

public class InvalidProductException extends RuntimeException {

	public InvalidProductException(String message) {
		super(message);
	}
}
