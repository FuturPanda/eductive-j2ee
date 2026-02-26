package com.formations.spring_products_api.dto;

import jakarta.validation.constraints.*;

public record CreateSupplierRequest(
	@NotBlank(message = "Le nom du fournisseur est obligatoire")
	@Size(
		min = 2,
		max = 200,
		message = "Le nom doit contenir entre {min} et {max} caractères"
	)
	String name,

	@NotBlank(message = "L'email de contact est obligatoire")
	@Email(message = "L'email doit être valide")
	String contactEmail,

	@Size(
		max = 20,
		message = "Le téléphone ne doit pas dépasser {max} caractères"
	)
	String phone
) {}
