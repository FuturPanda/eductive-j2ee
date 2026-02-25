package com.formations.spring_products_api.repository;

import com.formations.spring_products_api.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SpringMongoProductRepository extends MongoRepository<Product, String> {

	List<Product> findByCategory(String category);
}
