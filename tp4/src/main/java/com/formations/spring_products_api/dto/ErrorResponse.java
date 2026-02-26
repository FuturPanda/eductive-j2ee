package com.formations.spring_products_api.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private String path;
	private List<FieldError> fieldErrors;

	public ErrorResponse() {
		this.timestamp = LocalDateTime.now();
		this.fieldErrors = new ArrayList<>();
	}

	public ErrorResponse(int status, String error, String message, String path) {
		this();
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public void addFieldError(String field, Object rejectedValue, String message) {
		this.fieldErrors.add(new FieldError(field, rejectedValue, message));
	}

	public void addFieldError(String field, String message) {
		this.fieldErrors.add(new FieldError(field, message));
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<FieldError> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(List<FieldError> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}
}
