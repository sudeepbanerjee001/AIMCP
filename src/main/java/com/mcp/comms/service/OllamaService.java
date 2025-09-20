package com.mcp.comms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

@Service
public class OllamaService {

    private static final Logger log = LoggerFactory.getLogger(OllamaService.class);

    private static final String MODEL_NAME = "mistral:7b-instruct-v0.3-q8_0";

    /**
     * Streams query to Ollama and invokes callback for each token.
     */
    public void streamQuery(String prompt, Consumer<String> onToken) throws IOException {
        log.info("🚀 Starting Ollama process with model: {} and prompt: {}", MODEL_NAME, prompt);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "ollama", "run", MODEL_NAME
        );
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Write the user prompt into Ollama stdin
        try {
            log.debug("✍️ Sending prompt to Ollama stdin...");
            process.getOutputStream().write((prompt + "\n").getBytes());
            process.getOutputStream().flush();
            process.getOutputStream().close();
        } catch (IOException e) {
            log.error("❌ Failed to write prompt to Ollama stdin", e);
            throw e;
        }

        // Capture Ollama output line by line
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("📥 Ollama raw output: {}", line);
                onToken.accept(line);
            }
        } catch (IOException e) {
            log.error("❌ Error reading Ollama output", e);
            throw e;
        }

        try {
            int exitCode = process.waitFor();
            log.info("✅ Ollama process finished with exit code: {}", exitCode);
        } catch (InterruptedException e) {
            log.error("⚠️ Ollama process interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
