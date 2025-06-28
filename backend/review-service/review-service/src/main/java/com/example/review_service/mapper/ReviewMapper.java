package com.example.review_service.mapper;

import com.example.review_service.dto.ReviewRequestDTO;
import com.example.review_service.dto.ReviewResponseDTO;
import com.example.review_service.model.Review;

public class ReviewMapper {

    /**
     * Converteste un DTO de request (primit de la client) intr-o entitate Review
     * care poate fi salvata in baza de date.
     */
    public static Review toEntity(ReviewRequestDTO requestDTO) {
        Review review = new Review();
        review.setName(requestDTO.name());
        review.setEmail(requestDTO.email());
        review.setReviewMessage(requestDTO.reviewMessage());
        review.setRating(requestDTO.rating());
        // ID-ul si data (submissionDate) nu sunt setate aici,
        // ele vor fi generate automat de baza de date si Hibernate.
        return review;
    }

    /**
     * Converteste o entitate Review (din baza de date) intr-un DTO de raspuns
     * care poate fi trimis clientului.
     */
    public static ReviewResponseDTO toResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getName(),
                review.getEmail(),
                review.getReviewMessage(),
                review.getRating(),
                review.getSubmissionDate()
        );
    }
}