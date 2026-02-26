package com.formations.spring_products_api.repository;

import com.formations.spring_products_api.dto.CategoryStats;
import com.formations.spring_products_api.model.Category;
import com.formations.spring_products_api.model.Product;
import com.formations.spring_products_api.model.Supplier;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {

	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier")
	List<Product> findAllWithRelations();

	List<Product> findByCategory(Category category);

	List<Product> findByCategoryId(Long categoryId);

	List<Product> findBySupplier(Supplier supplier);

	@Query("SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max ORDER BY p.price")
	List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

	@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	List<Product> searchByName(@Param("keyword") String keyword);

	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :id")
	Optional<Product> findByIdWithRelations(@Param("id") Long id);

	@Query("SELECT p.category.name, COUNT(p) FROM Product p WHERE p.category IS NOT NULL GROUP BY p.category.name")
	List<Object[]> countByCategory();

	@Query("SELECT p.category.name, AVG(p.price) FROM Product p WHERE p.category IS NOT NULL GROUP BY p.category.name")
	List<Object[]> averagePriceByCategory();

	@Query("SELECT p FROM Product p ORDER BY p.price DESC")
	List<Product> findTopExpensive();

	@Query("SELECT p FROM Product p WHERE p NOT IN (SELECT oi.product FROM OrderItem oi)")
	List<Product> findNeverOrderedProducts();

	@Query("SELECT NEW com.formations.spring_products_api.dto.CategoryStats(p.category.name, COUNT(p), AVG(p.price)) FROM Product p WHERE p.category IS NOT NULL GROUP BY p.category.name")
	List<CategoryStats> getCategoryStats();

	@EntityGraph(value = "Product.withCategory")
	@Query("SELECT p FROM Product p")
	List<Product> findAllWithCategoryGraph();

	@EntityGraph(value = "Product.full")
	@Query("SELECT p FROM Product p")
	List<Product> findAllWithFullGraph();
}
