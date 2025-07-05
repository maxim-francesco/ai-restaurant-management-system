package com.example.ReservationManagement.service.impl;

import com.example.ReservationManagement.dto.*;
import com.example.ReservationManagement.mapper.ReservationMapper;
import com.example.ReservationManagement.model.Reservation;
import com.example.ReservationManagement.repository.ReservationRepository;
import com.example.ReservationManagement.service.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public ReservationResponseDTO createReservationFromUser(UserReservationRequestDTO dto) {
        Reservation reservation = ReservationMapper.toEntity(dto);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationMapper.toDto(saved);
    }

    @Override
    public ReservationResponseDTO createReservationFromAdmin(AdminReservationRequestDTO dto) {
        Reservation reservation = ReservationMapper.toEntity(dto);
        Reservation saved = reservationRepository.save(reservation);
        return ReservationMapper.toDto(saved);
    }

    @Override
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationResponseDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));
        return ReservationMapper.toDto(reservation);
    }

    @Override
    public ReservationResponseDTO updateStatus(Long id, UpdateReservationStatusDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with ID: " + id));
        ReservationMapper.updateStatusFromDto(dto, reservation);
        Reservation updated = reservationRepository.save(reservation);
        return ReservationMapper.toDto(updated);
    }

    @Override
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
    }
}

