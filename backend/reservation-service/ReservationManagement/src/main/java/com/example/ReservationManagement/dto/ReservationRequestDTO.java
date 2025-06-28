package com.example.ReservationManagement.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestDTO {
    private String customerName;
    private String phoneNumber;
    private LocalDateTime reservationDateTime;
    private int numberOfPeople;
}
