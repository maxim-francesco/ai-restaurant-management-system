package com.example.Restaurant.service;

import com.example.Restaurant.dto.ProductDTO;
import com.example.Restaurant.dto.ProductDetailDTO;
import org.springframework.web.multipart.MultipartFile; // Import nou

import java.util.List;

public interface ProductService {
    List<ProductDTO> findAll();
    ProductDTO findById(Long id);
    ProductDTO create(ProductDTO dto);
    ProductDTO update(Long id, ProductDTO dto);
    void delete(Long id);
    boolean isIngredientUsed(Long ingredientId);

    // ============== START MODIFICARE ==============
    ProductDTO uploadProductImage(Long productId, MultipartFile imageFile);
    void deleteProductImage(Long productId);
    // =============== END MODIFICARE ===============
    List<ProductDetailDTO> findAllWithDetails();
}