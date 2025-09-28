package com.mcp.comms.llm;

import com.mcp.comms.memory.MemoryManager;
import com.mcp.comms.memory.MemoryRetriever;

import java.util.*;

/**
 * Orchestrates retrieval, prompt construction, model call, and memory updates.
 */
public class ContextManager {
    private final MemoryRetriever retriever;
    private final MemoryManager memoryManager;
    private final PromptBuilder promptBuilder;
    private final ModelWrapper modelWrapper;

    // per-session short-term history (keeps small recent history)
    private final Map<String, Deque<String>> sessionHistories = new HashMap<>();
    private final int HISTORY_MAX_ITEMS = 10; // tune as needed

    public ContextManager(MemoryRetriever retriever,
                          MemoryManager memoryManager,
                          PromptBuilder promptBuilder,
                          ModelWrapper modelWrapper) {
        this.retriever = retriever;
        this.memoryManager = memoryManager;
        this.promptBuilder = promptBuilder;
        this.modelWrapper = modelWrapper;
    }

    public synchronized String handleMessage(String sessionId, String userMsg) {
        // ensure history exists
        sessionHistories.putIfAbsent(sessionId, new ArrayDeque<>());
        Deque<String> historyDeque = sessionHistories.get(sessionId);

        // build a small recent history list
        List<String> recentHistory = new ArrayList<>(historyDeque);

        // retrieve relevant long-term memories
        List<String> memories = retriever.retrieveRelevantMemories(userMsg);

        // system prompt (customize to your product)
        String systemPrompt = "You are an assistant that retains user-specific memory across sessions. Be concise.";

        // build the prompt
        String prompt = promptBuilder.build(systemPrompt, memories, recentHistory, userMsg);

        // call LLM
        String reply = modelWrapper.generate(prompt);

        // update long-term memory (summary + store)
        memoryManager.addMemory(userMsg, reply, sessionId);

        // update short-term history
        addToHistory(historyDeque, "User: " + userMsg);
        addToHistory(historyDeque, "Assistant: " + reply);

        return reply;
    }

    private void addToHistory(Deque<String> deque, String item) {
        deque.addLast(item);
        while (deque.size() > HISTORY_MAX_ITEMS) {
            deque.removeFirst();
        }
    }
}
