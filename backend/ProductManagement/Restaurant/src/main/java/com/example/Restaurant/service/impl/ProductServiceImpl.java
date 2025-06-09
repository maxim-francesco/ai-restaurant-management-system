package com.example.Restaurant.service.impl;

import com.example.Restaurant.dto.ProductDTO;
import com.example.Restaurant.mapper.ProductMapper;
import com.example.Restaurant.model.Category;
import com.example.Restaurant.model.Ingredient;
import com.example.Restaurant.model.Product;
import com.example.Restaurant.repository.CategoryRepository;
import com.example.Restaurant.repository.IngredientRepository;
import com.example.Restaurant.repository.ProductRepository;
import com.example.Restaurant.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    @Override
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductMapper.toDTO(product);
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        // Fetch category by ID
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Fetch all ingredients by IDs
        Set<Ingredient> ingredients = ingredientRepository.findAllById(dto.getIngredientIds())
                .stream().collect(Collectors.toSet());

        // Convert DTO to entity
        Product product = ProductMapper.toEntity(dto, category, ingredients);

        // Save entity
        Product saved = productRepository.save(product);

        return ProductMapper.toDTO(saved);
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Set<Ingredient> ingredients = ingredientRepository.findAllById(dto.getIngredientIds())
                .stream().collect(Collectors.toSet());

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setCategory(category);
        existing.setIngredients(ingredients);

        return ProductMapper.toDTO(productRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public boolean isIngredientUsed(Long ingredientId) {
        return productRepository.existsByIngredients_Id(ingredientId);
    }

}
