package com.formations.spring_products_api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {

	@Id
	private String id = UUID.randomUUID().toString();

	private String name;
	private String description;
	private BigDecimal price;
	private String category;
	private int stock = 0;
	private LocalDateTime createdAt;

	public Product(String name, BigDecimal price, String category) {
		this(name, price, category, "");
	}

	public Product(
		String name,
		BigDecimal price,
		String category,
		String description
	) {
		this(name, price, category, description, 0);
	}

	public Product(
		String name,
		BigDecimal price,
		String category,
		String description,
		int stock
	) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
		this.stock = stock;
		this.createdAt = LocalDateTime.now();
	}

	public Product() {
		this.createdAt = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public String getCategory() {
		return category;
	}

	public int getStock() {
		return stock;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
