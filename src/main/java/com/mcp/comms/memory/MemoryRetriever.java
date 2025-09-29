package com.mcp.comms.memory;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class MemoryRetriever {

    private final ChromaMemoryStore memoryStore;

    public MemoryRetriever(@Lazy ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    public List<String> retrieveSimilar(String query, int topK) {
        return memoryStore.queryMemory(query, topK);
    }
}
