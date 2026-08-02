package com.epiis.app.util;

import java.text.Normalizer;

public class StringUtils {

    public static String toKebabCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        // Quita tildes (ej: "Café" -> "Cafe")
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");

        // Convierte a minúsculas, reemplaza espacios por guiones y borra símbolos
        return withoutAccents.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}