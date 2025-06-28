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

import java.util.List;

@RestController // Combina @Controller si @ResponseBody. Spune lui Spring ca aceasta clasa gestioneaza cereri REST
// si ca raspunsurile metodelor vor fi convertite automat in JSON.
@RequestMapping("/api/reviews") // Seteaza un URL de baza pentru toate endpoint-urile din aceasta clasa.
public class ReviewController {

    private final ReviewService reviewService;

    // Injectam dependinta de ReviewService prin constructor.
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Endpoint pentru a crea un review nou
    // Se mapeaza la cereri de tip POST catre /api/reviews
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        ReviewResponseDTO createdReview = reviewService.createReview(reviewRequestDTO);
        // Returnam un raspuns cu statusul HTTP 201 CREATED si review-ul creat in body.
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    // Endpoint pentru a obtine toate review-urile, acum cu suport pentru paginare si sortare
// Se mapeaza la cereri de tip GET catre /api/reviews
    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> getAllReviews(Pageable pageable) {
        Page<ReviewResponseDTO> reviewsPage = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(reviewsPage);
    }
}