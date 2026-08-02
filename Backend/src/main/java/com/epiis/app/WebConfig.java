package com.epiis.app;

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
        // Ruta absoluta para archivos subidos dinámicamente
        String pathAbsoluto = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        
        // 1. Servir desde carpeta uploads (imágenes dinámicas)
        registry.addResourceHandler("/img/**")
                .addResourceLocations(pathAbsoluto)
                .setCachePeriod(3600);
        
        // 2. Alternativa: servir desde carpeta estática del proyecto
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
        
        // 3. Servir desde carpeta public
        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/public/")
                .setCachePeriod(3600);
        
        // 4. Fallback para archivos no encontrados
        registry.addResourceHandler("/**")
                .addResourceLocations(pathAbsoluto)
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/public/");
    }
}
