package com.formations.spring_products_api.dto;

import com.formations.spring_products_api.validation.ValidSKU;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record UpdateProductRequest(
	@NotBlank(message = "Le nom du produit est obligatoire")
	@Size(min = 2, max = 200, message = "Le nom doit contenir entre {min} et {max} caractères")
	String name,

	@Size(max = 1000, message = "La description ne doit pas dépasser {max} caractères")
	String description,

	@NotNull(message = "Le prix est obligatoire")
	@DecimalMin(value = "0.01", message = "Le prix doit être au minimum de {value}")
	@Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au maximum {integer} chiffres entiers et {fraction} décimales")
	BigDecimal price,

	@NotNull(message = "Le stock est obligatoire")
	@Min(value = 0, message = "Le stock ne peut pas être négatif")
	Integer stock,

	@ValidSKU String sku,

	@NotBlank(message = "La catégorie est obligatoire") String category,

	String supplier
) {}
