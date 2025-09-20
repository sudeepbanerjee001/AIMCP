package com.mcp.comms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.function.Consumer;

@Service
public class OllamaService {

    private static final Logger log = LoggerFactory.getLogger(OllamaService.class);
    private final WebClient webClient;

    public OllamaService() {
        log.info("🟢 Initializing OllamaService with WebClient");
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
        log.info("✅ OllamaService initialized with baseUrl http://localhost:11434");
    }

    /**
     * Streams query response from Ollama model
     *
     * @param prompt           the user prompt
     * @param onTokenReceived  consumer callback for each token
     */
    public void streamQuery(String prompt, Consumer<String> onTokenReceived) {
        log.info("➡️ [OllamaService] Sending prompt to Ollama: {}", prompt);

        Flux<String> responseFlux = webClient.post()
                .uri("/api/generate")
                .bodyValue(Map.of(
                        "model", "mistral:7b-instruct-v0.3-q8_0", // you can change to your model
                        "prompt", prompt,
                        "stream", true
                ))
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(sub -> log.info("📡 [OllamaService] Subscribed to Ollama stream..."))
                .doOnNext(chunk -> log.info("⬅️ [OllamaService] Received raw chunk: {}", chunk))
                .doOnError(err -> log.error("❌ [OllamaService] Error from Ollama", err))
                .doOnComplete(() -> log.info("✅ [OllamaService] Completed streaming from Ollama"));

        // IMPORTANT: must subscribe, otherwise nothing happens!
        responseFlux.subscribe(token -> {
            log.info("📝 [OllamaService] Processed token: {}", token);
            try {
                onTokenReceived.accept(token);
            } catch (Exception e) {
                log.error("⚠️ [OllamaService] Error delivering token to callback", e);
            }
        });
    }
}
