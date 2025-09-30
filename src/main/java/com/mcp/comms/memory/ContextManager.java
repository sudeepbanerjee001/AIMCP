package com.mcp.comms.memory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ContextManager {

    private final ChromaMemoryStore memoryStore;
    private final OllamaClient ollamaClient;
    private final ExecutorService executor;

    public ContextManager(ChromaMemoryStore memoryStore, OllamaClient ollamaClient) {
        this.memoryStore = memoryStore;
        this.ollamaClient = ollamaClient;
        this.executor = Executors.newFixedThreadPool(4);
    }

    /**
     * Asynchronously process a message
     *
     * @param message  User message
     * @param callback Consumer<String> callback to receive streaming response
     */
    public void processMessageAsync(String message, Consumer<String> callback) {
        executor.submit(() -> processMessage(message, callback));
    }

    /**
     * Process message: generate embedding, store in memory, retrieve similar messages, call LLM
     */
    private void processMessage(String message, Consumer<String> callback) {
        System.out.println("Processing message: " + message);

        // 1. Generate embedding
        List<Double> embedding = EmbeddingGenerator.generateEmbedding(message);
        System.out.println("Embedding generated: size=" + embedding.size());

        // 2. Store in memory
        String messageId = "msg_" + UUID.randomUUID();
        memoryStore.storeMessage(messageId, message, embedding);

        // 3. Retrieve similar messages
        List<String> similarMessages = memoryStore.getSimilarMessages(embedding, 5);
        System.out.println("Found " + similarMessages.size() + " similar messages");

        // 4. Construct prompt
        StringBuilder prompt = new StringBuilder("You are MCP AI assistant.\n\n");
        if (!similarMessages.isEmpty()) {
            prompt.append("Here are similar previous messages for context:\n");
            for (String msg : similarMessages) {
                prompt.append("- ").append(msg).append("\n");
            }
        }
        prompt.append("\nUser: ").append(message).append("\nAssistant:");

        // 5. Call Ollama LLM with streaming
        ollamaClient.generateStream(prompt.toString(), callback);
    }
}
