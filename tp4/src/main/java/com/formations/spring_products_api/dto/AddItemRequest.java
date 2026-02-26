package com.formations.spring_products_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemRequest(
	@NotNull(message = "L'ID du produit est obligatoire")
	Long productId,

	@NotNull(message = "La quantité est obligatoire")
	@Min(value = 1, message = "La quantité doit être au minimum de {value}")
	int quantity
) {}
