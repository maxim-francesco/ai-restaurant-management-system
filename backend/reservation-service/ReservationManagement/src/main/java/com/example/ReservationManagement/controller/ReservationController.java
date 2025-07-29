package com.example.ReservationManagement.controller;

import com.example.ReservationManagement.dto.*;
import com.example.ReservationManagement.events.LogEvent; // NOU: Importăm LogEvent
import com.example.ReservationManagement.service.JwtService;
import com.example.ReservationManagement.service.ReservationService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private static final String EXCHANGE_NAME = "logs_exchange";
    private static final String ROUTING_KEY_RESERVATION = "log.reservation.event";

    private final ReservationService reservationService;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    @Autowired
    public ReservationController(
            ReservationService reservationService,
            RabbitTemplate rabbitTemplate,
            JwtService jwtService) {
        this.reservationService = reservationService;
        this.rabbitTemplate = rabbitTemplate;
        this.jwtService = jwtService;
    }

    @PostMapping("/admin")
    public ResponseEntity<ReservationResponseDTO> createAdminReservation(
            @RequestBody AdminReservationRequestDTO requestDTO,
            @RequestHeader("Authorization") String authHeader) {

        ReservationResponseDTO response = reservationService.createReservationFromAdmin(requestDTO);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a creat Rezervarea #%d pentru clientul '%s'.",
                    userName,
                    response.getId(),
                    response.getCustomerName());

            LogEvent event = new LogEvent(logMessage, "RESERVATION", "CREATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_RESERVATION, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de rezervare (create): " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateReservationStatusDTO statusDTO,
            @RequestHeader("Authorization") String authHeader) {

        reservationService.updateStatus(id, statusDTO);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a actualizat statusul Rezervării #%d la '%s'.",
                    userName,
                    id,
                    statusDTO.getStatus());

            LogEvent event = new LogEvent(logMessage, "RESERVATION", "UPDATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_RESERVATION, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de rezervare (update status): " + e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id,
                                                  @RequestHeader("Authorization") String authHeader) {
        try {
            ReservationResponseDTO reservationToDelete = reservationService.getReservationById(id);
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a șters Rezervarea #%d pentru clientul '%s'.",
                    userName,
                    id,
                    reservationToDelete.getCustomerName());

            LogEvent event = new LogEvent(logMessage, "RESERVATION", "DELETE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_RESERVATION, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de rezervare (delete): " + e.getMessage());
        }

        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/user")
    public ResponseEntity<ReservationResponseDTO> createUserReservation(
            @RequestBody UserReservationRequestDTO requestDTO) {
        ReservationResponseDTO response = reservationService.createReservationFromUser(requestDTO);
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
}