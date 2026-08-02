package com.epiis.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // ✅ PERMITIR TODOS LOS GET (lectura de datos)
                .requestMatchers("GET", "/**").permitAll()
                
                // ✅ PERMITIR /api/auth/** (login, validar token)
                .requestMatchers("/api/auth/**").permitAll()
                
                // ✅ PERMITIR /api/cliente/login, registro, verificar
                .requestMatchers("/api/cliente/login", "/api/cliente/registro").permitAll()
                .requestMatchers("/api/cliente/verificar-disponibilidad/**").permitAll()
                
                // ✅ POST/PUT/DELETE en /api/pedido requieren autenticación
                .requestMatchers("POST", "/api/pedido/**").authenticated()
                .requestMatchers("PUT", "/api/pedido/**").authenticated()
                .requestMatchers("DELETE", "/api/pedido/**").authenticated()
                
                // ✅ POST/PUT/DELETE en /api/producto requieren autenticación
                .requestMatchers("POST", "/api/producto/**").authenticated()
                .requestMatchers("PUT", "/api/producto/**").authenticated()
                .requestMatchers("DELETE", "/api/producto/**").authenticated()
                
                // ✅ El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "https://polaris-frontend.netlify.app",
            "http://localhost:4200",
            "http://localhost:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
