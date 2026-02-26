package com.formations.spring_products_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class SKUValidator implements ConstraintValidator<ValidSKU, String> {

	// Format: 3 lettres majuscules + 3 chiffres (ex: ABC123)
	private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z]{3}[0-9]{3}$");

	@Override
	public void initialize(ValidSKU constraintAnnotation) {
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		return SKU_PATTERN.matcher(value).matches();
	}
}
