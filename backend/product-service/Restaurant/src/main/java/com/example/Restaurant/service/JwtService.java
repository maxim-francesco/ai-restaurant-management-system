package com.example.Restaurant.service; // Adaptează pachetul la structura ta

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {


    String extractUsername(String token);


    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractName(String token);
}