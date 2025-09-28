package com.mcp.comms.embedding;

import com.mcp.comms.memory.EmbeddingService;
import java.util.*;

public class DummyEmbeddingService implements EmbeddingService {
    @Override
    public List<Float> getEmbedding(String text) {
        // 🔴 Replace this later with a real embedding model (HuggingFace / OpenAI)
        return Arrays.asList(0.1f, 0.2f, 0.3f);
    }
}
