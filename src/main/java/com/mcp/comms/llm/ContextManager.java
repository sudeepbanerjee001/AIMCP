package com.mcp.comms.llm;

import com.mcp.comms.memory.*;

import java.util.*;

public class ContextManager {
    private final MemoryRetriever retriever;
    private final MemoryManager memoryManager;
    private final PromptBuilder promptBuilder;
    private final ModelWrapper modelWrapper;
    private final Map<String, List<String>> sessionHistories = new HashMap<>();

    public ContextManager(MemoryRetriever retriever,
                          MemoryManager memoryManager,
                          PromptBuilder promptBuilder,
                          ModelWrapper modelWrapper) {
        this.retriever = retriever;
        this.memoryManager = memoryManager;
        this.promptBuilder = promptBuilder;
        this.modelWrapper = modelWrapper;
    }

    public String handleMessage(String sessionId, String userMsg) {
        sessionHistories.putIfAbsent(sessionId, new ArrayList<>());
        List<String> history = sessionHistories.get(sessionId);

        // 1. retrieve memory
        List<String> memories = retriever.retrieveRelevantMemories(userMsg);

        // 2. build prompt
        String systemPrompt = "You are a helpful assistant with memory.";
        String prompt = promptBuilder.build(systemPrompt, memories, history, userMsg);

        // 3. call model
        String reply = modelWrapper.generate(prompt);

        // 4. update memory
        memoryManager.addMemory(userMsg, reply, sessionId);

        // 5. update history
        history.add("User: " + userMsg);
        history.add("Assistant: " + reply);

        return reply;
    }
}
