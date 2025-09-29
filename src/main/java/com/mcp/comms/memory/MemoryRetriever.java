package com.mcp.comms.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Lazy
public class MemoryRetriever {

    private final ChromaMemoryStore memoryStore;

    @Autowired
    public MemoryRetriever(ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    public List<String> queryMemory(List<Float> queryEmbedding, int topK) {
        return memoryStore.queryMemory(queryEmbedding, topK);
    }
}
