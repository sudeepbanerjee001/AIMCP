package com.mcp.comms.memory;

import java.util.List;

public class MemoryRetriever {
    private final ChromaMemoryStore memoryStore;

    public MemoryRetriever(ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    public List<String> retrieveRelevantMemories(String query) {
        return memoryStore.querySimilar(query, 3);
    }
}
