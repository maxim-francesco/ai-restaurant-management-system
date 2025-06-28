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

@Service // Marcheaza aceasta clasa ca un component de tip Service in contextul Spring
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // Folosim injectie prin constructor. Este cea mai recomandata metoda.
    // Spring va "injecta" automat o instanta de ReviewRepository cand creeaza ReviewService.
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * Metoda pentru a crea si salva un review nou.
     * @param requestDTO DTO-ul primit de la client
     * @return DTO-ul de raspuns, continand si datele generate de server (id, submissionDate)
     */
    @Transactional // Asigura ca operatiunea este atomica. Daca apare o eroare, totul se anuleaza.
    public ReviewResponseDTO createReview(ReviewRequestDTO requestDTO) {
        // 1. Convertim DTO-ul de request intr-o entitate folosind mapper-ul.
        Review reviewToSave = ReviewMapper.toEntity(requestDTO);

        // 2. Salvam entitatea in baza de date folosind repository-ul.
        Review savedReview = reviewRepository.save(reviewToSave);

        // 3. Convertim entitatea salvata (care acum are ID) inapoi intr-un DTO de raspuns.
        return ReviewMapper.toResponseDTO(savedReview);
    }

    /**
     * Metoda pentru a obtine o "pagina" de review-uri din baza de date.
     * Accepta un obiect Pageable care contine informatii despre paginare si sortare.
     * @param pageable Obiectul injectat automat de Spring pe baza parametrilor din URL.
     * @return Un obiect Page care contine lista de DTO-uri pentru pagina curenta si metadate.
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponseDTO> getAllReviews(Pageable pageable) {
        // 1. Folosim repository-ul pentru a aduce o pagina de entitati Review.
        // In loc de findAll(), folosim findAll(pageable).
        Page<Review> reviewsPage = reviewRepository.findAll(pageable);

        // 2. Obiectul Page are o metoda .map() foarte utila care ne permite sa convertim
        // continutul sau (o lista de Review) intr-un alt tip (o lista de ReviewResponseDTO).
        // Se aplica functia de mapare pe fiecare element de pe pagina.
        return reviewsPage.map(ReviewMapper::toResponseDTO);
    }

    // Aici vom adauga mai tarziu metode precum getReviewById, updateReview, deleteReview etc.
}