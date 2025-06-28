package com.example.Restaurant.controller;

import com.example.Restaurant.dto.CategoryDTO;
import com.example.Restaurant.events.LogEvent; // NOU: Importăm LogEvent
import com.example.Restaurant.service.CategoryService;
import com.example.Restaurant.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    // Constante pentru RabbitMQ
    private static final String EXCHANGE_NAME = "logs_exchange";
    private static final String ROUTING_KEY_CATEGORY = "log.category.event";

    // Dependințele necesare
    private final CategoryService categoryService;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    @GetMapping
    public List<CategoryDTO> getAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> create(@RequestBody CategoryDTO dto,
                                              @RequestHeader("Authorization") String authHeader) {
        // Logica de business
        CategoryDTO createdCategory = categoryService.create(dto);

        // Logica de logging
        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a creat o categorie nouă: '%s' (ID: %d).",
                    userName,
                    createdCategory.getName(),
                    createdCategory.getId());

            // MODIFICAT: Creăm și trimitem un obiect LogEvent
            LogEvent event = new LogEvent(logMessage, "CATEGORY", "CREATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_CATEGORY, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de categorie (create): " + e.getMessage());
        }

        return ResponseEntity.status(201).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable Long id,
                                              @RequestBody CategoryDTO dto,
                                              @RequestHeader("Authorization") String authHeader) {
        // Logica de business
        CategoryDTO updatedCategory = categoryService.update(id, dto);

        // Logica de logging
        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a actualizat Categoria '%s' (ID: %d).",
                    userName,
                    updatedCategory.getName(),
                    updatedCategory.getId());

            // MODIFICAT: Creăm și trimitem un obiect LogEvent
            LogEvent event = new LogEvent(logMessage, "CATEGORY", "UPDATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_CATEGORY, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de categorie (update): " + e.getMessage());
        }

        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader) {
        // Logăm înainte de a șterge pentru a avea acces la date
        try {
            CategoryDTO categoryToDelete = categoryService.findById(id);

            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a șters Categoria '%s' (ID: %d).",
                    userName,
                    categoryToDelete.getName(),
                    id);

            // MODIFICAT: Creăm și trimitem un obiect LogEvent
            LogEvent event = new LogEvent(logMessage, "CATEGORY", "DELETE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_CATEGORY, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de categorie (delete): " + e.getMessage());
        }

        // Executăm logica de business
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}