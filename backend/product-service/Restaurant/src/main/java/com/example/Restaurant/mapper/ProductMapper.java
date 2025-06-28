package com.example.Restaurant.mapper;

import com.example.Restaurant.dto.ProductDTO;
import com.example.Restaurant.model.Category;
import com.example.Restaurant.model.Ingredient;
import com.example.Restaurant.model.Product;

import java.util.Set;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductDTO toDTO(Product product) {
        if (product == null) return null;

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .ingredientIds(product.getIngredients() != null ?
                        product.getIngredients().stream()
                                .map(Ingredient::getId)
                                .collect(Collectors.toSet())
                        : null)
                .imageUrl(product.getImageUrl()) // Adăugat câmpul imageUrl
                .build();
    }

    public static Product toEntity(ProductDTO dto, Category category, Set<Ingredient> ingredients) {
        if (dto == null) return null;

        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(category)
                .ingredients(ingredients)
                .imageUrl(dto.getImageUrl()) // Adăugat câmpul imageUrl
                .build();
    }
}