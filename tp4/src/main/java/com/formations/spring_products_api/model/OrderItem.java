package com.formations.spring_products_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@JsonIgnore
	private Order order;

	@NotNull(message = "Le produit est obligatoire")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@NotNull(message = "La quantité est obligatoire")
	@Min(value = 1, message = "La quantité doit être au minimum de {value}")
	@Max(value = 1000, message = "La quantité ne peut pas dépasser {value}")
	@Column(nullable = false)
	private Integer quantity;

	@NotNull(message = "Le prix unitaire est obligatoire")
	@DecimalMin(value = "0.01", message = "Le prix unitaire doit être au minimum de {value}")
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@Column(precision = 10, scale = 2)
	private BigDecimal subtotal;

	public OrderItem() {
	}

	public OrderItem(Product product, int quantity) {
		this.product = product;
		this.quantity = quantity;
		this.unitPrice = product.getPrice();
		calculateSubtotal();
	}

	@PrePersist
	@PreUpdate
	protected void calculateSubtotal() {
		if (unitPrice != null && quantity != null && quantity > 0) {
			this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
		calculateSubtotal();
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
		calculateSubtotal();
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
}
