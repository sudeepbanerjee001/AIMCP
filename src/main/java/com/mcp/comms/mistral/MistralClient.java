package com.mcp.comms.mistral;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;

@Component
public class MistralClient {
    private static final String MISTRAL_API_URL = "http://localhost:11434/api/chat";
    private static final String MODEL_NAME = "mistral";

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public MistralClient() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Streams assistant response but also returns the complete reply.
     */
    public String chat(List<ChatSession.Message> history, Consumer<String> streamer) {
        StringBuilder fullResponse = new StringBuilder();

        try {
            // Payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", MODEL_NAME);
            payload.put("stream", true);

            List<Map<String, String>> messages = new ArrayList<>();
            for (ChatSession.Message msg : history) {
                messages.add(Map.of("role", msg.role, "content", msg.content));
            }
            payload.put("messages", messages);

            // Prepare request
            RequestCallback requestCallback = request -> {
                request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                mapper.writeValue(request.getBody(), payload);
            };

            // Stream the response
            ResponseExtractor<Void> responseExtractor = (ClientHttpResponse response) -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;

                        try {
                            JsonNode node = mapper.readTree(line);

                            if (node.has("message")) {
                                String content = node.get("message").get("content").asText();
                                fullResponse.append(content);
                                streamer.accept(content); // send chunk to websocket
                            }

                            // Ollama ends with {"done": true}
                            if (node.has("done") && node.get("done").asBoolean()) {
                                break;
                            }

                        } catch (Exception e) {
                            System.err.println("Skipping bad line: " + line);
                        }
                    }
                }
                return null;
            };

            restTemplate.execute(MISTRAL_API_URL,
                    org.springframework.http.HttpMethod.POST,
                    requestCallback,
                    responseExtractor);

        } catch (Exception e) {
            e.printStackTrace();
            streamer.accept("[Error: " + e.getMessage() + "]");
        }

        // Return the accumulated assistant reply
        return fullResponse.toString();
    }
}
