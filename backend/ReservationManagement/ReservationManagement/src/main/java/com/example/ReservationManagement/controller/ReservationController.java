package com.example.ReservationManagement.controller;

import com.example.ReservationManagement.dto.*;
import com.example.ReservationManagement.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // Endpoint pentru utilizator
    @PostMapping("/user")
    public ResponseEntity<ReservationResponseDTO> createUserReservation(
            @RequestBody UserReservationRequestDTO requestDTO) {
        ReservationResponseDTO response = reservationService.createReservationFromUser(requestDTO);
        return ResponseEntity.ok(response);
    }

    // Endpoint pentru admin
    @PostMapping("/admin")
    public ResponseEntity<ReservationResponseDTO> createAdminReservation(
            @RequestBody AdminReservationRequestDTO requestDTO) {
        ReservationResponseDTO response = reservationService.createReservationFromAdmin(requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateReservationStatusDTO statusDTO) {
        reservationService.updateStatus(id, statusDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
