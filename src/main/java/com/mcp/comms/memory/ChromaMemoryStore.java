package com.mcp.comms.memory;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class ChromaMemoryStore {

    private final ChromaClient client;

    public ChromaMemoryStore(ChromaClient client) {
        this.client = client;
    }

    /**
     * Search the collection using an embedding query.
     */
    public String search(String collectionName, List<Float> queryEmbedding, int topK) {
        try {
            String jsonResponse = client.queryCollection(collectionName, queryEmbedding, topK);
            return jsonResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Store an embedding in a collection.
     */
    public String store(String collectionName, String id, List<Double> embedding, String metadata) {
        try {
            String jsonResponse = client.addToCollection(collectionName, id, embedding, metadata);
            return jsonResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * New method to match ContextManager call
     */
    public String addMemory(String message, String id, List<Double> embedding, Map<String, String> metadata) {
        try {
            // Convert metadata map to string if needed
            String metadataStr = (metadata != null) ? metadata.toString() : "";
            return store("default", id, embedding, metadataStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> queryMemory(List<Float> queryEmbedding, int topK) {

        return null;
    }
}
