package com.formations.spring_products_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPriceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
	
	String message() default "Le prix doit avoir au maximum 2 décimales (ex: 99.99, pas 99.999)";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
