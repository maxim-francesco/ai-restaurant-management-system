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

    @NotBlank(message = "Numele nu poate fi gol.")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Emailul nu poate fi gol.")
    @Email(message = "Adresa de email nu este valida.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Mesajul recenziei nu poate fi gol.")
    @Column(nullable = false, length = 2000)
    private String reviewMessage;

    @Min(value = 0, message = "Rating-ul trebuie sa fie cel putin 0.")
    @Max(value = 10, message = "Rating-ul trebuie sa fie cel mult 10.")
    @Column(nullable = false)
    private Integer rating;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime submissionDate;

}