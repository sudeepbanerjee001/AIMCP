package com.mcp.comms.memory;

import java.util.List;

public interface EmbeddingService {
    List<Float> getEmbedding(String text);
}
