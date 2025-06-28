package com.example.review_service.dto;

import java.time.LocalDateTime;

// Acest record contine datele pe care le trimitem inapoi clientului
public record ReviewResponseDTO(
        Long id,
        String name,
        String email,
        String reviewMessage,
        Integer rating,
        LocalDateTime submissionDate
) {}