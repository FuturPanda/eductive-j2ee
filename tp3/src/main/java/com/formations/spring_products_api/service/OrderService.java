package com.formations.spring_products_api.service;

import com.formations.spring_products_api.model.Order;
import com.formations.spring_products_api.model.OrderItem;
import com.formations.spring_products_api.model.OrderStatus;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.repository.IOrderRepository;
import com.formations.spring_products_api.repository.IProductRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

	private final IOrderRepository orderRepository;
	private final IProductRepository productRepository;

	public OrderService(IOrderRepository orderRepository, IProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
	}

	@Transactional
	public Order createOrder(String customerName, String customerEmail, Map<Long, Integer> productsAndQuantities) {
		Order order = new Order(customerName, customerEmail);

		for (Map.Entry<Long, Integer> entry : productsAndQuantities.entrySet()) {
			Long productId = entry.getKey();
			Integer quantity = entry.getValue();

			Product product = productRepository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found: " + productId));

			OrderItem item = new OrderItem(product, quantity);
			order.addItem(item);
		}

		order.calculateTotal();
		return orderRepository.save(order);
	}

	@Transactional
	public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
		order.setStatus(newStatus);
	}

	@Transactional(readOnly = true)
	public Order getOrderWithItems(Long id) {
		return orderRepository.findByIdWithItems(id)
			.orElseThrow(() -> new RuntimeException("Order not found: " + id));
	}

	@Transactional(readOnly = true)
	public List<Order> getAllOrdersWithItems() {
		return orderRepository.findOrdersWithItems();
	}

	@Transactional(readOnly = true)
	public List<Order> getOrdersByCustomerEmail(String email) {
		return orderRepository.findByCustomerEmail(email);
	}

	@Transactional(readOnly = true)
	public List<Order> getOrdersByStatus(OrderStatus status) {
		return orderRepository.findByStatus(status);
	}

	@Transactional
	public Order addItemToOrder(Long orderId, Long productId, int quantity) {
		Order order = orderRepository.findByIdWithItems(orderId)
			.orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new RuntimeException("Product not found: " + productId));

		OrderItem item = new OrderItem(product, quantity);
		order.addItem(item);
		order.calculateTotal();
		
		return orderRepository.save(order);
	}

	@Transactional
	public Order updateItemQuantity(Long orderId, Long itemId, int newQuantity) {
		Order order = orderRepository.findByIdWithItems(orderId)
			.orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

		OrderItem item = order.getItems().stream()
			.filter(i -> i.getId().equals(itemId))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("OrderItem not found: " + itemId));

		item.setQuantity(newQuantity);
		order.calculateTotal();
		
		return orderRepository.save(order);
	}

	@Transactional
	public Order removeItemFromOrder(Long orderId, Long itemId) {
		Order order = orderRepository.findByIdWithItems(orderId)
			.orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

		OrderItem itemToRemove = order.getItems().stream()
			.filter(i -> i.getId().equals(itemId))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("OrderItem not found: " + itemId));

		order.removeItem(itemToRemove);
		order.calculateTotal();
		
		return orderRepository.save(order);
	}

	@Transactional
	public void deleteOrder(Long id) {
		if (!orderRepository.existsById(id)) {
			throw new RuntimeException("Order not found: " + id);
		}
		orderRepository.deleteById(id);
	}
}
