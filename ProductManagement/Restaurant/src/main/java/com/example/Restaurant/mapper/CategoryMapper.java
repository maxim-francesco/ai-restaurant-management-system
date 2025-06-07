package com.example.Restaurant.mapper;

import com.example.Restaurant.dto.CategoryDTO;
import com.example.Restaurant.model.Category;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        if (category == null) return null;

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public static Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;

        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
