package com.epiis.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Service
public class ReniecService {

    @Value("${reniec.api.url}")
    private String reniecApiUrl;

    @Value("${reniec.api.token}")
    private String reniecToken;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ReniecService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Consulta DATOS REALES de RENIEC por DNI
     */
    public Map<String, Object> consultarDni(String dni) {
        try {
            // Validar formato DNI
            if (dni == null || !dni.matches("^[0-9]{8}$")) {
                return Map.of("success", false, "error", "DNI inválido. Debe tener 8 dígitos");
            }

            System.out.println("🔍 Consultando RENIEC para DNI: " + dni);

            // Construir URL
            String url = reniecApiUrl + "?numero=" + dni;

            // Hacer petición a RENIEC
            Mono<String> response = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + reniecToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(error -> {
                        System.err.println("❌ Error en RENIEC: " + error.getMessage());
                    });

            String jsonResponse = response.block();

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                return Map.of(
                    "success", false,
                    "error", "Respuesta vacía de RENIEC"
                );
            }

            // Parsear respuesta
            @SuppressWarnings("unchecked")
            Map<String, Object> data = objectMapper.readValue(jsonResponse, Map.class);

            // Validar que contiene los campos esperados
            if (!data.containsKey("document_number")) {
                return Map.of(
                    "success", false,
                    "error", "Respuesta inválida de RENIEC"
                );
            }

            System.out.println("✅ DNI encontrado en RENIEC: " + data.get("full_name"));

            return Map.of("success", true, "data", data);

        } catch (WebClientResponseException.Unauthorized e) {
            System.err.println("🔑 ERROR: Token de RENIEC inválido o expirado");
            return Map.of(
                "success", false,
                "error", "Token de RENIEC inválido. Contacta al administrador para renovar el token."
            );

        } catch (WebClientResponseException.NotFound e) {
            System.err.println("❌ DNI no encontrado en RENIEC");
            return Map.of(
                "success", false,
                "error", "DNI no encontrado en RENIEC"
            );

        } catch (WebClientResponseException e) {
            System.err.println("❌ Error HTTP de RENIEC: " + e.getStatusCode() + " - " + e.getMessage());
            return Map.of(
                "success", false,
                "error", "Error al consultar RENIEC: " + e.getStatusCode()
            );

        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
            return Map.of(
                "success", false,
                "error", "Error al consultar RENIEC: " + e.getMessage()
            );
        }
    }

    /**
     * Extrae información de la respuesta RENIEC
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> extraerDatosReniec(Map<String, Object> reniecData) {
        try {
            return Map.of(
                "nombres", reniecData.getOrDefault("first_name", "").toString().trim(),
                "apellido_paterno", reniecData.getOrDefault("first_last_name", "").toString().trim(),
                "apellido_materno", reniecData.getOrDefault("second_last_name", "").toString().trim(),
                "dni", reniecData.getOrDefault("document_number", "").toString().trim()
            );
        } catch (Exception e) {
            return Map.of("error", "Error al extraer datos: " + e.getMessage());
        }
    }
}