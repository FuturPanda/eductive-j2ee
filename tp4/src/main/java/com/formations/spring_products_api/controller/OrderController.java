package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.dto.AddItemRequest;
import com.formations.spring_products_api.dto.CreateOrderRequest;
import com.formations.spring_products_api.dto.UpdateItemQuantityRequest;
import com.formations.spring_products_api.dto.UpdateOrderStatusRequest;
import com.formations.spring_products_api.model.Order;
import com.formations.spring_products_api.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/orders")
@Validated
@Tag(name = "Orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	public ResponseEntity<List<Order>> getAllOrders() {
		return ResponseEntity.ok(orderService.getAllOrdersWithItems());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Order> getOrder(@PathVariable Long id) {
		return ResponseEntity.ok(orderService.getOrderWithItems(id));
	}

	@PostMapping
	public ResponseEntity<Order> createOrder(
		@Valid @RequestBody CreateOrderRequest request
	) {
		Order created = orderService.createOrder(
			request.customerName(),
			request.customerEmail(),
			request.products()
		);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.getId())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> updateOrderStatus(
		@PathVariable Long id,
		@Valid @RequestBody UpdateOrderStatusRequest request
	) {
		orderService.updateOrderStatus(id, request.status());
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{orderId}/items")
	public ResponseEntity<Order> addItem(
		@PathVariable Long orderId,
		@Valid @RequestBody AddItemRequest request
	) {
		return ResponseEntity.ok(
			orderService.addItemToOrder(
				orderId,
				request.productId(),
				request.quantity()
			)
		);
	}

	@PatchMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<Order> updateItemQuantity(
		@PathVariable Long orderId,
		@PathVariable Long itemId,
		@Valid @RequestBody UpdateItemQuantityRequest request
	) {
		return ResponseEntity.ok(
			orderService.updateItemQuantity(orderId, itemId, request.quantity())
		);
	}

	@DeleteMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<Order> removeItem(
		@PathVariable Long orderId,
		@PathVariable Long itemId
	) {
		return ResponseEntity.ok(
			orderService.removeItemFromOrder(orderId, itemId)
		);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
		orderService.deleteOrder(id);
		return ResponseEntity.noContent().build();
	}
}
