package com.example.Restaurant.repository;

import com.example.Restaurant.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByIngredients_Id(Long ingredientId);

}
