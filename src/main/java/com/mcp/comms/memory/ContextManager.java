package com.mcp.comms.memory;

import java.util.List;
import java.util.Map;

public class ContextManager {

    private final ChromaMemoryStore memoryStore;

    public ContextManager(ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    // Process a message and return a string response
    public String processMessage(String message) {
        List<Float> dummyEmbedding = List.of(0.0f, 0.0f, 0.0f);
        List<String> similarDocs = memoryStore.queryMemory(dummyEmbedding, 3);
        return String.join("\n", similarDocs);
    }

    // Store a message into Chroma memory
    public void storeMessage(String message, Map<String, String> metadata) {
        String id = "msg-" + System.currentTimeMillis();
        List<Float> dummyEmbedding = List.of(0.0f, 0.0f, 0.0f); // Replace with real embedding if available
        memoryStore.addMemory(id, message, metadata, dummyEmbedding);
    }
}
