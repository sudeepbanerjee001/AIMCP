package com.mcp.comms.memory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChromaMemoryStore {

    // Internal memory store: message ID -> MessageData
    private final Map<String, MessageData> memory = new ConcurrentHashMap<>();

    // Class to hold message and embedding
    private static class MessageData {
        String message;
        List<Double> embedding;

        MessageData(String message, List<Double> embedding) {
            this.message = message;
            this.embedding = embedding;
        }
    }

    /**
     * Store a message along with its embedding in memory
     *
     * @param id        Unique message ID
     * @param message   The message text
     * @param embedding The embedding vector
     */
    public void storeMessage(String id, String message, List<Double> embedding) {
        memory.put(id, new MessageData(message, embedding));
        System.out.println("Memory stored: id=" + id + ", message=" + message);
    }

    /**
     * Retrieve up to topK messages most similar to the given embedding
     *
     * @param queryEmbedding The embedding vector to compare against
     * @param topK           Maximum number of similar messages to retrieve
     * @return List of messages sorted by similarity
     */
    public List<String> getSimilarMessages(List<Double> queryEmbedding, int topK) {
        // PriorityQueue to sort by similarity (highest first)
        PriorityQueue<Map.Entry<String, MessageData>> pq = new PriorityQueue<>(
                Comparator.comparingDouble(e -> -cosineSimilarity(queryEmbedding, e.getValue().embedding))
        );

        pq.addAll(memory.entrySet());

        List<String> results = new ArrayList<>();
        int count = 0;
        while (!pq.isEmpty() && count < topK) {
            Map.Entry<String, MessageData> entry = pq.poll();
            results.add(entry.getValue().message);
            count++;
        }

        return results;
    }

    /**
     * Compute cosine similarity between two vectors
     *
     * @param vec1 First vector
     * @param vec2 Second vector
     * @return Cosine similarity
     */
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) return 0.0;

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.size(); i++) {
            dot += vec1.get(i) * vec2.get(i);
            normA += vec1.get(i) * vec1.get(i);
            normB += vec2.get(i) * vec2.get(i);
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
