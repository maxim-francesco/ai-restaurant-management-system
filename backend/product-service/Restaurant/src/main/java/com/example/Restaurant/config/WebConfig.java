package com.example.Restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    // WebConfig.java
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Folosește o cale absolută. Pe sistemele Linux (ca Railway), /tmp este de obicei disponibil.
        // ATENȚIE: Fișierele din /tmp se vor pierde la restart!
        String uploadPath = "/tmp/uploads/product-images/";

        registry.addResourceHandler("/uploads/product-images/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
