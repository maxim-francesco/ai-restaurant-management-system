package com.example.Restaurant.service; // Adaptează pachetul la structura ta

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    /**
     * Extrage username-ul (subiectul) dintr-un token JWT.
     * @param token Token-ul JWT.
     * @return String Username-ul.
     */
    String extractUsername(String token);

    /**
     * Generează un token JWT nou pentru un utilizator.
     * @param userDetails Detaliile utilizatorului (furnizate de Spring Security).
     * @return String Token-ul JWT generat.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Verifică dacă un token JWT este valid.
     * Un token este valid dacă nu a expirat și dacă aparține utilizatorului specificat.
     * @param token Token-ul JWT.
     * @param userDetails Detaliile utilizatorului pentru comparație.
     * @return boolean True dacă token-ul este valid, altfel false.
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    String extractName(String token);
}