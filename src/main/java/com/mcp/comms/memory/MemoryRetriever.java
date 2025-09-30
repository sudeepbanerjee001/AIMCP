package com.mcp.comms.memory;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * MemoryRetriever fetches similar messages using ChromaMemoryStore.
 */
@Component
public class MemoryRetriever {

    private final ChromaMemoryStore memoryStore;

    public MemoryRetriever(ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    public List<Message> retrieveSimilarMessages(List<Double> embedding, int topK) {
        // Convert embedding to List<Float> if needed
        List<Float> floatEmbedding = embedding.stream()
                .map(Double::floatValue)
                .toList();
        return memoryStore.getSimilarMessages(floatEmbedding, topK);
    }
}
