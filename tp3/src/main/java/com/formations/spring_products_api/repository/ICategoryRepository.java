package com.formations.spring_products_api.repository;

import com.formations.spring_products_api.model.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);

    @Query("SELECT c FROM Category c WHERE (SELECT COUNT(p) FROM Product p WHERE p.category = c) >= :minProducts")
    List<Category> findCategoriesWithMinProducts(@Param("minProducts") long minProducts);
}
