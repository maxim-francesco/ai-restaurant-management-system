package com.example.ReservationManagement.config; // Adaptează pachetul la structura ta

import com.example.ReservationManagement.config.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import NOU: pentru HttpMethod
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // NOU: Aplicăm configurarea CORS definită mai jos
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permite POST către /api/reservations/user fără autentificare
                        .requestMatchers(HttpMethod.POST, "/api/reservations/user").permitAll() // LINIE NOUĂ
                        // Toate celelalte rute sub /api/reservations/** necesită autentificare
                        .requestMatchers("/api/reservations/**").authenticated()
                        // Orice altă cerere este permisă (fără autentificare)
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * NOU: Acest Bean definește regulile CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitem request-uri de la originea aplicației Angular
        configuration.setAllowedOrigins(Arrays.asList("https://clientapp-wvvp-git-main-maxim-francescos-projects.vercel.app/", "https://adminapp-cttk.vercel.app/"));
        // Permitem metodele HTTP standard
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permitem toate header-ele, inclusiv cel de Authorization
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Permitem trimiterea de cookies/credentials, dacă va fi cazul
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicăm această configurație pentru toate căile (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}