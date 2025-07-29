package com.example.Restaurant.repository;

import com.example.Restaurant.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
