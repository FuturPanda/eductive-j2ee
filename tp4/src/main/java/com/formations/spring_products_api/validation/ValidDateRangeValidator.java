package com.formations.spring_products_api.validation;

import com.formations.spring_products_api.model.Order;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Order> {

	@Override
	public void initialize(ValidDateRange constraintAnnotation) {
	}

	@Override
	public boolean isValid(Order order, ConstraintValidatorContext context) {
		// Ne valide que si les deux dates sont présentes
		if (order == null || order.getOrderDate() == null || order.getDeliveryDate() == null) {
			return true;
		}

		// deliveryDate doit être >= orderDate
		return !order.getDeliveryDate().isBefore(order.getOrderDate());
	}
}
