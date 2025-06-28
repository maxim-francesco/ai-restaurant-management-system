package com.example.logs_service.listener;

import com.example.logs_service.events.LogEvent;
import com.example.logs_service.model.LogEntry;
import com.example.logs_service.repository.LogEntryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.time.ZoneId;

@Component
public class LogEventListener {

    private final LogEntryRepository logRepository;

    public LogEventListener(LogEntryRepository logRepository) {
        this.logRepository = logRepository;
    }

    // MODIFICAT: Metoda primește acum un obiect LogEvent, nu un String
    @RabbitListener(queues = "logs_queue")
    public void handleLogMessage(LogEvent event) {
        System.out.println(
                "### LOG PRIMIT [" + event.getLogType() + "/" + event.getOperationType() + "]: -> " + event.getMessage()
        );

        // Creăm un obiect nou de tip LogEntry
        LogEntry logEntry = new LogEntry();
        logEntry.setMessage(event.getMessage());
        // Convertim java.util.Date din eveniment la LocalDateTime pentru entitate
        logEntry.setTimestamp(event.getTimestamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

        // NOU: Setăm noile câmpuri
        logEntry.setLogType(event.getLogType());
        logEntry.setOperationType(event.getOperationType());

        // Salvăm entitatea completă în baza de date
        logRepository.save(logEntry);

        System.out.println("### Log detaliat salvat cu succes în baza de date!");
    }
}