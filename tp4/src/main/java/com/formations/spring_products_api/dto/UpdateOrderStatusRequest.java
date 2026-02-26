package com.formations.spring_products_api.dto;

import com.formations.spring_products_api.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
	@NotNull(message = "Le statut est obligatoire")
	OrderStatus status
) {}
