package com.epiis.app;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// ❌ ELIMINA ESTA LÍNEA (Causa que las peticiones se queden colgadas)
// @EnableWebMvc 
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = Paths.get(uploadDir).toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/img/**")
                .addResourceLocations(path);
    }
}