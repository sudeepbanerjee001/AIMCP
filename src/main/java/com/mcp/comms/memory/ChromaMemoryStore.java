package com.mcp.comms.memory;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * ChromaMemoryStore handles low-level memory storage and retrieval.
 */
@Component
public class ChromaMemoryStore {

    // Simulated in-memory store
    private final List<StoredMessage> store = new ArrayList<>();

    // Convert List<Float> embeddings to List<Double> for querying
    public List<Message> getSimilarMessages(List<Float> embedding, int topK) {
        List<Double> queryEmbedding = embedding.stream()
                .map(Float::doubleValue)
                .collect(Collectors.toList());
        // Replace with actual similarity search logic
        System.out.println("Querying similar messages with embedding: " + queryEmbedding);
        return List.of(); // Placeholder
    }

    /**
     * Save a message with metadata
     */
    public void saveMessage(String summary, Map<String, String> metadata) {
        store.add(new StoredMessage(summary, metadata));
        System.out.println("ChromaMemoryStore saved message: " + summary);
    }

    /**
     * Return top K messages (for demo)
     */
    public List<String> getTopMessages(int topK) {
        return store.stream()
                .limit(topK)
                .map(StoredMessage::getSummary)
                .toList();
    }

    // Inner class to hold message + metadata
    private static class StoredMessage {
        private final String summary;
        private final Map<String, String> metadata;

        public StoredMessage(String summary, Map<String, String> metadata) {
            this.summary = summary;
            this.metadata = metadata;
        }

        public String getSummary() {
            return summary;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }
    }
}
