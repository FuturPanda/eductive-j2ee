package com.formations.spring_products_api.dto;

import jakarta.validation.constraints.*;

public record CreateCategoryRequest(
	@NotBlank(message = "Le nom de la catégorie est obligatoire")
	@Size(min = 2, max = 100, message = "Le nom doit contenir entre {min} et {max} caractères")
	String name,

	@Size(max = 500, message = "La description ne doit pas dépasser {max} caractères")
	String description
) {}
