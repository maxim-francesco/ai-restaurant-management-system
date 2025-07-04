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
import org.springframework.web.multipart.MultipartFile; // Import nou

import java.io.IOException; // Import nou
import java.nio.file.Files; // Import nou
import java.nio.file.Path; // Import nou
import java.nio.file.Paths; // Import nou
import java.nio.file.StandardCopyOption; // Import nou
import java.util.List;
import java.util.Set;
import java.util.UUID; // Import nou
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    // ============== START MODIFICARE ==============
    // Define the directory where images will be stored
    // Define the directory where images will be stored
    private final String uploadDir = "/tmp/uploads/product-images/";
    // =============== END MODIFICARE ===============

    @Override
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDetailDTO> findAllWithDetails() {
        // Apelăm metoda optimizată din repository și mapper-ul pentru detalii
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
        existing.setImageUrl(dto.getImageUrl()); // Asigurăm că imageUrl este actualizat la update

        return ProductMapper.toDTO(productRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id) // Preia produsul pentru a-i șterge imaginea
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Delete the associated image file if it exists
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            try {
                Path imagePath = Paths.get(uploadDir).resolve(product.getImageUrl());
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                // Log the error but don't prevent product deletion
                System.err.println("Could not delete image file: " + product.getImageUrl() + " " + e.getMessage());
            }
        }

        productRepository.deleteById(id);
    }

    @Override
    public boolean isIngredientUsed(Long ingredientId) {
        return productRepository.existsByIngredients_Id(ingredientId);
    }

    // ============== START MODIFICARE ==============
    @Override
    public ProductDTO uploadProductImage(Long productId, MultipartFile imageFile) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (imageFile.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        try {
            // Ensure the upload directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique file name
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Copy the file to the target location
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update product's imageUrl
            product.setImageUrl(fileName); // Store just the filename or relative path
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
                product.setImageUrl(null); // Clear the image URL from the product
                productRepository.save(product);
            } catch (IOException e) {
                throw new RuntimeException("Could not delete image file: " + product.getImageUrl(), e);
            }
        }
    }
    // =============== END MODIFICARE ===============


}