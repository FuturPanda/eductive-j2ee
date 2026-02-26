package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.model.Supplier;
import com.formations.spring_products_api.repository.ISupplierRepository;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

	private final ISupplierRepository supplierRepository;

	public SupplierController(ISupplierRepository supplierRepository) {
		this.supplierRepository = supplierRepository;
	}

	@GetMapping
	public ResponseEntity<List<Supplier>> getAllSuppliers() {
		return ResponseEntity.ok(supplierRepository.findAll());
	}

	@GetMapping("/count")
	public ResponseEntity<Long> count() {
		return ResponseEntity.ok(supplierRepository.count());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Supplier> getSupplier(@PathVariable Long id) {
		return supplierRepository.findById(id)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
		Supplier created = supplierRepository.save(supplier);
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
		return supplierRepository.findById(id)
			.map(existing -> {
				existing.setName(supplier.getName());
				existing.setContactEmail(supplier.getContactEmail());
				existing.setPhone(supplier.getPhone());
				return ResponseEntity.ok(supplierRepository.save(existing));
			})
			.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
		if (!supplierRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		supplierRepository.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
