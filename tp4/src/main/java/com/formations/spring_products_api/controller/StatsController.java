package com.formations.spring_products_api.controller;

import com.formations.spring_products_api.dto.CategoryStats;
import com.formations.spring_products_api.dto.OrderStatusCount;
import com.formations.spring_products_api.dto.ProductOrderStats;
import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.service.StatsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
@Tag(name = "Stats")
public class StatsController {

	private final StatsService statsService;

	public StatsController(StatsService statsService) {
		this.statsService = statsService;
	}

	@GetMapping("/products/categories/count")
	public ResponseEntity<
		List<Map<String, Object>>
	> getProductCountByCategory() {
		List<Object[]> results = statsService.getProductCountByCategory();
		List<Map<String, Object>> response = results
			.stream()
			.map(r -> {
				Map<String, Object> map = new HashMap<>();
				map.put("categoryName", r[0]);
				map.put("productCount", r[1]);
				return map;
			})
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/products/categories/price")
	public ResponseEntity<
		List<Map<String, Object>>
	> getAveragePriceByCategory() {
		List<Object[]> results = statsService.getAveragePriceByCategory();
		List<Map<String, Object>> response = results
			.stream()
			.map(r -> {
				Map<String, Object> map = new HashMap<>();
				map.put("categoryName", r[0]);
				map.put("averagePrice", r[1]);
				return map;
			})
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/products/price")
	public ResponseEntity<List<Product>> getTopExpensiveProducts(
		@RequestParam(defaultValue = "10") int limit
	) {
		return ResponseEntity.ok(statsService.getTopExpensiveProducts(limit));
	}

	@GetMapping("/categories/stats")
	public ResponseEntity<List<CategoryStats>> getCategoryStats() {
		return ResponseEntity.ok(statsService.getCategoryStats());
	}

	@GetMapping("/orders/revenue/total")
	public ResponseEntity<Map<String, BigDecimal>> getTotalRevenue() {
		Map<String, BigDecimal> response = new HashMap<>();
		response.put("totalRevenue", statsService.getTotalRevenue());
		return ResponseEntity.ok(response);
	}

	@GetMapping("/orders/status/count")
	public ResponseEntity<List<Map<String, Object>>> getOrderCountByStatus() {
		List<Object[]> results = statsService.getOrderCountByStatus();
		List<Map<String, Object>> response = results
			.stream()
			.map(r -> {
				Map<String, Object> map = new HashMap<>();
				map.put("status", r[0]);
				map.put("count", r[1]);
				return map;
			})
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/orders/status-counts")
	public ResponseEntity<List<OrderStatusCount>> getOrderStatusCounts() {
		return ResponseEntity.ok(statsService.getOrderStatusCounts());
	}

	@GetMapping("/orders/most-ordered-products")
	public ResponseEntity<List<Map<String, Object>>> getMostOrderedProducts(
		@RequestParam(defaultValue = "10") int limit
	) {
		List<Object[]> results = statsService.getMostOrderedProducts(limit);
		List<Map<String, Object>> response = results
			.stream()
			.map(r -> {
				Map<String, Object> map = new HashMap<>();
				map.put("productName", r[0]);
				map.put("totalQuantity", r[1]);
				return map;
			})
			.toList();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/orders/product/top")
	public ResponseEntity<List<ProductOrderStats>> getMostOrderedProductStats(
		@RequestParam(defaultValue = "10") int limit
	) {
		return ResponseEntity.ok(
			statsService.getMostOrderedProductStats(limit)
		);
	}

	@GetMapping("/products/never-ordered")
	public ResponseEntity<List<Product>> getNeverOrderedProducts() {
		return ResponseEntity.ok(statsService.getNeverOrderedProducts());
	}

	@GetMapping("/categories/products/min")
	public ResponseEntity<List<Category>> getCategoriesWithMinProducts(
		@RequestParam(defaultValue = "1") int minProducts
	) {
		return ResponseEntity.ok(
			statsService.getCategoriesWithMinProducts(minProducts)
		);
	}
}
