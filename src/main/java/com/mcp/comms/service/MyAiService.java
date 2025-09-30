package com.mcp.comms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MyAiService {

    private static final Logger logger = LoggerFactory.getLogger(MyAiService.class);

    @Value("${ai.provider:ollama}") // ollama | openai | mistral
    private String aiProvider;

    @Value("${ai.model:mistral:7b-instruct-v0.3-q8_0}")
    private String model;

    @Value("${ai.baseUrl:http://localhost:11434}") // Ollama default
    private String baseUrl;

    @Value("${ai.apiKey:}") // only needed for OpenAI / Mistral hosted
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String process(String input) {
        try {
            switch (aiProvider.toLowerCase()) {
                case "ollama":
                    return callOllama(input);
                case "openai":
                case "mistral":
                    return callOpenAiStyle(input);
                default:
                    return "Unknown AI provider: " + aiProvider;
            }
        } catch (Exception e) {
            logger.error("AI call failed", e);
            return "AI Error: " + e.getMessage();
        }
    }

    private String callOllama(String input) {
        String url = baseUrl + "/api/generate";

        Map<String, Object> payload = Map.of(
                "model", model,
                "prompt", input,
                "stream", false   // ✅ disable streaming
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(url, payload, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("response")) {
            return (String) response.getBody().get("response");
        }
        return "No response from Ollama";
    }


    private String callOpenAiStyle(String input) {
        String url = baseUrl + "/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (apiKey != null && !apiKey.isBlank()) {
            headers.set("Authorization", "Bearer " + apiKey);
        }

        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "user", "content", input)
                }
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("choices")) {
            var choices = (java.util.List<Map<String, Object>>) response.getBody().get("choices");
            if (!choices.isEmpty()) {
                Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                return (String) msg.get("content");
            }
        }
        return "No response from AI provider";
    }
}
