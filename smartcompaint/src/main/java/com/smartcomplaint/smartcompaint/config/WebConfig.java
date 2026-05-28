package com.smartcomplaint.smartcompaint.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Path uploadDir;
    private final String[] allowedOrigins;

    public WebConfig(
            @Value("${app.upload-dir}") String uploadDir,
            @Value("${app.cors.allowed-origins}") String allowedOrigins
    ) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toArray(String[]::new);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadDir.toUri().toString());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
        registry.addMapping("/uploads/**")
                .allowedOriginPatterns(allowedOrigins)
                .allowedMethods("GET")
                .allowedHeaders("*");
    }
}
