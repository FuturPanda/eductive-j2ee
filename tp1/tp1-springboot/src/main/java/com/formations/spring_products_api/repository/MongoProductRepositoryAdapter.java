package com.formations.spring_products_api.repository;

import com.formations.spring_products_api.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("prod")
public class MongoProductRepositoryAdapter implements IProductRepository {

	private final SpringMongoProductRepository mongoRepository;

	public MongoProductRepositoryAdapter(
		SpringMongoProductRepository mongoRepository
	) {
		this.mongoRepository = mongoRepository;
	}

	@Override
	public Product save(Product product) {
		return mongoRepository.save(product);
	}

	@Override
	public Optional<Product> findById(String id) {
		return mongoRepository.findById(id);
	}

	@Override
	public List<Product> findAll() {
		return mongoRepository.findAll();
	}

	@Override
	public List<Product> findByCategory(String category) {
		return mongoRepository.findByCategory(category);
	}

	@Override
	public void delete(String id) {
		mongoRepository.deleteById(id);
	}

	@Override
	public boolean exists(String id) {
		return mongoRepository.existsById(id);
	}

	@Override
	public long count() {
		return mongoRepository.count();
	}
}
