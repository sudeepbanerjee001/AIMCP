package com.mcp.comms.memory;

public class MemoryRetriever {

    private final ChromaMemoryStore memoryStore;

    public MemoryRetriever(ChromaMemoryStore store) {
        this.memoryStore = store;
    }

    /**
     * Retrieve top 3 similar memories based on text query
     */
    public String retrieveSimilar(String query) {
        return memoryStore.querySimilar(query, 3);
    }
}
