package com.example.gallery_service.repository;

import com.example.gallery_service.model.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marchează această interfață ca fiind un bean de tip Repository gestionat de Spring
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {

    // Spring Data JPA va înțelege automat din numele metodei ce trebuie să facă.
    // Această metodă va genera o interogare de tip "SELECT * FROM event_categories WHERE name = ?"
    // și va returna un Optional, care ne protejează de erori de tip NullPointerException.
    Optional<EventCategory> findByName(String name);
}