package com.mcp.comms.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemoryRetriever {

    private static final Logger logger = LoggerFactory.getLogger(MemoryRetriever.class);

    private final ChromaMemoryStore memoryStore;

    public MemoryRetriever(ChromaMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    // Retrieve similar messages using embeddings
    public List<String> retrieveSimilarMessages(List<Double> embedding, int topK) {
        logger.info("Retrieving up to {} similar messages", topK);
        return memoryStore.getSimilarMessages(embedding, topK);
    }
}
