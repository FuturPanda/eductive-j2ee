package com.formations.spring_products_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "supplier")
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Length(max = 200)
	@Column(nullable = false, length = 200)
	private String name;

	@Email
	@NotBlank
	@Column(nullable = false)
	private String contactEmail;

	private String phone;

	@OneToMany(mappedBy = "supplier")
	private List<Product> products;

	public Supplier() {}

	public Supplier(String name, String contactEmail, String phone) {
		this.name = name;
		this.contactEmail = contactEmail;
		this.phone = phone;
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

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
