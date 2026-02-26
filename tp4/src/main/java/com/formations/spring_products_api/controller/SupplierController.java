package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.model.Supplier;
import com.formations.spring_products_api.service.SupplierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/suppliers")
@Tag(name = "Suppliers")
public class SupplierController {

	private final SupplierService supplierService;

	public SupplierController(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	@GetMapping
	public ResponseEntity<List<Supplier>> getAllSuppliers() {
		return ResponseEntity.ok(supplierService.getAllSuppliers());
	}

	@GetMapping("/count")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(supplierService.count());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Supplier> getSupplier(@PathVariable Long id) {
		return supplierService
			.getSupplierById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Supplier> createSupplier(
		@RequestBody Supplier supplier
	) {
		Supplier created = supplierService.createSupplier(supplier);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(created.getId())
			.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Supplier> updateSupplier(
		@PathVariable Long id,
		@RequestBody Supplier supplier
	) {
		return supplierService
			.updateSupplier(id, supplier)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
		boolean deleted = supplierService.deleteSupplier(id);
		if (!deleted) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
	}
}
