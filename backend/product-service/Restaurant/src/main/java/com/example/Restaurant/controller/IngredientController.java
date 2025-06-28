package com.example.Restaurant.controller;

import com.example.Restaurant.dto.IngredientDTO;
import com.example.Restaurant.events.LogEvent; // NOU: Importăm LogEvent
import com.example.Restaurant.service.IngredientService;
import com.example.Restaurant.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    // Constante pentru RabbitMQ
    private static final String EXCHANGE_NAME = "logs_exchange";
    private static final String ROUTING_KEY_INGREDIENT = "log.ingredient.event";

    // Dependințele necesare
    private final IngredientService ingredientService;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    @GetMapping
    public List<IngredientDTO> getAll() {
        return ingredientService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<IngredientDTO> create(@RequestBody IngredientDTO dto,
                                                @RequestHeader("Authorization") String authHeader) {
        IngredientDTO createdIngredient = ingredientService.create(dto);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a creat un ingredient nou: '%s' (ID: %d).",
                    userName,
                    createdIngredient.getName(),
                    createdIngredient.getId());

            // MODIFICAT: Creăm și trimitem un obiect LogEvent
            LogEvent event = new LogEvent(logMessage, "INGREDIENT", "CREATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_INGREDIENT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de ingredient (create): " + e.getMessage());
        }

        return ResponseEntity.status(201).body(createdIngredient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> update(@PathVariable Long id,
                                                @RequestBody IngredientDTO dto,
                                                @RequestHeader("Authorization") String authHeader) {
        IngredientDTO updatedIngredient = ingredientService.update(id, dto);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a actualizat Ingredientul '%s' (ID: %d).",
                    userName,
                    updatedIngredient.getName(),
                    updatedIngredient.getId());

            // MODIFICAT: Creăm și trimitem un obiect LogEvent
            LogEvent event = new LogEvent(logMessage, "INGREDIENT", "UPDATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_INGREDIENT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de ingredient (update): " + e.getMessage());
        }

        return ResponseEntity.ok(updatedIngredient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            IngredientDTO ingredientToDelete = ingredientService.findById(id);

            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a șters Ingredientul '%s' (ID: %d).",
                    userName,
                    ingredientToDelete.getName(),
                    id);

            // MODIFICAT: Creăm și trimitem un obiect LogEvent
            LogEvent event = new LogEvent(logMessage, "INGREDIENT", "DELETE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_INGREDIENT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de ingredient (delete): " + e.getMessage());
        }

        ingredientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}