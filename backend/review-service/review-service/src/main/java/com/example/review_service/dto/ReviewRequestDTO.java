package com.example.review_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

// Acest record contine DOAR datele pe care le asteptam de la client
public record ReviewRequestDTO(
        @NotBlank(message = "Numele nu poate fi gol.")
        String name,

        @NotBlank(message = "Emailul nu poate fi gol.")
        @Email(message = "Adresa de email nu este valida.")
        String email,

        @NotBlank(message = "Mesajul recenziei nu poate fi gol.")
        String reviewMessage,

        @Min(value = 0, message = "Rating-ul trebuie sa fie cel putin 0.")
        @Max(value = 10, message = "Rating-ul trebuie sa fie cel mult 10.")
        Integer rating
) {}