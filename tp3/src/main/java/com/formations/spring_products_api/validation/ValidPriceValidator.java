package com.formations.spring_products_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class ValidPriceValidator implements ConstraintValidator<ValidPrice, BigDecimal> {

	@Override
	public void initialize(ValidPrice constraintAnnotation) {
	}

	@Override
	public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
		if (value == null) {
			return true; // null est valide (utilisez @NotNull séparément si nécessaire)
		}

		// Vérifier que le nombre a max 2 décimales
		// stripTrailingZeros() pour gérer les cas comme 99.990 -> 99.99
		return value.stripTrailingZeros().scale() <= 2;
	}
}
