package com.example.review_service.repository;

import com.example.review_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Adnotare ce marcheaza interfata ca un bean de tip Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Magia Spring Data JPA se intampla aici!
    // Momentan, nu este nevoie sa scriem nicio linie de cod in interiorul interfetei.
}