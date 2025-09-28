package com.mcp.comms.memory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryManager {

    private final ChromaMemoryStore memoryStore;

    public MemoryManager(ChromaMemoryStore store) {
        this.memoryStore = store;
    }

    /**
     * Add summary or message to memory
     */
    public void saveMemory(String summary, String sessionId) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("sessionId", sessionId);

        memoryStore.addMemory(summary, metadata);
    }

    /**
     * Retrieve relevant memories
     */
    public String retrieveMemory(List<Float> queryEmbedding, int topK) {
        return memoryStore.queryMemory(queryEmbedding, topK);
    }
}
