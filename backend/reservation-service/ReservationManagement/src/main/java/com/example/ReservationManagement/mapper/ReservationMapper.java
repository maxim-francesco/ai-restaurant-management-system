package com.example.ReservationManagement.mapper;

import com.example.ReservationManagement.dto.*;
import com.example.ReservationManagement.model.Reservation;
import com.example.ReservationManagement.model.ReservationStatus;

public class ReservationMapper {

    public static Reservation toEntity(UserReservationRequestDTO dto) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName(dto.getCustomerName());
        reservation.setPhoneNumber(dto.getPhoneNumber());
        reservation.setReservationDateTime(dto.getReservationDateTime());
        reservation.setNumberOfPeople(dto.getNumberOfPeople());
        reservation.setStatus(ReservationStatus.PENDING); // Status implicit
        return reservation;
    }

    public static Reservation toEntity(AdminReservationRequestDTO dto) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName(dto.getCustomerName());
        reservation.setPhoneNumber(dto.getPhoneNumber());
        reservation.setReservationDateTime(dto.getReservationDateTime());
        reservation.setNumberOfPeople(dto.getNumberOfPeople());
        reservation.setStatus(dto.getStatus() != null ? dto.getStatus() : ReservationStatus.CONFIRMED);
        return reservation;
    }

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

    public static void updateStatusFromDto(UpdateReservationStatusDTO dto, Reservation reservation) {
        if (dto.getStatus() != null) {
            reservation.setStatus(dto.getStatus());
        }
    }
}
