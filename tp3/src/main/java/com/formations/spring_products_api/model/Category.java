package com.formations.spring_products_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Le nom de la catégorie est obligatoire")
	@Size(min = 2, max = 100, message = "Le nom doit contenir entre {min} et {max} caractères")
	@Column(unique = true, length = 100)
	private String name;

	@Size(max = 500, message = "La description ne doit pas dépasser {max} caractères")
	@Column(length = 500)
	private String description;

	@OneToMany(
		mappedBy = "category",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
	@JsonIgnore
	@BatchSize(size = 10)
	private List<Product> products;

	public Category() {
	}

	public Category(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
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

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
