package com.example.Restaurant.controller;

import com.example.Restaurant.dto.ProductDTO;
import com.example.Restaurant.events.LogEvent;
import com.example.Restaurant.service.JwtService;
import com.example.Restaurant.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType; // Import nou
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Import nou

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final String EXCHANGE_NAME = "logs_exchange";
    private static final String ROUTING_KEY_PRODUCT = "log.product.event";

    private final ProductService productService;
    private final RabbitTemplate rabbitTemplate;
    private final JwtService jwtService;

    @GetMapping
    public List<ProductDTO> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@RequestBody ProductDTO dto,
                                             @RequestHeader("Authorization") String authHeader) {
        ProductDTO createdProduct = productService.create(dto);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a creat un produs nou: '%s' (ID: %d).",
                    userName, createdProduct.getName(), createdProduct.getId());

            LogEvent event = new LogEvent(logMessage, "PRODUCT", "CREATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_PRODUCT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de produs (create): " + e.getMessage());
        }

        return ResponseEntity.status(201).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable Long id,
                                             @RequestBody ProductDTO dto,
                                             @RequestHeader("Authorization") String authHeader) {
        ProductDTO updatedProduct = productService.update(id, dto);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a actualizat Produsul '%s' (ID: %d).",
                    userName, updatedProduct.getName(), updatedProduct.getId());

            LogEvent event = new LogEvent(logMessage, "PRODUCT", "UPDATE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_PRODUCT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de produs (update): " + e.getMessage());
        }

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            ProductDTO productToDelete = productService.findById(id); // Obține produsul înainte de ștergere pentru a loga numele
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a șters Produsul '%s' (ID: %d).",
                    userName, productToDelete.getName(), id);

            LogEvent event = new LogEvent(logMessage, "PRODUCT", "DELETE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_PRODUCT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului de produs (delete): " + e.getMessage());
        }

        productService.delete(id); // Acum ștergem produsul (și implicit imaginea dacă există)
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ingredients/{id}/in-use")
    public boolean isIngredientInUse(@PathVariable Long id) {
        return productService.isIngredientUsed(id);
    }

    // ============== START MODIFICARE ==============
    @PostMapping(value = "/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> uploadProductImage(@PathVariable Long productId,
                                                         @RequestPart("imageFile") MultipartFile imageFile,
                                                         @RequestHeader("Authorization") String authHeader) {
        ProductDTO updatedProduct = productService.uploadProductImage(productId, imageFile);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a încărcat o imagine pentru Produsul '%s' (ID: %d). Nume fișier: %s",
                    userName, updatedProduct.getName(), updatedProduct.getId(), imageFile.getOriginalFilename());

            LogEvent event = new LogEvent(logMessage, "PRODUCT", "UPLOAD_IMAGE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_PRODUCT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului pentru upload imagine produs: " + e.getMessage());
        }

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}/image")
    public ResponseEntity<Void> deleteProductImage(@PathVariable Long productId,
                                                   @RequestHeader("Authorization") String authHeader) {
        ProductDTO productToDeleteImageFor = productService.findById(productId); // Obține produsul pentru logare

        productService.deleteProductImage(productId);

        try {
            final String token = authHeader.substring(7);
            final String userName = jwtService.extractName(token);
            String logMessage = String.format(
                    "Utilizatorul '%s' a șters imaginea pentru Produsul '%s' (ID: %d).",
                    userName, productToDeleteImageFor.getName(), productId);

            LogEvent event = new LogEvent(logMessage, "PRODUCT", "DELETE_IMAGE");
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_PRODUCT, event);

        } catch (Exception e) {
            System.err.println("### Eroare la trimiterea log-ului pentru ștergere imagine produs: " + e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }
    // =============== END MODIFICARE ===============
}