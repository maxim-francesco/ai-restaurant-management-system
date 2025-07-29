package com.example.review_service.controller;

import com.example.review_service.dto.ReviewRequestDTO;
import com.example.review_service.dto.ReviewResponseDTO;
import com.example.review_service.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO createdReview = reviewService.createReview(reviewRequestDTO);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviews(Pageable pageable) {
        Page<ReviewResponseDTO> reviewsPage = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviewsPage);
    }
}