package com.example.Restaurant.service;

import com.example.Restaurant.dto.CreateOrderRequest;
import com.example.Restaurant.dto.OrderDTO;
import com.example.Restaurant.model.Order; // Import Order model for potential internal use

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(CreateOrderRequest createOrderRequest);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getAllOrders();
    OrderDTO updateOrder(Long id, OrderDTO orderDTO);
    void deleteOrder(Long id);
}