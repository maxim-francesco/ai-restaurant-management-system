package com.example.Restaurant.config;

import com.example.Restaurant.config.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Aici specificăm originile permise
        config.setAllowedOrigins(Arrays.asList("https://clientapp-648cvri7w-maxim-francescos-projects.vercel.app/", "https://adminapp-cttk.vercel.app/")); // Portul dinamic poate fi util pentru teste
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept", "Authorization")); // Adăugăm explicit headerele permise
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Adăugăm și PATCH, pentru orice eventualitate
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF este deja dezactivat, ceea ce este corect pentru o aplicație stateless cu API-uri
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // MODIFICARE CHEIE: Permitem explicit toate cererile de tip OPTIONS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // NOU: Permitem accesul la imaginile produselor fără autentificare
                        .requestMatchers("/uploads/product-images/**").permitAll()
                        // Păstrăm regulile existente
                        .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**", "/api/ingredients/**").permitAll()
                        // Orice altă cerere necesită autentificare
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}