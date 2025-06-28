package com.example.logs_service.service;

import com.example.logs_service.dto.LogResponseDTO;
import com.example.logs_service.mapper.LogMapper;
import com.example.logs_service.model.LogEntry;
import com.example.logs_service.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Marcăm clasa ca fiind un Service, stratul pentru logica de business
public class LogService {

    private final LogEntryRepository logRepository;
    private final LogMapper logMapper;

    @Autowired
    public LogService(LogEntryRepository logRepository, LogMapper logMapper) {
        this.logRepository = logRepository;
        this.logMapper = logMapper;
    }

    public List<LogResponseDTO> getLatestLogs() {
        // 1. Preluăm entitățile din baza de date, sortate
        List<LogEntry> logs = logRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));

        // 2. Folosim mapper-ul pentru a le converti într-o listă de DTO-uri
        return logMapper.toDtoList(logs);
    }
}