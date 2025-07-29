package com.example.Restaurant.service;

import com.example.Restaurant.dto.IngredientDTO;

import java.util.List;

public interface IngredientService {
    List<IngredientDTO> findAll();
    IngredientDTO findById(Long id);
    IngredientDTO create(IngredientDTO dto);
    IngredientDTO update(Long id, IngredientDTO dto);
    void delete(Long id);
}
