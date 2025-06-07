package com.example.Restaurant.controller;

import com.example.Restaurant.dto.CategoryDTO;
import com.example.Restaurant.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDTO> getAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryDTO dto) {
        return ResponseEntity.status(201).body(categoryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
