package com.example.review_service.controller;

import com.example.review_service.config.SecurityConfig;
import com.example.review_service.dto.ReviewRequestDTO;
import com.example.review_service.dto.ReviewResponseDTO;
import com.example.review_service.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// ... alte importuri ...
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

// @WebMvcTest este o adnotare specializata pentru a testa DOAR stratul web (controllerele).
// Porneste un context Spring restrans, doar cu bean-urile necesare pentru MVC.
// Este mai rapid decat @SpringBootTest complet.
@WebMvcTest(ReviewController.class)
@Import(SecurityConfig.class)
class ReviewControllerTest {

    // MockMvc este obiectul principal care ne permite sa simulam cereri HTTP.
    @Autowired
    private MockMvc mockMvc;

    // In loc de @Mock, folosim @MockBean.
    // Ii spune lui Spring: "Gaseste bean-ul de tip ReviewService in context si inlocuieste-l cu un mock".
    @MockBean
    private ReviewService reviewService;

    // Avem nevoie de un ObjectMapper pentru a converti obiectele noastre Java in string-uri JSON.
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenPostReview_thenReturns201Created() throws Exception {
        // ARRANGE (GIVEN)
        // 1. Pregatim datele de intrare si de iesire.
        var requestDTO = new ReviewRequestDTO(
                "Test User",
                "user@test.com",
                "Excellent!",
                10
        );

        var responseDTO = new ReviewResponseDTO(
                1L, // Ne asteptam ca serviciul sa returneze un ID
                "Test User",
                "user@test.com",
                "Excellent!",
                10,
                LocalDateTime.now()
        );

        // 2. Programam comportamentul mock-ului ReviewService.
        // "Data fiind situatia in care reviewService.createReview este apelat cu orice ReviewRequestDTO,
        // atunci sa se returneze obiectul nostru responseDTO."
        given(reviewService.createReview(any(ReviewRequestDTO.class))).willReturn(responseDTO);

        // ACT & ASSERT (WHEN & THEN)
        // Construim si executam cererea HTTP simulata si verificam raspunsul.
        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        // Aici este modificarea cheie!
                        // Adaugam un token CSRF valid in cerere pentru a trece de protectia Spring Security.
                        .with(csrf()))
                .andExpect(status().isCreated()) // Verificam ca statusul HTTP este 201 Created
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test User")))
                .andExpect(jsonPath("$.rating", is(10)));
    }
}