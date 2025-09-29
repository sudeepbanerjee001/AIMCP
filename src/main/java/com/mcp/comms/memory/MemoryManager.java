package com.mcp.comms.memory;

import java.util.HashMap;
import java.util.Map;

public class MemoryManager {

    private final ContextManager contextManager;

    public MemoryManager(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    public void saveSummary(String summary) {
        Map<String, String> metadata = new HashMap<>();
        // Add metadata if needed
        contextManager.storeMessage(summary, metadata);
    }

    public String retrieveSimilar(String message) {
        return contextManager.processMessage(message);
    }
}
