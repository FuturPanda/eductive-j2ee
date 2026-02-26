package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.model.Order;
import com.formations.spring_products_api.model.OrderStatus;
import com.formations.spring_products_api.service.OrderService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/orders")
@Validated
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

	@GetMapping("/by-email")
	public ResponseEntity<List<Order>> getOrdersByEmail(@RequestParam String email) {
		return ResponseEntity.ok(orderService.getOrdersByCustomerEmail(email));
	}

	@GetMapping("/by-status")
	public ResponseEntity<List<Order>> getOrdersByStatus(@RequestParam OrderStatus status) {
		return ResponseEntity.ok(orderService.getOrdersByStatus(status));
	}

	@PostMapping
	public ResponseEntity<Order> createOrder(
		@RequestParam @NotBlank(message = "Le nom du client est obligatoire") 
		@Size(min = 2, max = 100, message = "Le nom du client doit contenir entre {min} et {max} caractères") 
		String customerName,
		
		@RequestParam @NotBlank(message = "L'email du client est obligatoire") 
		@Email(message = "L'email doit être une adresse valide") 
		String customerEmail,
		
		@RequestBody Map<Long, Integer> productsAndQuantities
	) {
		Order created = orderService.createOrder(customerName, customerEmail, productsAndQuantities);
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
		return ResponseEntity.ok(orderService.addItemToOrder(orderId, productId, quantity));
	}

	@PatchMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<Order> updateItemQuantity(
		@PathVariable Long orderId,
		@PathVariable Long itemId,
		@RequestParam int quantity
	) {
		return ResponseEntity.ok(orderService.updateItemQuantity(orderId, itemId, quantity));
	}

	@DeleteMapping("/{orderId}/items/{itemId}")
	public ResponseEntity<Order> removeItem(
		@PathVariable Long orderId,
		@PathVariable Long itemId
	) {
		return ResponseEntity.ok(orderService.removeItemFromOrder(orderId, itemId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
		orderService.deleteOrder(id);
		return ResponseEntity.noContent().build();
	}
}
