package com.example.gallery_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Această linie mapează cererile URL care încep cu /uploads/gallery/**
        // la folderul fizic 'uploads/gallery/' de pe disc.
        // "file:" este esențial pentru a indica o cale din sistemul de fișiere.
        registry.addResourceHandler("/uploads/gallery/**")
                .addResourceLocations("file:./uploads/gallery/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplică regula pentru toate endpoint-urile care încep cu /api/
                .allowedOrigins("https://clientapp-wvvp-git-main-maxim-francescos-projects.vercel.app/", "https://adminapp-cttk.vercel.app/") // Permite cereri DOAR de la această origine (aplicația ta Angular)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specifică metodele HTTP permise
                .allowedHeaders("*") // Permite orice header-e în cerere (ex: Authorization)
                .allowCredentials(true); // Permite trimiterea de cookies sau token-uri de autentificare
    }
}