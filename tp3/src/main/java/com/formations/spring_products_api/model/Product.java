package com.formations.spring_products_api.model;

import com.formations.spring_products_api.validation.ValidSKU;
import com.formations.spring_products_api.validation.ValidPrice;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "Product.withCategory",
		attributeNodes = @NamedAttributeNode("category")
	),
	@NamedEntityGraph(
		name = "Product.full",
		attributeNodes = {
			@NamedAttributeNode("category"),
			@NamedAttributeNode("supplier")
		}
	)
})
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Le nom du produit est obligatoire")
	@Size(min = 2, max = 200, message = "Le nom doit contenir entre {min} et {max} caractères")
	@Column(nullable = false, length = 200)
	private String name;

	@Size(max = 1000, message = "La description ne doit pas dépasser {max} caractères")
	@Column(length = 1000)
	private String description;

	@NotNull(message = "Le prix est obligatoire")
	@DecimalMin(value = "0.01", message = "Le prix doit être au minimum de {value}")
	@Digits(integer = 8, fraction = 2, message = "Le prix doit avoir au maximum {integer} chiffres entiers et {fraction} décimales")
	@ValidPrice
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;

	@NotNull(message = "Le stock est obligatoire")
	@Min(value = 0, message = "Le stock ne peut pas être négatif")
	@Column(nullable = false)
	private Integer stock = 0;

	@ValidSKU
	@Column(length = 10)
	private String sku;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@NotNull(message = "La catégorie est obligatoire")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Product() {
	}

	public Product(String name, BigDecimal price) {
		this.name = name;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
