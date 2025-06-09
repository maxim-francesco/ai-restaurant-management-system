package com.example.Restaurant.mapper;

import com.example.Restaurant.dto.IngredientDTO;
import com.example.Restaurant.model.Ingredient;

public class IngredientMapper {

    public static IngredientDTO toDTO(Ingredient ingredient) {
        if (ingredient == null) return null;

        return IngredientDTO.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .build();
    }

    public static Ingredient toEntity(IngredientDTO dto) {
        if (dto == null) return null;

        return Ingredient.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
