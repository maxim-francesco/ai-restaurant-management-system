package com.example.Restaurant.controller;

import com.example.Restaurant.dto.IngredientDTO;
import com.example.Restaurant.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public List<IngredientDTO> getAll() {
        return ingredientService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientDTO> create(@RequestBody IngredientDTO dto) {
        return ResponseEntity.status(201).body(ingredientService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> update(@PathVariable Long id, @RequestBody IngredientDTO dto) {
        return ResponseEntity.ok(ingredientService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
