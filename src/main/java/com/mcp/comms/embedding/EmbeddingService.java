package com.mcp.comms.embedding;

import java.util.List;

public interface EmbeddingService {
    java.util.List<Float> getEmbedding(String text);
}
