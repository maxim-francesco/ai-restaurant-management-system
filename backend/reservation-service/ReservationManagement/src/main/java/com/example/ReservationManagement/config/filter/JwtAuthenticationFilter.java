package com.example.ReservationManagement.config.filter; // Adaptează pachetul la structura ta

import com.example.ReservationManagement.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component // Îl marcăm ca o componentă Spring pentru a putea fi injectat
@RequiredArgsConstructor // Generează un constructor cu câmpurile 'final'
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain // 'filterChain' este lanțul de filtre prin care trece request-ul
    ) throws ServletException, IOException {

        // 1. Extragem header-ul "Authorization" din request
        final String authHeader = request.getHeader("Authorization");

        // 2. Verificăm dacă header-ul există și dacă începe cu "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Dacă nu, trimitem request-ul mai departe și ne oprim
            return;
        }

        // 3. Extragem token-ul JWT (tot ce vine după "Bearer ")
        final String jwt = authHeader.substring(7);
        // 4. Extragem email-ul utilizatorului din token folosind JwtService
        final String userEmail = jwtService.extractUsername(jwt);

        // 5. Verificăm dacă avem un email și dacă utilizatorul nu este deja autentificat
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // În acest punct, avem încredere în token deoarece semnătura a fost validată implicit
            // la extragerea username-ului.
            // Acum, trebuie să actualizăm SecurityContextHolder pentru ca Spring Security să știe
            // că acest utilizator este autentificat pentru request-ul curent.

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userEmail, // Principal-ul (identitatea) este email-ul
                    null,      // Nu avem nevoie de credențiale (parolă) aici, token-ul este dovada
                    Collections.emptyList() // Nu adăugăm roluri deocamdată, pentru simplitate
            );
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            // Actualizăm contextul de securitate
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Trimitem request-ul mai departe, către următorul filtru din lanț
        filterChain.doFilter(request, response);
    }
}