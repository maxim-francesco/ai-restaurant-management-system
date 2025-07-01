package com.example.Restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;

    // Am înlocuit ID-urile cu obiectele DTO complete
    private CategoryDTO category;
    private Set<IngredientDTO> ingredients;
}