package com.example.Restaurant.service.impl;

import com.example.Restaurant.dto.CategoryDTO;
import com.example.Restaurant.mapper.CategoryMapper;
import com.example.Restaurant.model.Category;
import com.example.Restaurant.repository.CategoryRepository;
import com.example.Restaurant.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return CategoryMapper.toDTO(category);
    }

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        Category category = CategoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDTO(saved);
    }

    @Override
    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());

        return CategoryMapper.toDTO(categoryRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
