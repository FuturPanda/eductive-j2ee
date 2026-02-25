package com.formations.spring_products_api.exception;

import com.formations.spring_products_api.dto.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleProductNotFound(ProductNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorMessage("Produit non trouvé"));
	}

	@ExceptionHandler(InvalidProductException.class)
	public ResponseEntity<ErrorMessage> handleInvalidProduct(InvalidProductException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorMessage(e.getMessage()));
	}
}
