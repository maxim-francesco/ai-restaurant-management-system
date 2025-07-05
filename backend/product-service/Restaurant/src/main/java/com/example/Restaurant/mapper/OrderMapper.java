package com.example.Restaurant.mapper;

import com.example.Restaurant.dto.CreateOrderRequest;
import com.example.Restaurant.dto.OrderDTO;
import com.example.Restaurant.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public OrderDTO toDto(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .customerName(order.getCustomerName())
                .customerAddress(order.getCustomerAddress())
                .orderItems(orderItemMapper.toDtoList(order.getOrderItems()))
                .build();
    }

    public List<OrderDTO> toDtoList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Order toEntity(CreateOrderRequest createOrderRequest) {
        if (createOrderRequest == null) {
            return null;
        }
        return Order.builder()
                .customerName(createOrderRequest.getCustomerName())
                .customerAddress(createOrderRequest.getCustomerAddress())
                .orderDate(LocalDateTime.now())
                .build();
    }
}