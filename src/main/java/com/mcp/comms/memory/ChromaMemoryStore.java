package com.mcp.comms.memory;

import com.mcp.comms.embedding.EmbeddingService;

import java.util.*;

public class ChromaMemoryStore {
    private final ChromaClient client;
    private final EmbeddingService embeddingService;
    private final String collectionName;

    public ChromaMemoryStore(ChromaClient client,
                             EmbeddingService embeddingService,
                             String collectionName) {
        this.client = client;
        this.embeddingService = embeddingService;
        this.collectionName = collectionName;
    }

    public void addMemory(String text, Map<String, String> metadata) {
        List<Float> vector = embeddingService.getEmbedding(text);
        client.addDocument(collectionName, UUID.randomUUID().toString(), text, vector, metadata);
    }

    public List<String> querySimilar(String text, int topK) {
        List<Float> queryVec = embeddingService.getEmbedding(text);
        return client.query(collectionName, queryVec, topK);
    }
}
