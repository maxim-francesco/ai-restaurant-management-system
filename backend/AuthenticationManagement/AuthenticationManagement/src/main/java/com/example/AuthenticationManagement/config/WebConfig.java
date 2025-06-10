package com.example.AuthenticationManagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure resource handlers to serve static files like images.
     * This method maps a URL path to a physical location on the server's filesystem.
     *
     * @param registry The registry to which resource handlers are added.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // IMPORTANT: The path handler must match the URL structure used in your controller.
        // We are using "/images/**" to match the URLs like "http://localhost:8082/images/..."
        registry
                .addResourceHandler("/images/**")
                // Serve files from an "uploads" directory located in the project's root folder.
                // Using "file:" is better for user-uploaded content than "classpath:",
                // as it saves files externally and they persist between application builds.
                // Make sure to create the "uploads" directory in your project's root.
                .addResourceLocations("file:./uploads/");
    }
}
