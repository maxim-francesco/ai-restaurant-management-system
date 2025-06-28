package com.example.review_service.service;

import com.example.review_service.dto.ReviewRequestDTO;
import com.example.review_service.dto.ReviewResponseDTO;
import com.example.review_service.model.Review;
import com.example.review_service.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Activeaza integrarea cu Mockito pentru a gestiona adnotarile @Mock si @InjectMocks
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    // Cream o instanta FALSA (un "mock") a repository-ului.
    // Aceasta nu se va conecta la nicio baza de date.
    @Mock
    private ReviewRepository reviewRepository;

    // Injectam mock-ul de mai sus in clasa pe care vrem sa o testam.
    // Spring nu este implicat aici, Mockito face injectia.
    @InjectMocks
    private ReviewService reviewService;

    private Review review;
    private ReviewRequestDTO reviewRequestDTO;

    // Aceasta metoda, adnotata cu @BeforeEach, se va executa inainte de FIECARE test.
    // Este perfecta pentru a pregati obiectele de care avem nevoie in teste.
    @BeforeEach
    void setUp() {
        reviewRequestDTO = new ReviewRequestDTO(
                "Tester",
                "tester@email.com",
                "Un test bun!",
                10
        );

        review = new Review();
        review.setId(1L);
        review.setName("Tester");
        review.setEmail("tester@email.com");
        review.setReviewMessage("Un test bun!");
        review.setRating(10);
        review.setSubmissionDate(LocalDateTime.now());
    }


    // Adnotarea @Test marcheaza aceasta metoda ca fiind un test executabil.
    @Test
    void whenCreateReview_shouldReturnReviewResponseDTO() {
        // ARRANGE (sau GIVEN) - Pregatirea scenariului
        // Ii spunem lui Mockito: "Cand metoda 'save' de pe mock-ul reviewRepository
        // este apelata cu ORICE obiect de tip Review, atunci sa returnezi obiectul nostru 'review'".
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // ACT (sau WHEN) - Executarea metodei pe care o testam
        ReviewResponseDTO savedReviewDTO = reviewService.createReview(reviewRequestDTO);

        // ASSERT (sau THEN) - Verificarea rezultatelor
        // Verificam ca DTO-ul returnat nu este null.
        assertThat(savedReviewDTO).isNotNull();
        // Verificam ca ID-ul a fost asignat corect.
        assertThat(savedReviewDTO.id()).isEqualTo(1L);
        // Verificam ca numele corespunde.
        assertThat(savedReviewDTO.name()).isEqualTo("Tester");
    }
}