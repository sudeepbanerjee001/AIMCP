package com.mcp.comms.memory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class OllamaClient {

    private final String model;

    /**
     * Initialize the client with the Ollama model name
     * @param model Model name, e.g., "mistral:7b-instruct-v0.3-q8_0"
     */
    public OllamaClient(String model) {
        this.model = model;
    }

    /**
     * Generate streaming response from Ollama CLI.
     *
     * @param prompt   The prompt to send to the LLM
     * @param callback Callback for each token/line of output
     */
    public void generateStream(String prompt, Consumer<String> callback) {
        try {
            // Build the command for Ollama CLI
            ProcessBuilder builder = new ProcessBuilder(
                    "ollama", "run", model, "--prompt", prompt
            );

            builder.redirectErrorStream(true); // Merge stdout and stderr
            Process process = builder.start();

            // Read output line by line
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    callback.accept(line);
                }
            }

            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
            callback.accept("Error calling Ollama: " + e.getMessage());
        }
    }
}
