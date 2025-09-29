package com.mcp.comms.memory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChromaClient {

    private final String baseUrl;

    public ChromaClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Add a single document (embedding + metadata) to a collection.
     *
     * @param collectionName The collection name in Chroma.
     * @param id             Unique document ID.
     * @param embedding      The embedding vector as List<Double>.
     * @param metadata       Metadata associated with the document.
     * @return Response from Chroma server.
     */
    public String addToCollection(String collectionName, String id, List<Double> embedding, String metadata) {
        try {
            // Build the URL
            String url = baseUrl + "/collections/" + collectionName + "/add";

            // Build the document payload
            Map<String, Object> document = new HashMap<>();
            document.put("id", id);
            document.put("embedding", embedding);
            document.put("metadata", metadata);

            // Build the request body
            Map<String, Object> body = new HashMap<>();
            body.put("documents", Collections.singletonList(document));

            // Make the HTTP POST request (pseudo-code; replace with your actual HTTP client)
            String response = postJson(url, body);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Query a collection for top-K similar embeddings.
     *
     * @param collectionName The collection name in Chroma.
     * @param queryEmbedding Query embedding vector as List<Double>.
     * @param topK           Number of results to return.
     * @return Response from Chroma server.
     */
    public String queryCollection(String collectionName, List<Double> queryEmbedding, int topK) {
        try {
            String url = baseUrl + "/collections/" + collectionName + "/query";

            Map<String, Object> body = new HashMap<>();
            body.put("query_embeddings", Collections.singletonList(queryEmbedding));
            body.put("n_results", topK);

            String response = postJson(url, body);

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pseudo-method to send a JSON POST request. Replace with your actual HTTP client code.
     */
    private String postJson(String url, Map<String, Object> body) {
        // TODO: Implement HTTP POST logic (e.g., using HttpClient, OkHttp, or RestTemplate)
        // For now, just return a dummy response
        return "{\"status\":\"success\"}";
    }
}
