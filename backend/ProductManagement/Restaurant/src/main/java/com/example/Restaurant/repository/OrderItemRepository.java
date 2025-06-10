package com.example.Restaurant.repository;

import com.example.Restaurant.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // You can add custom query methods here if needed later,
    // e.g., List<OrderItem> findByOrderId(Long orderId);
}