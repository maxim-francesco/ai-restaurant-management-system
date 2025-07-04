package com.example.Restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// @EnableWebMvc <-- ELIMINĂ ACEASTĂ LINIE
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Asigură-te că folosești o cale absolută pentru testare
        // Atenție: fișierele se vor pierde la restart!
        registry.addResourceHandler("/uploads/product-images/**")
                .addResourceLocations("file:/tmp/uploads/product-images/");
    }
}