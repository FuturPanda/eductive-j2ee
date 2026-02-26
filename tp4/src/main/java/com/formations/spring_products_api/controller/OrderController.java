package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.dto.CreateOrderRequest;
import com.formations.spring_products_api.model.Order;
import com.formations.spring_products_api.model.OrderStatus;
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
		@RequestParam OrderStatus status
	) {
		orderService.updateOrderStatus(id, status);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{orderId}/items")
	public ResponseEntity<Order> addItem(
		@PathVariable Long orderId,
		@RequestParam Long productId,
		@RequestParam int quantity
	) {
		return ResponseEntity.ok(
			orderService.addItemToOrder(orderId, productId, quantity)
		);
	}

	@PatchMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<Order> updateItemQuantity(
		@PathVariable Long orderId,
		@PathVariable Long itemId,
		@RequestParam int quantity
	) {
		return ResponseEntity.ok(
			orderService.updateItemQuantity(orderId, itemId, quantity)
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
