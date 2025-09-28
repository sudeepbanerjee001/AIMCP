package com.mcp.comms.memory;

import java.util.*;
import java.util.stream.Collectors;

public class MemoryStore {
    private final List<MemoryEntry> store = new ArrayList<>();
    private final EmbeddingService embeddingService;

    public MemoryStore(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public void addMemory(String text, Map<String, String> metadata) {
        List<Float> vector = embeddingService.getEmbedding(text);
        store.add(new MemoryEntry(UUID.randomUUID().toString(), text, vector, metadata));
    }

    public List<MemoryEntry> querySimilar(String text, int topK) {
        List<Float> queryVec = embeddingService.getEmbedding(text);
        return store.stream()
                .sorted(Comparator.comparingDouble(e -> -cosineSimilarity(queryVec, e.getVector())))
                .limit(topK)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(List<Float> a, List<Float> b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
            dot += a.get(i) * b.get(i);
            normA += Math.pow(a.get(i), 2);
            normB += Math.pow(b.get(i), 2);
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB) + 1e-9);
    }

    public record MemoryEntry(String id, String text, List<Float> vector, Map<String, String> metadata) {}
}
