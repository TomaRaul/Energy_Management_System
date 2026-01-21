package com.ds.ems.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAnswer(String question) {
        try {
            // Construim URL-ul final cu cheia
            String finalUrl = apiUrl + "?key=" + apiKey;

            // Construim Body-ul JSON specific Gemini
            // Structura ceruta de Google: { "contents": [{ "parts": [{ "text": "Intrebarea mea" }] }] }
            String requestJson = """
                {
                    "contents": [{
                        "parts": [{
                            "text": "%s"
                        }]
                    }]
                }
                """.formatted(question.replace("\"", "\\\"")); // escape ghilimelele din întrebare

            // Set Header-ul
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

            // Facem apelul POST
            String response = restTemplate.postForObject(finalUrl, request, String.class);

            // Parsezi raspunsul (care e un JSON stufos) ca sa iei doar textul
            return extractTextFromResponse(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "I am sorry, my AI brain is currently offline. Please try again later.";
        }
    }

    private String extractTextFromResponse(String jsonResponse) {
        try {
            // Navigam prin JSON: candidates[0] -> content -> parts[0] -> text
            JsonNode root = objectMapper.readTree(jsonResponse);
            if (root.has("candidates") && !root.path("candidates").isEmpty()) {
                JsonNode candidate = root.path("candidates").get(0);
                JsonNode content = candidate.path("content");
                JsonNode parts = content.path("parts");
                if (!parts.isEmpty()) {
                    return parts.get(0).path("text").asText();
                }
            }
            return "I received a response, but it was empty.";
        } catch (Exception e) {
            return "Error parsing AI response.";
        }
    }
}