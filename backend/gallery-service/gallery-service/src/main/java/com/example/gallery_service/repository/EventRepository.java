package com.example.gallery_service.repository;

import com.example.gallery_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Această metodă va returna o listă de evenimente care aparțin unei anumite categorii,
    // căutând după ID-ul categoriei. Numele metodei este din nou interpretat automat de Spring.
    // Va genera o interogare de tip "SELECT * FROM events WHERE category_id = ?"
    List<Event> findByCategoryId(Long categoryId);
}