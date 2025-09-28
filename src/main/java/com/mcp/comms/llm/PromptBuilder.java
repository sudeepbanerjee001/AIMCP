package com.mcp.comms.llm;

import java.util.List;

public class PromptBuilder {
    public String build(String systemPrompt,
                        List<String> memories,
                        List<String> recentHistory,
                        String newMessage) {
        String memText = (memories == null || memories.isEmpty()) ? "No relevant memory." :
                String.join("\n", memories);

        String historyText = (recentHistory == null || recentHistory.isEmpty()) ? "No recent history." :
                String.join("\n", recentHistory);

        return systemPrompt + "\n\n" +
                "Relevant Memory:\n" + memText + "\n\n" +
                "Recent History:\n" + historyText + "\n\n" +
                "User: " + newMessage + "\nAssistant:";
    }
}
