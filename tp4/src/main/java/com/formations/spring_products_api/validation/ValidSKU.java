package com.formations.spring_products_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SKUValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSKU {
	
	String message() default "Le SKU doit contenir 3 lettres suivies de 3 chiffres (ex: ABC123)";
	
	Class<?>[] groups() default {};
	
	Class<? extends Payload>[] payload() default {};
}
