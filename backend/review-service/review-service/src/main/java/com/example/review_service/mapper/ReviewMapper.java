package com.example.review_service.mapper;

import com.example.review_service.dto.ReviewRequestDTO;
import com.example.review_service.dto.ReviewResponseDTO;
import com.example.review_service.model.Review;

public class ReviewMapper {

    public static Review toEntity(ReviewRequestDTO requestDTO) {
        Review review = new Review();
        review.setName(requestDTO.name());
        review.setEmail(requestDTO.email());
        review.setReviewMessage(requestDTO.reviewMessage());
        review.setRating(requestDTO.rating());
        return review;
    }

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