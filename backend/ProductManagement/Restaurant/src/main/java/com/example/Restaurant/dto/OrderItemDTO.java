package com.example.Restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {
    private Long id;
    private Long productId; // The ID of the product
    private String productName; // Name of the product for display
    private Integer quantity;
    private BigDecimal priceAtOrder; // Price of the product when the order was placed
}