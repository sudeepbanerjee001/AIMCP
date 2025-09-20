package com.mcp.comms.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

@Component
public class MistralTool {

    private static final Logger log = LoggerFactory.getLogger(MistralTool.class);

    public void queryMistralStreaming(String input, Consumer<String> tokenConsumer) {
        log.info("🛠 queryMistralStreaming called with input: {}", input);

        try {
            // Spawn ollama CLI process
            ProcessBuilder pb = new ProcessBuilder("ollama", "run", "mistral:7b-instruct-v0.3-q8_0");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            log.info("⚡ ollama process started");

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Send input to Mistral
            process.getOutputStream().write((input + "\n").getBytes());
            process.getOutputStream().flush();
            log.info("✉️ Input sent to Mistral: {}", input);

            String line;
            while ((line = reader.readLine()) != null) {
                log.info("📤 Received from Mistral: {}", line);
                // Stream token by token to WebSocket
                tokenConsumer.accept(line + "\n");
            }

            int exitCode = process.waitFor();
            log.info("✅ ollama process exited with code {}", exitCode);

        } catch (Exception e) {
            log.error("❌ Error in queryMistralStreaming", e);
        }
    }
}
