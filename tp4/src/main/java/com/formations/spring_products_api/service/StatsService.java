package com.formations.spring_products_api.service;

import com.formations.spring_products_api.dto.CategoryStats;
import com.formations.spring_products_api.dto.OrderStatusCount;
import com.formations.spring_products_api.dto.ProductOrderStats;
import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.repository.ICategoryRepository;
import com.formations.spring_products_api.repository.IOrderRepository;
import com.formations.spring_products_api.repository.IProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StatsService {

	private final IProductRepository productRepository;
	private final IOrderRepository orderRepository;
	private final ICategoryRepository categoryRepository;

	public StatsService(
		IProductRepository productRepository,
		IOrderRepository orderRepository,
		ICategoryRepository categoryRepository
	) {
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.categoryRepository = categoryRepository;
	}

	public List<Object[]> getProductCountByCategory() {
		return productRepository.countByCategory();
	}

	public List<Object[]> getAveragePriceByCategory() {
		return productRepository.averagePriceByCategory();
	}

	public List<Product> getTopExpensiveProducts(int limit) {
		return productRepository
			.findTopExpensive()
			.stream()
			.limit(limit)
			.toList();
	}

	public List<CategoryStats> getCategoryStats() {
		return productRepository.getCategoryStats();
	}

	public BigDecimal getTotalRevenue() {
		return orderRepository.getTotalRevenue();
	}

	public List<Object[]> getOrderCountByStatus() {
		return orderRepository.countByStatus();
	}

	public List<OrderStatusCount> getOrderStatusCounts() {
		return orderRepository.getOrderCountByStatus();
	}

	public List<Object[]> getMostOrderedProducts(int limit) {
		return orderRepository
			.findMostOrderedProducts()
			.stream()
			.limit(limit)
			.toList();
	}

	public List<ProductOrderStats> getMostOrderedProductStats(int limit) {
		return orderRepository
			.getMostOrderedProductStats()
			.stream()
			.limit(limit)
			.toList();
	}

	public List<Product> getNeverOrderedProducts() {
		return productRepository.findNeverOrderedProducts();
	}

	public List<Category> getCategoriesWithMinProducts(int minProducts) {
		return categoryRepository.findCategoriesWithMinProducts(minProducts);
	}
}
