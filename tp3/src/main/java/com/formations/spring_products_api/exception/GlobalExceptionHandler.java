package com.formations.spring_products_api.exception;

import com.formations.spring_products_api.dto.ErrorResponse;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.RollbackException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleProductNotFound(
			ProductNotFoundException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			"Not Found",
			"Produit non trouvé",
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleCategoryNotFound(
			CategoryNotFoundException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.NOT_FOUND.value(),
			"Not Found",
			e.getMessage(),
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(InvalidProductException.class)
	public ResponseEntity<ErrorResponse> handleInvalidProduct(
			InvalidProductException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			e.getMessage(),
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(DuplicateSkuException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateSku(
			DuplicateSkuException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.CONFLICT.value(),
			"Conflict",
			e.getMessage(),
			request.getRequestURI()
		);
		error.addFieldError("sku", e.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ErrorResponse> handleInsufficientStock(
			InsufficientStockException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.UNPROCESSABLE_ENTITY.value(),
			"Unprocessable Entity",
			e.getMessage(),
			request.getRequestURI()
		);
		error.addFieldError("stock", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
	}

	@ExceptionHandler(CategoryHasProductsException.class)
	public ResponseEntity<ErrorResponse> handleCategoryHasProducts(
			CategoryHasProductsException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.CONFLICT.value(),
			"Conflict",
			e.getMessage(),
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationExceptions(
			MethodArgumentNotValidException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			"Erreurs de validation",
			request.getRequestURI()
		);
		
		e.getBindingResult().getAllErrors().forEach(err -> {
			if (err instanceof FieldError fieldError) {
				error.addFieldError(
					fieldError.getField(),
					fieldError.getRejectedValue(),
					fieldError.getDefaultMessage()
				);
			} else {
				error.addFieldError(err.getObjectName(), err.getDefaultMessage());
			}
		});
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ErrorResponse> handleMethodValidation(
			HandlerMethodValidationException e, HttpServletRequest request) {
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			"Erreurs de validation des paramètres",
			request.getRequestURI()
		);
		
		for (ParameterValidationResult result : e.getParameterValidationResults()) {
			String paramName = result.getMethodParameter().getParameterName();
			result.getResolvableErrors().forEach(err -> {
				error.addFieldError(
					paramName != null ? paramName : "param",
					err.getDefaultMessage()
				);
			});
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(
			ConstraintViolationException e, HttpServletRequest request) {
		log.info("Handling ConstraintViolationException: {}", e.getMessage());
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			"Erreurs de validation",
			request.getRequestURI()
		);
		
		for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
			error.addFieldError(
				violation.getPropertyPath().toString(),
				violation.getInvalidValue(),
				violation.getMessage()
			);
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<ErrorResponse> handleTransactionException(
			TransactionSystemException e, HttpServletRequest request) {
		Throwable cause = e.getRootCause();
		if (cause instanceof ConstraintViolationException cve) {
			return handleConstraintViolation(cve, request);
		}
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			"Erreur de transaction",
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(RollbackException.class)
	public ResponseEntity<ErrorResponse> handleRollbackException(
			RollbackException e, HttpServletRequest request) {
		log.debug("Handling RollbackException", e);
		Throwable cause = e.getCause();
		if (cause instanceof ConstraintViolationException cve) {
			return handleConstraintViolation(cve, request);
		}
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			"Erreur de rollback",
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<ErrorResponse> handlePersistenceException(
			PersistenceException e, HttpServletRequest request) {
		log.debug("Handling PersistenceException: {}", e.getClass().getName(), e);
		Throwable cause = e;
		while (cause != null) {
			if (cause instanceof ConstraintViolationException cve) {
				return handleConstraintViolation(cve, request);
			}
			cause = cause.getCause();
		}
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			"Erreur de persistence",
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
			DataIntegrityViolationException e, HttpServletRequest request) {
		log.debug("Handling DataIntegrityViolationException", e);
		Throwable cause = e;
		while (cause != null) {
			if (cause instanceof ConstraintViolationException cve) {
				return handleConstraintViolation(cve, request);
			}
			cause = cause.getCause();
		}
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.BAD_REQUEST.value(),
			"Bad Request",
			"Erreur d'intégrité des données",
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception e, HttpServletRequest request) {
		log.warn("Handling generic exception: {} - {}", e.getClass().getName(), e.getMessage());
		Throwable cause = e;
		while (cause != null) {
			if (cause instanceof ConstraintViolationException cve) {
				return handleConstraintViolation(cve, request);
			}
			cause = cause.getCause();
		}
		
		ErrorResponse error = new ErrorResponse(
			HttpStatus.INTERNAL_SERVER_ERROR.value(),
			"Internal Server Error",
			"Erreur interne",
			request.getRequestURI()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
