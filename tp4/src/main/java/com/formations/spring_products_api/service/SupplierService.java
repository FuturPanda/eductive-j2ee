package com.formations.spring_products_api.service;

import com.formations.spring_products_api.model.Supplier;
import com.formations.spring_products_api.repository.ISupplierRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupplierService {

	private final ISupplierRepository supplierRepository;

	public SupplierService(ISupplierRepository supplierRepository) {
		this.supplierRepository = supplierRepository;
	}

	public List<Supplier> getAllSuppliers() {
		return supplierRepository.findAll();
	}

	public Long count() {
		return supplierRepository.count();
	}

	public Optional<Supplier> getSupplierById(Long id) {
		return supplierRepository.findById(id);
	}

	public Supplier createSupplier(Supplier supplier) {
		Supplier created = supplierRepository.save(supplier);
		return created;
	}

	public Optional<Supplier> updateSupplier(Long id, Supplier supplier) {
		return supplierRepository.findById(id).map(existing -> {
			existing.setName(supplier.getName());
			existing.setContactEmail(supplier.getContactEmail());
			existing.setPhone(supplier.getPhone());
			return supplierRepository.save(existing);
		});
	}

	public boolean deleteSupplier(Long id) {
		if (!supplierRepository.existsById(id)) {
			return false;
		}
		supplierRepository.deleteById(id);
		return true;
	}
}
