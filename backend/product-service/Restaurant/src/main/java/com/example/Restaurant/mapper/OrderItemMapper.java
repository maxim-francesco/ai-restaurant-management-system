package com.example.Restaurant.mapper;

import com.example.Restaurant.dto.OrderItemDTO;
import com.example.Restaurant.dto.OrderItemRequestDTO;
import com.example.Restaurant.model.OrderItem;
import com.example.Restaurant.model.Product;
import com.example.Restaurant.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderItemMapper {

    private final ProductRepository productRepository;

    public OrderItemMapper(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public OrderItemDTO toDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemDTO.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .priceAtOrder(orderItem.getPriceAtOrder())
                .build();
    }

    public List<OrderItemDTO> toDtoList(List<OrderItem> orderItems) {
        if (orderItems == null) {
            return null;
        }
        return orderItems.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderItem toEntity(OrderItemRequestDTO orderItemRequestDTO) {
        if (orderItemRequestDTO == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderItemRequestDTO.getQuantity());

        Product product = productRepository.findById(orderItemRequestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + orderItemRequestDTO.getProductId()));

        orderItem.setProduct(product);
        orderItem.setPriceAtOrder(product.getPrice());

        return orderItem;
    }

    public List<OrderItem> toEntityList(List<OrderItemRequestDTO> orderItemRequestDTOs) {
        if (orderItemRequestDTOs == null) {
            return null;
        }
        return orderItemRequestDTOs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}