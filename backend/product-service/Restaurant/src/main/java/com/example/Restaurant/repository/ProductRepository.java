package com.example.Restaurant.repository;// In file: src/main/java/com/example/Restaurant/repository/ProductRepository.java
import com.example.Restaurant.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Metoda nouă pentru a încărca produsele cu toate detaliile (eager loading)
    @Query("SELECT p FROM Product p JOIN FETCH p.category LEFT JOIN FETCH p.ingredients")
    List<Product> findAllWithDetails();

    boolean existsByIngredients_Id(Long ingredientId);
}