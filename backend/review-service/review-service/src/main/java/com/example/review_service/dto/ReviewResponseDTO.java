package com.example.review_service.dto;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        String name,
        String email,
        String reviewMessage,
        Integer rating,
        LocalDateTime submissionDate
) {}