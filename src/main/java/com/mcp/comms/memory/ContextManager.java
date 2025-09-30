package com.mcp.comms.memory;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * ContextManager handles storage and retrieval of messages in memory.
 */
@Component
public class ContextManager {

    private final ChromaMemoryStore memoryStore;

    public ContextManager(ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    /**
     * Store a message with metadata.
     * Called by MemoryManager.
     */
    public void storeMessage(String summary, Map<String, String> metadata) {
        System.out.println("ContextManager storing message: " + summary + " with metadata: " + metadata);
        // Delegate to memoryStore
        memoryStore.saveMessage(summary, metadata);
    }

    /**
     * Retrieve messages
     */
    public List<String> getMessages(int topK) {
        return memoryStore.getTopMessages(topK);
    }
}
