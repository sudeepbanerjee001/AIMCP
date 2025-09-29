package com.mcp.comms.memory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChromaMemoryStore {

    private final ChromaClient client;
    private final String collectionName = "default";

    public ChromaMemoryStore(ChromaClient client) {
        this.client = client;
    }

    public void addMemory(String id, String document, Map<String, String> metadata) {
        // Add a document + embedding + metadata to Chroma
        // Use dummy embeddings (as Float)
        List<Float> dummyEmbedding = List.of(0.0f, 0.0f, 0.0f);

        client.addDocument(collectionName, id, document, dummyEmbedding, metadata);
    }

    public List<String> queryMemory(List<Float> queryEmbedding, int topK) {
        // ChromaClient expects Float embeddings; convert to Double for its internal query
        List<Double> embeddingD = queryEmbedding.stream()
                .map(Float::doubleValue)
                .collect(Collectors.toList());

        String jsonResponse = client.queryCollection(collectionName, embeddingD, topK);

        // Simplified: parse JSON and return as List<String>
        return List.of(jsonResponse);
    }
}
