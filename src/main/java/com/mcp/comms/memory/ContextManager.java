package com.mcp.comms.memory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ContextManager {

    private final ChromaMemoryStore memoryStore;

    public ContextManager(ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    /**
     * Adds a message and its embedding to the memory store.
     *
     * @param message  The text message to store.
     * @param metadata Optional metadata associated with this message.
     */
    public void addMessageToMemory(String message, Map<String, String> metadata) {

        // Example dummy embedding (replace with real embedding from your model)
        List<Double> dummyEmbedding = Arrays.asList(0.1d, 0.2d, 0.3d);

        // Unique ID for this memory entry
        String memoryId = "doc_" + System.currentTimeMillis();

        // Store in memoryStore
        memoryStore.addMemory(message, memoryId, dummyEmbedding, metadata);
    }

    /**
     * Alias for addMessageToMemory, for backward compatibility.
     *
     * @param message  The text message to store.
     * @param metadata Metadata associated with this message.
     */
    public void storeMessage(String message, Map<String, String> metadata) {
        addMessageToMemory(message, metadata);
    }

    /**
     * Retrieves top K similar messages for a given query embedding.
     *
     * @param queryEmbedding The query embedding vector.
     * @param topK           Number of results to return.
     * @return JSON response from memory store.
     */
    public String searchMemory(List<Double> queryEmbedding, int topK) {
        return memoryStore.search("default", queryEmbedding, topK);
    }

    /**
     * Processes an incoming message (used by WebSocket handler).
     * Stores the message in memory and returns a confirmation response.
     *
     * @param message Input message from client.
     * @return Confirmation response string.
     */
    public String processMessage(String message) {
        // Store message in memory with simple metadata
        addMessageToMemory(message, Map.of("source", "websocket"));

        // Return a response for the WebSocket client
        return "Message processed: " + message;
    }
}
