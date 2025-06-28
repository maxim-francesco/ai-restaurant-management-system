package com.example.review_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Combina @ControllerAdvice cu @ResponseBody. Perfect pentru API-uri REST.
public class GlobalExceptionHandler {

    /**
     * Aceasta metoda este chemata automat cand @Valid esueaza intr-un controller.
     * @param ex Exceptia aruncata de Spring, care contine toate erorile de validare.
     * @return Un ResponseEntity care contine o harta a erorilor si statusul HTTP 400 BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Iteram prin toate erorile gasite si extragem numele campului si mesajul de eroare.
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}