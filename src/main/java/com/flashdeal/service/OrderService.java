package com.flashdeal.service;

import com.flashdeal.model.Order;
import com.flashdeal.model.OrderStatus;
import com.flashdeal.persistence.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service managing order retrieval, filtering, and revenue aggregation.
 */
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void recordOrder(Order order) {
        orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public List<Order> filterOrders(String productId, OrderStatus status) {
        return orderRepository.filterOrders(productId, status);
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.calculateSuccessfulRevenue();
    }

    public int getOrderCount() {
        return orderRepository.getOrderCount();
    }

    public OrderRepository getRepository() {
        return orderRepository;
    }
}
