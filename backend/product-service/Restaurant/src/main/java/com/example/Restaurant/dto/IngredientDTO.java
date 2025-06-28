package com.example.Restaurant.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDTO {
    private Long id;
    private String name;
}
