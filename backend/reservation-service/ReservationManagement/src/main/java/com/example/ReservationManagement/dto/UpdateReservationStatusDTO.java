package com.example.ReservationManagement.dto;

import com.example.ReservationManagement.model.ReservationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReservationStatusDTO {
    private ReservationStatus status;
}
