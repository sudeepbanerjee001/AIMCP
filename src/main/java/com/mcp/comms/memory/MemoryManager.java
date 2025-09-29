package com.mcp.comms.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemoryManager {

    private final ContextManager contextManager;

    @Autowired
    public MemoryManager(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    public void addMemory(String summary) {
        Map<String, String> metadata = new HashMap<>();
        contextManager.storeMessage(summary, metadata);
    }

    public List<String> retrieveMemory(List<Float> queryEmbedding, int topK) {
        return contextManager.getSimilarDocuments(queryEmbedding, topK);
    }
}
