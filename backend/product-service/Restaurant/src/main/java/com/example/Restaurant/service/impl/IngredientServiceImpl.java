package com.example.Restaurant.service.impl;

import com.example.Restaurant.dto.IngredientDTO;
import com.example.Restaurant.mapper.IngredientMapper;
import com.example.Restaurant.model.Ingredient;
import com.example.Restaurant.repository.IngredientRepository;
import com.example.Restaurant.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    @Override
    public List<IngredientDTO> findAll() {
        return ingredientRepository.findAll().stream()
                .map(IngredientMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public IngredientDTO findById(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        return IngredientMapper.toDTO(ingredient);
    }

    @Override
    public IngredientDTO create(IngredientDTO dto) {
        Ingredient ingredient = IngredientMapper.toEntity(dto);
        Ingredient saved = ingredientRepository.save(ingredient);
        return IngredientMapper.toDTO(saved);
    }

    @Override
    public IngredientDTO update(Long id, IngredientDTO dto) {
        Ingredient existing = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        existing.setName(dto.getName());

        return IngredientMapper.toDTO(ingredientRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        ingredientRepository.deleteById(id);
    }
}
