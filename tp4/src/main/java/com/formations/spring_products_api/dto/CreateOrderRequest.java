package com.formations.spring_products_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record CreateOrderRequest(
	@NotBlank(message = "Le nom du client est obligatoire")
	@Size(min = 2, max = 100, message = "Le nom du client doit contenir entre {min} et {max} caractères")
	String customerName,

	@NotBlank(message = "L'email du client est obligatoire")
	@Email(message = "L'email doit être une adresse valide")
	String customerEmail,

	@NotEmpty(message = "La commande doit contenir au moins un produit")
	Map<Long, Integer> products
) {}
