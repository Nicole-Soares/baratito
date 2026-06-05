package com.baratito.server.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class CorrectorOrtografico {

    private static final Logger log = LoggerFactory.getLogger(CorrectorOrtografico.class);
    private static final String LANGUAGETOOL_API = "https://api.languagetool.org/v2/check";

    private final RestTemplate restTemplate;

    public CorrectorOrtografico(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Calcula la distancia máxima de edición permitida para aceptar una corrección.
     * @param query texto original ingresado por el usuario
     * @return distancia máxima de edición, proporcional a la cantidad de palabras en la query (ej: "leche" → 1, "arroz con leche" → 3)
     */
    private int maxDistanciaEdicion(String query) {
        return query.trim().split("\\s+").length;
    }

    /**
     * Intenta corregir la query si tiene errores ortográficos.
     * @param query texto ingresado por el usuario (ej: "arros con lece")
     * @return query corregida (ej: "arroz con leche"), o la original si no hay correcciones o si la API no está disponible
     */
    public String corregir(String query) {
        if (query == null || query.isBlank()) return query;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("text", query);
            params.add("language", "es");
            params.add("enabledOnly", "false");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<LtResponse> response = restTemplate.exchange(
                    LANGUAGETOOL_API,
                    HttpMethod.POST,
                    request,
                    LtResponse.class
            );

            if (response.getBody() == null || response.getBody().matches().isEmpty()) {
                return query;
            }

            String corregida = aplicarCorrecciones(query, response.getBody().matches());

            // Rechazar la corrección si la diferencia total de caracteres es demasiado grande.
            // Esto evita que "bjhsc" → "base" (corrección agresiva sobre texto sin sentido).
            if (distanciaLevenshtein(query.toLowerCase(), corregida.toLowerCase()) > maxDistanciaEdicion(query)) {
                log.info("[CorrectorOrtografico] Corrección rechazada por distancia excesiva: '{}' → '{}', usando original", query, corregida);
                return query;
            }

            if (!corregida.equalsIgnoreCase(query)) {
                log.info("[CorrectorOrtografico] '{}' → '{}'", query, corregida);
            }

            return corregida;

        } catch (Exception e) {
            log.warn("[CorrectorOrtografico] API no disponible, usando query original '{}': {}", query, e.getMessage());
            return query;
        }
    }

    /**
     * Aplica las correcciones sugeridas por LanguageTool sobre el texto original.
     * Itera de atrás para adelante para que los offsets no se desplacen.
     * @param texto texto original
     * @param matches lista de errores detectados por LanguageTool con sus sugerencias
     * @return texto corregido aplicando la primera sugerencia de cada error
     */
    private String aplicarCorrecciones(String texto, List<LtMatch> matches) {
        StringBuilder sb = new StringBuilder(texto);

        for (int i = matches.size() - 1; i >= 0; i--) {
            LtMatch match = matches.get(i);

            if (match.replacements() == null || match.replacements().isEmpty()) continue;

            String sugerencia = match.replacements().get(0).value();
            if (sugerencia == null || sugerencia.isBlank()) continue;

            int inicio = match.offset();
            int fin    = match.offset() + match.length();

            if (inicio < 0 || fin > sb.length()) continue;

            sb.replace(inicio, fin, sugerencia);
        }

        return sb.toString();
    }

    /**
     * Calcula la distancia de Levenshtein entre dos strings:
     * cantidad mínima de inserciones, eliminaciones o sustituciones necesarias para transformar 'a' en 'b'.
     * @param a string original
     * @param b string corregido
     * @return distancia de edición entre 'a' y 'b'
     */
    private int distanciaLevenshtein(String a, String b) {
        int[] costos = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) costos[j] = j;

        for (int i = 1; i <= a.length(); i++) {
            int anterior = costos[0];
            costos[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int temp = costos[j];
                costos[j] = (a.charAt(i - 1) == b.charAt(j - 1))
                        ? anterior
                        : 1 + Math.min(anterior, Math.min(costos[j], costos[j - 1]));
                anterior = temp;
            }
        }
        return costos[b.length()];
    }

    // ─── DTOs de respuesta de LanguageTool ───────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    record LtResponse(
            @JsonProperty("matches") List<LtMatch> matches
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record LtMatch(
            @JsonProperty("offset")       int offset,
            @JsonProperty("length")       int length,
            @JsonProperty("replacements") List<LtReplacement> replacements
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record LtReplacement(
            @JsonProperty("value") String value
    ) {}
}