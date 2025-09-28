package com.mcp.comms.memory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class ChromaClient {
    private final String baseUrl;
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    public ChromaClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();
        this.mapper = new ObjectMapper();
    }

    public void addDocument(String collection, String id, String text,
                            List<Float> embedding, Map<String, String> metadata) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("ids", Collections.singletonList(id));
        payload.put("documents", Collections.singletonList(text));
        payload.put("embeddings", Collections.singletonList(embedding));
        payload.put("metadatas", Collections.singletonList(metadata));

        String url = baseUrl + "/collections/" + collection + "/add";
        restTemplate.postForEntity(url, payload, String.class);
    }

    public List<String> query(String collection, List<Float> queryEmbedding, int topK) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("query_embeddings", Collections.singletonList(queryEmbedding));
        payload.put("n_results", topK);

        String url = baseUrl + "/collections/" + collection + "/query";

        ResponseEntity<String> response = restTemplate.postForEntity(url, payload, String.class);

        try {
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode docs = root.get("documents").get(0);
            List<String> results = new ArrayList<>();
            for (JsonNode d : docs) {
                results.add(d.asText());
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Chroma query parse error", e);
        }
    }
}
