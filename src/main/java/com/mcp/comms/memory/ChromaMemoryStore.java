package com.mcp.comms.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChromaMemoryStore {

    private final ChromaClient client;
    private final String collectionName = "aimcp";

    public ChromaMemoryStore(ChromaClient client) {
        this.client = client;
    }

    /**
     * Add memory to Chroma
     */
    public void addMemory(String text, Map<String, String> metadata) {
        String id = UUID.randomUUID().toString();
        List<Float> embedding = new ArrayList<>();
        // Dummy embedding — replace with real embedding logic
        embedding.add(0.1f); embedding.add(0.2f); embedding.add(0.3f);

        client.addDocument(collectionName, id, text, embedding, metadata);
    }

    /**
     * Query top K documents using embedding
     */
    public String queryMemory(List<Float> queryVec, int topK) {
        List<Double> queryVecD = new ArrayList<>();
        for (Float f : queryVec) queryVecD.add(f.doubleValue());
        return client.queryCollection(collectionName, queryVecD, topK);
    }

    /**
     * Query top K documents using text input
     */
    public String querySimilar(String textQuery, int topK) {
        // Convert text to dummy embedding (replace with real embedding)
        List<Float> embedding = new ArrayList<>();
        embedding.add(0.1f); embedding.add(0.2f); embedding.add(0.3f);

        return queryMemory(embedding, topK);
    }
}
