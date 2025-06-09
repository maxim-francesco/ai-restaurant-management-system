package com.example.ReservationManagement.mapper;

import com.example.ReservationManagement.dto.*;
import com.example.ReservationManagement.model.Reservation;
import com.example.ReservationManagement.model.ReservationStatus;

public class ReservationMapper {

    // Mapping pentru rezervări făcute de utilizatori - status implicit: PENDING
    public static Reservation toEntity(UserReservationRequestDTO dto) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName(dto.getCustomerName());
        reservation.setPhoneNumber(dto.getPhoneNumber());
        reservation.setReservationDateTime(dto.getReservationDateTime());
        reservation.setNumberOfPeople(dto.getNumberOfPeople());
        reservation.setStatus(ReservationStatus.PENDING); // Status implicit
        return reservation;
    }

    // Mapping pentru rezervări create de admin - status la alegere (CONFIRMED/PENDING/etc.)
    public static Reservation toEntity(AdminReservationRequestDTO dto) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName(dto.getCustomerName());
        reservation.setPhoneNumber(dto.getPhoneNumber());
        reservation.setReservationDateTime(dto.getReservationDateTime());
        reservation.setNumberOfPeople(dto.getNumberOfPeople());
        reservation.setStatus(dto.getStatus() != null ? dto.getStatus() : ReservationStatus.CONFIRMED);
        return reservation;
    }

    // Transformare din entitate în DTO pentru răspunsuri
    public static ReservationResponseDTO toDto(Reservation reservation) {
        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .customerName(reservation.getCustomerName())
                .phoneNumber(reservation.getPhoneNumber())
                .reservationDateTime(reservation.getReservationDateTime())
                .numberOfPeople(reservation.getNumberOfPeople())
                .status(reservation.getStatus())
                .build();
    }

    // Update doar pentru status
    public static void updateStatusFromDto(UpdateReservationStatusDTO dto, Reservation reservation) {
        if (dto.getStatus() != null) {
            reservation.setStatus(dto.getStatus());
        }
    }
}
