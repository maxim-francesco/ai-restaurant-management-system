package com.example.ReservationManagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    private String phoneNumber;

    private LocalDateTime reservationDateTime;

    private int numberOfPeople;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
}
