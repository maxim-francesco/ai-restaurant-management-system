package com.example.ReservationManagement.dto;

import com.example.ReservationManagement.model.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminReservationRequestDTO {
    private String customerName;
    private String phoneNumber;
    private LocalDateTime reservationDateTime;
    private int numberOfPeople;
    private ReservationStatus status; // poate fi CONFIRMED sau PENDING
}
