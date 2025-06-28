package com.example.Restaurant.repository;

import com.example.Restaurant.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // You can add custom query methods here if needed later,
    // e.g., List<Order> findByCustomerName(String customerName);
}