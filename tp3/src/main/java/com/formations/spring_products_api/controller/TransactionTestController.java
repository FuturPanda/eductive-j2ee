package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.service.TransactionTestService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/transactions")
public class TransactionTestController {

	private final TransactionTestService transactionTestService;

	public TransactionTestController(TransactionTestService transactionTestService) {
		this.transactionTestService = transactionTestService;
	}

	@PostMapping("/success")
	public ResponseEntity<Map<String, Object>> testSuccessfulTransaction() {
		long countBefore = transactionTestService.countProducts();
		Product created = transactionTestService.createProductSuccess();
		long countAfter = transactionTestService.countProducts();

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Transaction committed successfully");
		response.put("countBefore", countBefore);
		response.put("countAfter", countAfter);
		response.put("createdProduct", created);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/rollback")
	public ResponseEntity<Map<String, Object>> testRollback() {
		long countBefore = transactionTestService.countProducts();
		
		try {
			transactionTestService.testRollback();
		} catch (RuntimeException e) {
			long countAfter = transactionTestService.countProducts();
			
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Transaction rolled back as expected");
			response.put("exceptionMessage", e.getMessage());
			response.put("countBefore", countBefore);
			response.put("countAfter", countAfter);
			response.put("rollbackSuccessful", countBefore == countAfter);
			return ResponseEntity.ok(response);
		}
		
		Map<String, Object> response = new HashMap<>();
		response.put("message", "ERROR: Exception was not thrown");
		return ResponseEntity.internalServerError().body(response);
	}

	@PostMapping("/auto-update/{id}")
	public ResponseEntity<Map<String, Object>> testAutoUpdate(@PathVariable Long id) {
		Product updated = transactionTestService.testAutoUpdate(id);
		
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Product updated automatically (dirty checking)");
		response.put("updatedProduct", updated);
		response.put("note", "No explicit save() call needed - Hibernate auto-flushes changes at transaction commit");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/count")
	public ResponseEntity<Map<String, Object>> getCount() {
		Map<String, Object> response = new HashMap<>();
		response.put("productCount", transactionTestService.countProducts());
		return ResponseEntity.ok(response);
	}
}
