package com.mcp.comms.embedding;

import java.util.ArrayList;
import java.util.List;

/**
 * Dummy embedder used for testing. Replace with real embeddings later.
 */
public class DummyEmbeddingService implements EmbeddingService {
    @Override
    public List<Float> getEmbedding(String text) {
        // Simple deterministic lightweight vector for testing
        List<Float> v = new ArrayList<>();
        // cap vector size to, say, 16 dims for safe handling by Chroma (Chroma accepts any float arrays)
        int dims = 16;
        for (int i = 0; i < dims; i++) {
            float val = (i < text.length()) ? (text.charAt(i) % 100) / 100.0f : 0.0f;
            v.add(val);
        }
        return v;
    }
}
