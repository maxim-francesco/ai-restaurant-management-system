package com.example.Restaurant.service.impl;

import com.example.Restaurant.dto.ProductDTO;
import com.example.Restaurant.dto.ProductDetailDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    private final String uploadDir = "/tmp/uploads/product-images/";

    @Override
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDetailDTO> findAllWithDetails() {
        return productRepository.findAllWithDetails().stream()
                .map(ProductMapper::toDetailDTO)
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
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Set<Ingredient> ingredients = ingredientRepository.findAllById(dto.getIngredientIds())
                .stream().collect(Collectors.toSet());

        Product product = ProductMapper.toEntity(dto, category, ingredients);

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
        existing.setImageUrl(dto.getImageUrl());

        return ProductMapper.toDTO(productRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                Path imagePath = Paths.get(uploadDir).resolve(product.getImageUrl());
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.err.println("Could not delete image file: " + product.getImageUrl() + " " + e.getMessage());
            }
        }

        productRepository.deleteById(id);
    }

    @Override
    public boolean isIngredientUsed(Long ingredientId) {
        return productRepository.existsByIngredients_Id(ingredientId);
    }

    @Override
    public ProductDTO uploadProductImage(Long productId, MultipartFile imageFile) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (imageFile.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            product.setImageUrl(fileName);
            Product updatedProduct = productRepository.save(product);

            return ProductMapper.toDTO(updatedProduct);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store image file", e);
        }
    }

    @Override
    public void deleteProductImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                Path imagePath = Paths.get(uploadDir).resolve(product.getImageUrl());
                Files.deleteIfExists(imagePath);
                product.setImageUrl(null);
                productRepository.save(product);
            } catch (IOException e) {
                throw new RuntimeException("Could not delete image file: " + product.getImageUrl(), e);
            }
        }
    }


}