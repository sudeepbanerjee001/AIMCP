package com.mcp.comms.memory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Embedding generator using local Ollama embeddings API with retries and logging.
 */
public class EmbeddingGenerator {

    private static final String OLLAMA_URL = "http://localhost:11434/api/embeddings";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(EmbeddingGenerator.class.getName());

    // Retry settings
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    /**
     * Generate embedding for the given text using Ollama.
     * @param text input string
     * @return List<Double> embedding vector
     */
    public static List<Double> generateEmbedding(String text) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                logger.info(String.format("Generating embedding (attempt %d/%d) for text: %s",
                        attempt, MAX_RETRIES, text));

                URL url = new URL(OLLAMA_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(60000); // allow longer reads for large vectors

                // Build request body
                String jsonInput = String.format("{\"model\":\"mistral:7b-instruct-v0.3-q8_0\",\"prompt\":\"%s\"}", text);
                logger.info("Request body: " + jsonInput);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonInput.getBytes());
                    os.flush();
                }

                int status = conn.getResponseCode();
                logger.info("Ollama responded with status: " + status);

                if (status != 200) {
                    logger.warning("Unexpected response code: " + status);
                }

                // Read response as string for debugging
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }
                in.close();
                String responseStr = responseBuilder.toString();
                logger.fine("Raw Ollama response: " + responseStr);

                // Parse response
                JsonNode root = mapper.readTree(responseStr);
                List<Double> embedding = new ArrayList<>();

                JsonNode embedArray = root.get("embedding");
                if (embedArray != null && embedArray.isArray()) {
                    Iterator<JsonNode> it = embedArray.iterator();
                    while (it.hasNext()) {
                        embedding.add(it.next().asDouble());
                    }
                }

                conn.disconnect();
                logger.info("Successfully generated embedding of length: " + embedding.size());
                return embedding;

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error generating embedding (attempt " + attempt + ")", e);
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // fallback: return dummy vector if Ollama not reachable
        logger.warning("Falling back to dummy embedding after " + MAX_RETRIES + " failed attempts.");
        List<Double> fallback = new ArrayList<>();
        fallback.add(0.0);
        return fallback;
    }
}
