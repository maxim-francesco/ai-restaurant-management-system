package com.example.Restaurant.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private Set<Long> ingredientIds;

    // ============== START MODIFICARE ==============
    private String imageUrl;
    // =============== END MODIFICARE ===============
}