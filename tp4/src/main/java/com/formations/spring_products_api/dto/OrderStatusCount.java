package com.formations.spring_products_api.dto;

import com.formations.spring_products_api.model.OrderStatus;

public class OrderStatusCount {

    private OrderStatus status;
    private Long count;

    public OrderStatusCount(OrderStatus status, Long count) {
        this.status = status;
        this.count = count;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
