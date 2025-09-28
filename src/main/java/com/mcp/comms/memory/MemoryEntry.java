package com.mcp.comms.memory;

import java.util.List;
import java.util.Map;

public class MemoryEntry {
    private final String id;
    private final String text;
    private final Map<String, String> metadata;
    private final List<Float> vector;   // ✅ added vector storage

    public MemoryEntry(String id, String text, Map<String, String> metadata, List<Float> vector) {
        this.id = id;
        this.text = text;
        this.metadata = metadata;
        this.vector = vector;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public List<Float> getVector() {
        return vector;
    }
}
