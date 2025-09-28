package com.mcp.comms.memory;

import com.mcp.comms.embedding.EmbeddingService;

import java.util.*;
import java.util.stream.Collectors;

public class MemoryStore {
    private final List<MemoryEntry> entries = new ArrayList<>();
    private final EmbeddingService embeddingService;

    public MemoryStore(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public void addMemory(String text, Map<String, String> metadata) {
        List<Float> vector = embeddingService.getEmbedding(text);
        MemoryEntry entry = new MemoryEntry(UUID.randomUUID().toString(), text, metadata, vector);
        entries.add(entry);
    }

    public List<MemoryEntry> querySimilar(String query, int topK) {
        List<Float> queryVec = embeddingService.getEmbedding(query);

        return entries.stream()
                .sorted(Comparator.comparingDouble(e -> -cosineSimilarity(queryVec, e.getVector())))
                .limit(topK)
                .collect(Collectors.toList());
    }

    private double cosineSimilarity(List<Float> v1, List<Float> v2) {
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += v1.get(i) * v1.get(i);
            norm2 += v2.get(i) * v2.get(i);
        }
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2) + 1e-10);
    }
}
