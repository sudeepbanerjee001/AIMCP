package com.mcp.comms.memory;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class ChromaClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public ChromaClient(String baseUrl) {
        this.baseUrl = baseUrl; // e.g., "http://127.0.0.1:8000/api/v2"
        this.restTemplate = new RestTemplate();
    }

    public void createCollection(String name) {
        String url = baseUrl + "/collections";
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public void addDocument(String collection, String id, String document, List<Float> embedding, Map<String, String> metadata) {
        // Convert Float -> Double
        List<Double> embeddingD = new ArrayList<>();
        for (Float f : embedding) {
            embeddingD.add(f.doubleValue());
        }

        String url = baseUrl + "/collections/" + collection + "/add";

        Map<String, Object> body = new HashMap<>();
        body.put("ids", Collections.singletonList(id));
        body.put("documents", Collections.singletonList(document));
        body.put("embeddings", Collections.singletonList(embeddingD));
        body.put("metadatas", Collections.singletonList(metadata != null ? metadata : new HashMap<>()));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }

    public String queryCollection(String collection, List<Double> queryEmbedding, int nResults) {
        String url = baseUrl + "/collections/" + collection + "/query";

        Map<String, Object> body = new HashMap<>();
        body.put("query_embeddings", Collections.singletonList(queryEmbedding));
        body.put("n_results", nResults);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
