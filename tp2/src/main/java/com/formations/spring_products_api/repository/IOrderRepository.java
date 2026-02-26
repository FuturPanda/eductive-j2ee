package com.formations.spring_products_api.repository;

import com.formations.spring_products_api.dto.OrderStatusCount;
import com.formations.spring_products_api.dto.ProductOrderStats;
import com.formations.spring_products_api.model.Order;
import com.formations.spring_products_api.model.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerEmail(String email);

    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product")
    List<Order> findOrdersWithItems();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    @Query("SELECT oi.product.name, SUM(oi.quantity) FROM OrderItem oi GROUP BY oi.product.name ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findMostOrderedProducts();

    @Query("SELECT NEW com.formations.spring_products_api.dto.OrderStatusCount(o.status, COUNT(o)) FROM Order o GROUP BY o.status")
    List<OrderStatusCount> getOrderCountByStatus();

    @Query("SELECT NEW com.formations.spring_products_api.dto.ProductOrderStats(oi.product.name, SUM(oi.quantity), SUM(oi.subtotal)) FROM OrderItem oi GROUP BY oi.product.name ORDER BY SUM(oi.quantity) DESC")
    List<ProductOrderStats> getMostOrderedProductStats();
}
