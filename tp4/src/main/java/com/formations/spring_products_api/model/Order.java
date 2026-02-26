package com.formations.spring_products_api.model;

import com.formations.spring_products_api.validation.ValidDateRange;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@ValidDateRange
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String orderNumber;

	@NotBlank(message = "Le nom du client est obligatoire")
	@Size(min = 2, max = 100, message = "Le nom du client doit contenir entre {min} et {max} caractères")
	@Column(nullable = false)
	private String customerName;

	@NotBlank(message = "L'email du client est obligatoire")
	@Email(message = "L'email doit être une adresse valide")
	private String customerEmail;

	@NotNull(message = "Le statut de la commande est obligatoire")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;

	@NotNull(message = "Le montant total est obligatoire")
	@DecimalMin(value = "0.01", message = "Le montant total doit être au minimum de {value}")
	@Column(precision = 12, scale = 2)
	private BigDecimal totalAmount;

	@PastOrPresent(message = "La date de commande ne peut pas être dans le futur")
	private LocalDateTime orderDate;

	@FutureOrPresent(message = "La date de livraison ne peut pas être dans le passé")
	private LocalDateTime deliveryDate;

	@NotEmpty(message = "La commande doit contenir au moins un article")
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	public Order() {
	}

	public Order(String customerName, String customerEmail) {
		this.customerName = customerName;
		this.customerEmail = customerEmail;
	}

	@PrePersist
	protected void onCreate() {
		this.orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		this.orderDate = LocalDateTime.now();
		this.status = OrderStatus.PENDING;
		if (this.totalAmount == null) {
			this.totalAmount = BigDecimal.ZERO;
		}
	}

	public void addItem(OrderItem item) {
		items.add(item);
		item.setOrder(this);
	}

	public void removeItem(OrderItem item) {
		items.remove(item);
		item.setOrder(null);
	}

	public void calculateTotal() {
		this.totalAmount = items.stream()
			.map(OrderItem::getSubtotal)
			.filter(subtotal -> subtotal != null)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public LocalDateTime getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(LocalDateTime deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
}
