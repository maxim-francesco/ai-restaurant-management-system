package com.example.review_service.service;

import com.example.review_service.dto.ReviewRequestDTO;
import com.example.review_service.dto.ReviewResponseDTO;
import com.example.review_service.mapper.ReviewMapper;
import com.example.review_service.model.Review;
import com.example.review_service.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }


    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO requestDTO) {
        Review reviewToSave = ReviewMapper.toEntity(requestDTO);

        Review savedReview = reviewRepository.save(reviewToSave);

        return ReviewMapper.toResponseDTO(savedReview);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getAllReviews(Pageable pageable) {
        Page<Review> reviewsPage = reviewRepository.findAll(pageable);
        return reviewsPage.map(ReviewMapper::toResponseDTO);
    }

}