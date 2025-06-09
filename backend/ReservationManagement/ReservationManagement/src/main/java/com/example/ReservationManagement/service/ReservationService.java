package com.example.ReservationManagement.service;

import com.example.ReservationManagement.dto.*;

import java.util.List;

public interface ReservationService {

    ReservationResponseDTO createReservationFromUser(UserReservationRequestDTO dto);

    ReservationResponseDTO createReservationFromAdmin(AdminReservationRequestDTO dto);

    List<ReservationResponseDTO> getAllReservations();

    ReservationResponseDTO getReservationById(Long id);

    ReservationResponseDTO updateStatus(Long id, UpdateReservationStatusDTO dto);

    void deleteReservation(Long id);
}
