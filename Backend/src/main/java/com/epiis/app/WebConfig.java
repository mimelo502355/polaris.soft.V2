package com.epiis.app;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            // Crear carpeta de uploads si no existe
            Files.createDirectories(Paths.get(uploadDir));
        } catch (Exception e) {
            System.err.println("Error creando carpeta de uploads: " + e.getMessage());
        }
        
        String pathAbsoluto = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        
        // Servir imágenes desde carpeta uploads
        registry.addResourceHandler("/img/**")
                .addResourceLocations(pathAbsoluto)
                .setCachePeriod(3600);
    }
}
