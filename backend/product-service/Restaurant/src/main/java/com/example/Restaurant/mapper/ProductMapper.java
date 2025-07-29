package com.example.Restaurant.mapper;

import com.example.Restaurant.dto.ProductDTO;
import com.example.Restaurant.dto.ProductDetailDTO;
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
                .imageUrl(product.getImageUrl())
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
                .imageUrl(dto.getImageUrl())
                .build();
    }

    public static ProductDetailDTO toDetailDTO(Product product) {
        if (product == null) return null;

        return ProductDetailDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .category(CategoryMapper.toDTO(product.getCategory()))
                .ingredients(product.getIngredients() != null ?
                        product.getIngredients().stream()
                                .map(IngredientMapper::toDTO)
                                .collect(Collectors.toSet())
                        : null)
                .build();
    }
}