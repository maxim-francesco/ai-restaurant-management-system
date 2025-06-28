package com.example.ReservationManagement;

import com.example.ReservationManagement.controller.ReservationController;
import com.example.ReservationManagement.dto.*;
import com.example.ReservationManagement.events.LogEvent;
import com.example.ReservationManagement.model.ReservationStatus;
import com.example.ReservationManagement.service.JwtService;
import com.example.ReservationManagement.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Clasa de test pentru ReservationController.
 * Folosește @WebMvcTest pentru a testa doar layer-ul web (controllerul).
 * Dependențele (servicii, RabbitTemplate) sunt simulate folosind @MockBean.
 * Spring Security este activ, iar testele folosesc @WithMockUser pentru a simula utilizatori autentificați.
 */
@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private JwtService jwtService;

    // DTOs pentru date de test
    private ReservationResponseDTO reservationResponseDTO;
    private AdminReservationRequestDTO adminReservationRequestDTO;
    private UserReservationRequestDTO userReservationRequestDTO;
    private UpdateReservationStatusDTO updateReservationStatusDTO;
    private LocalDateTime testDateTime;

    private static final String EXCHANGE_NAME = "logs_exchange";
    private static final String ROUTING_KEY_RESERVATION = "log.reservation.event";

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        testDateTime = LocalDateTime.of(2025, 10, 15, 20, 0, 0);

        reservationResponseDTO = ReservationResponseDTO.builder()
                .id(1L)
                .customerName("John Doe")
                .phoneNumber("0712345678")
                .reservationDateTime(testDateTime)
                .numberOfPeople(4)
                .status(ReservationStatus.CONFIRMED)
                .build();

        adminReservationRequestDTO = AdminReservationRequestDTO.builder()
                .customerName("John Doe")
                .phoneNumber("0712345678")
                .reservationDateTime(testDateTime)
                .numberOfPeople(4)
                .status(ReservationStatus.CONFIRMED)
                .build();

        userReservationRequestDTO = UserReservationRequestDTO.builder()
                .customerName("Jane Doe")
                .phoneNumber("0787654321")
                .reservationDateTime(testDateTime.plusHours(1))
                .numberOfPeople(2)
                .build();

        updateReservationStatusDTO = UpdateReservationStatusDTO.builder()
                .status(ReservationStatus.CONFIRMED)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simulează un user cu rol ADMIN pentru a trece de filtrul de securitate (evită 403 Forbidden)
    void createAdminReservation_shouldCreateReservationAndLogEvent() throws Exception {
        // Arrange
        String token = "Bearer dummy.jwt.token";
        String adminUsernameForLog = "logAdmin";

        // Simulează extragerea numelui din token, necesară pentru logica din controller
        when(jwtService.extractName("dummy.jwt.token")).thenReturn(adminUsernameForLog);
        when(reservationService.createReservationFromAdmin(any(AdminReservationRequestDTO.class))).thenReturn(reservationResponseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/reservations/admin")
                        .header("Authorization", token) // Header-ul este necesar pentru logica internă a controllerului
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminReservationRequestDTO)))
                .andExpect(status().isOk());

        // Verifică interacțiunile
        verify(reservationService, times(1)).createReservationFromAdmin(any(AdminReservationRequestDTO.class));
        verify(jwtService, times(1)).extractName("dummy.jwt.token");

        ArgumentCaptor<LogEvent> captor = ArgumentCaptor.forClass(LogEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(EXCHANGE_NAME), eq(ROUTING_KEY_RESERVATION), captor.capture());

        String expectedLogMessage = String.format(
                "Utilizatorul '%s' a creat Rezervarea #%d pentru clientul '%s'.",
                adminUsernameForLog,
                reservationResponseDTO.getId(),
                reservationResponseDTO.getCustomerName());
        assertEquals(expectedLogMessage, captor.getValue().getMessage());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Asigură accesul la endpoint
    void updateStatus_shouldUpdateStatusAndLogEvent() throws Exception {
        // Arrange
        Long reservationId = 1L;
        String token = "Bearer dummy.jwt.token";
        String adminUsernameForLog = "logAdmin";

        when(jwtService.extractName("dummy.jwt.token")).thenReturn(adminUsernameForLog);
        // Conform controller-ului furnizat, metoda 'updateStatus' din serviciu este 'void'.
        // Deci, folosirea 'doNothing()' este corectă.
        doNothing().when(reservationService).updateStatus(anyLong(), any(UpdateReservationStatusDTO.class));

        // Act & Assert
        mockMvc.perform(put("/api/reservations/{id}/status", reservationId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReservationStatusDTO)))
                .andExpect(status().isNoContent());

        // Verifică
        verify(reservationService, times(1)).updateStatus(eq(reservationId), any(UpdateReservationStatusDTO.class));
        verify(jwtService, times(1)).extractName("dummy.jwt.token");

        ArgumentCaptor<LogEvent> captor = ArgumentCaptor.forClass(LogEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(EXCHANGE_NAME), eq(ROUTING_KEY_RESERVATION), captor.capture());

        String expectedLogMessage = String.format(
                "Utilizatorul '%s' a actualizat statusul Rezervării #%d la '%s'.",
                adminUsernameForLog,
                reservationId,
                updateReservationStatusDTO.getStatus()
        );
        assertEquals(expectedLogMessage, captor.getValue().getMessage());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteReservation_shouldDeleteReservationAndLogEvent() throws Exception {
        // Arrange
        Long reservationId = 1L;
        String token = "Bearer dummy.jwt.token";
        String adminUsernameForLog = "logAdmin";

        when(reservationService.getReservationById(reservationId)).thenReturn(reservationResponseDTO);
        when(jwtService.extractName("dummy.jwt.token")).thenReturn(adminUsernameForLog);
        doNothing().when(reservationService).deleteReservation(reservationId);

        // Act & Assert
        mockMvc.perform(delete("/api/reservations/{id}", reservationId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        // Verifică
        verify(reservationService, times(1)).getReservationById(reservationId);
        verify(reservationService, times(1)).deleteReservation(reservationId);
        verify(jwtService, times(1)).extractName("dummy.jwt.token");

        ArgumentCaptor<LogEvent> captor = ArgumentCaptor.forClass(LogEvent.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(EXCHANGE_NAME), eq(ROUTING_KEY_RESERVATION), captor.capture());
    }

    @Test
    @WithMockUser // Chiar dacă endpoint-ul nu necesită rol, are nevoie de autentificare
    void createUserReservation_shouldCreateReservationWithoutLogging() throws Exception {
        // Arrange
        ReservationResponseDTO userResponse = ReservationResponseDTO.builder().id(2L).customerName("Jane Doe").build();
        when(reservationService.createReservationFromUser(any(UserReservationRequestDTO.class))).thenReturn(userResponse);

        // Act & Assert
        mockMvc.perform(post("/api/reservations/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReservationRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.customerName").value("Jane Doe"));

        verify(reservationService, times(1)).createReservationFromUser(any(UserReservationRequestDTO.class));
        verifyNoInteractions(jwtService);
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    @WithMockUser // Asigură trecerea de filtrul de securitate (evită 401 Unauthorized)
    void getAllReservations_shouldReturnListOfReservations() throws Exception {
        // Arrange
        List<ReservationResponseDTO> reservations = Collections.singletonList(reservationResponseDTO);
        when(reservationService.getAllReservations()).thenReturn(reservations);

        // Act & Assert
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    @WithMockUser // Asigură trecerea de filtrul de securitate
    void getReservationById_shouldReturnReservation() throws Exception {
        // Arrange
        Long reservationId = 1L;
        when(reservationService.getReservationById(reservationId)).thenReturn(reservationResponseDTO);

        // Act & Assert
        mockMvc.perform(get("/api/reservations/{id}", reservationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservationResponseDTO.getId()));

        verify(reservationService, times(1)).getReservationById(reservationId);
    }
}