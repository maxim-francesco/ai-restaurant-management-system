package com.example.ReservationManagement.dto;

import com.example.ReservationManagement.model.ReservationStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDTO {
    private Long id;
    private String customerName;
    private String phoneNumber;
    private LocalDateTime reservationDateTime;
    private int numberOfPeople;
    private ReservationStatus status;
}
