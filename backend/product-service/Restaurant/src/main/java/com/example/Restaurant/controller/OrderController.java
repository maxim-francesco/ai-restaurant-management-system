package com.example.Restaurant.controller;

import com.example.Restaurant.dto.CreateOrderRequest;
import com.example.Restaurant.dto.OrderDTO;
import com.example.Restaurant.events.LogEvent;
import com.example.Restaurant.service.JwtService;
import com.example.Restaurant.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    // Constante pentru RabbitMQ
    private static final String EXCHANGE_NAME = "logs_exchange";
    private static final String ROUTING_KEY_ORDER = "log.order.event";

    // Dependințele necesare
    private final OrderService orderService;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    // Constructor cu toate dependințele
    public OrderController(OrderService orderService, RabbitTemplate rabbitTemplate, JwtService jwtService) {
        this.orderService = orderService;
        this.rabbitTemplate = rabbitTemplate;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest createOrderRequest,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            // Logica de business
            OrderDTO createdOrder = orderService.createOrder(createOrderRequest);

            // Logica de logging
            try {
                final String token = authHeader.substring(7);
                final String userName = jwtService.extractName(token);
                String logMessage = String.format(
                        "Utilizatorul '%s' a creat o comandă nouă cu ID-ul: %d.",
                        userName,
                        createdOrder.getId());

                LogEvent event = new LogEvent(logMessage, "ORDER", "CREATE");
                rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_ORDER, event);

            } catch (Exception logEx) {
                System.err.println("### Eroare la trimiterea log-ului de comandă (create): " + logEx.getMessage());
            }

            return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id,
                                                @RequestBody OrderDTO orderDTO,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            // Logica de business
            OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);

            // --- Logica de logging MODIFICATĂ ---
            try {
                final String token = authHeader.substring(7);
                final String userName = jwtService.extractName(token);

                // MODIFICAT: Am schimbat mesajul pentru a folosi câmpuri existente
                String logMessage = String.format(
                        "Utilizatorul '%s' a actualizat comanda cu ID-ul %d. Noul total este: %.2f.",
                        userName,
                        updatedOrder.getId(),
                        updatedOrder.getTotalAmount()); // Folosim suma totală

                LogEvent event = new LogEvent(logMessage, "ORDER", "UPDATE");
                rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_ORDER, event);

            } catch (Exception logEx) {
                System.err.println("### Eroare la trimiterea log-ului de comandă (update): " + logEx.getMessage());
            }

            return ResponseEntity.ok(updatedOrder);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            // Logica de logging (înainte de ștergere)
            try {
                final String token = authHeader.substring(7);
                final String userName = jwtService.extractName(token);
                String logMessage = String.format(
                        "Utilizatorul '%s' a șters comanda cu ID-ul: %d.",
                        userName,
                        id);

                LogEvent event = new LogEvent(logMessage, "ORDER", "DELETE");
                rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_ORDER, event);

            } catch (Exception logEx) {
                System.err.println("### Eroare la trimiterea log-ului de comandă (delete): " + logEx.getMessage());
            }

            // Logica de business
            orderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Metodele read-only rămân neschimbate
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        try {
            OrderDTO orderDTO = orderService.getOrderById(id);
            return ResponseEntity.ok(orderDTO);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}