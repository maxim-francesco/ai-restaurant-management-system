package com.example.AuthenticationManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configurează un "resource handler" pentru a servi fișiere statice (imaginile încărcate).
     * @param registry registrul de handlere
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Această linie face legătura magică:
        // Când un request vine la o adresă care începe cu "/images/**"
        // Spring va căuta fișierul corespunzător în folderul "uploads/" de pe disc.
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:/tmp/uploads/"); // Cale absolută
    }
}