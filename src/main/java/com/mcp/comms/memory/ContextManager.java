package com.mcp.comms.memory;

import java.util.*;

public class ContextManager {

    private final ChromaClient chromaClient;
    private final String collectionName = "aimcp";

    public ContextManager(ChromaClient client) {
        this.chromaClient = client;
        chromaClient.createCollection(collectionName);
    }

    public void addToMemory(String sessionId, String message, List<Double> embedding) {
        String docId = sessionId + "_" + System.currentTimeMillis();

        List<Float> embeddingF = new ArrayList<>();
        for (Double d : embedding) embeddingF.add(d.floatValue());

        Map<String, String> metadata = new HashMap<>();
        chromaClient.addDocument(collectionName, docId, message, embeddingF, metadata);
    }

    public String getContext(String sessionId, List<Double> queryEmbedding, int nResults) {
        return chromaClient.queryCollection(collectionName, queryEmbedding, nResults);
    }
}
