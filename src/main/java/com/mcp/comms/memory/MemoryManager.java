package com.mcp.comms.memory;

import java.util.HashMap;

public class MemoryManager {
    private final ChromaMemoryStore memoryStore;
    private final Summarizer summarizer;

    public MemoryManager(ChromaMemoryStore memoryStore, Summarizer summarizer) {
        this.memoryStore = memoryStore;
        this.summarizer = summarizer;
    }

    public void addMemory(String userInput, String assistantReply, String sessionId) {
        String combined = "User: " + userInput + "\nAssistant: " + assistantReply;
        String summary = summarizer.summarize(combined);

        memoryStore.addMemory(summary, new HashMap<>() {{
            put("sessionId", sessionId);
        }});
    }
}
