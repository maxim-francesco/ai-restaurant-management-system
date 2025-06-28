package com.example.review_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Numele nu poate fi gol.") // Valideaza ca nu e null si nu contine doar spatii
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Emailul nu poate fi gol.")
    @Email(message = "Adresa de email nu este valida.") // Valideaza formatul emailului
    @Column(nullable = false, unique = true) // Emailul trebuie sa fie unic in baza de date
    private String email;

    @NotBlank(message = "Mesajul recenziei nu poate fi gol.")
    @Column(nullable = false, length = 2000) // Setam o lungime maxima pentru mesaj
    private String reviewMessage;

    @Min(value = 0, message = "Rating-ul trebuie sa fie cel putin 0.")   // Valideaza valoarea minima
    @Max(value = 10, message = "Rating-ul trebuie sa fie cel mult 10.") // Valideaza valoarea maxima
    @Column(nullable = false)
    private Integer rating;

    @CreationTimestamp // Genereaza automat data si ora la crearea inregistrarii
    @Column(updatable = false, nullable = false)
    private LocalDateTime submissionDate;

}