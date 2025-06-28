package com.example.contact_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 1. Anunță Spring că această clasă va gestiona excepții pentru toate controllerele
public class GlobalExceptionHandler {

    // 2. Această metodă va fi apelată specific când apare o eroare de validare (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 3. Creăm un map pentru a stoca erorile (ex: "email": "Adresa de email nu este validă.")
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // 4. Returnăm un răspuns HTTP 400 Bad Request cu lista de erori în format JSON
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}