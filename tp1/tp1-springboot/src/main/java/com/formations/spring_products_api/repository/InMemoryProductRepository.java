package com.formations.spring_products_api.repository;

import com.formations.spring_products_api.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!prod")
public class InMemoryProductRepository implements IProductRepository {

	private final ConcurrentHashMap<String, Product> repository;

	public InMemoryProductRepository() {
		this.repository = new ConcurrentHashMap<>();
		var product1 = new Product("Product1", new BigDecimal(100), "outils");
		var product2 = new Product(
			"Product2",
			new BigDecimal(200),
			"technology"
		);
		var product3 = new Product(
			"Product3",
			new BigDecimal(1000000),
			"voiture"
		);
		this.repository.put(product1.getId(), product1);
		this.repository.put(product2.getId(), product2);
		this.repository.put(product3.getId(), product3);
	}

	@Override
	public Product save(Product product) {
		this.repository.put(product.getId(), product);
		return product;
	}

	@Override
	public Optional<Product> findById(String id) {
		return Optional.ofNullable(this.repository.get(id));
	}

	@Override
	public List<Product> findAll() {
		return List.copyOf(this.repository.values());
	}

	@Override
	public List<Product> findByCategory(String category) {
		return this.repository.values()
			.stream()
			.filter(product -> product.getCategory().equals(category))
			.toList();
	}

	@Override
	public void delete(String id) {
		this.repository.remove(id);
	}

	@Override
	public boolean exists(String id) {
		return this.repository.containsKey(id);
	}

	@Override
	public long count() {
		return this.repository.size();
	}
}
