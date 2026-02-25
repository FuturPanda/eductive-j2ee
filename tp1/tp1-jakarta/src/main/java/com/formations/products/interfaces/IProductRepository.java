package com.formations.products.interfaces;

import com.formations.products.model.Product;
import java.util.List;
import java.util.Optional;

public interface IProductRepository {
	public Product save(Product product);
	public Optional<Product> findById(String id);
	public List<Product> findAll();
	public List<Product> findByCategory(String category);
	public void delete(String id);
	public boolean exists(String id);
	public long count();
}
