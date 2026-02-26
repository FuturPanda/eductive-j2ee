package com.formations.spring_products_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidDateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
	
	String message() default "La date de livraison doit être postérieure ou égale à la date de commande";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
