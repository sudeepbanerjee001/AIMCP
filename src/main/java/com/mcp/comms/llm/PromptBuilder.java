package com.mcp.comms.llm;

import java.util.List;

public class PromptBuilder {
    public String build(String systemPrompt,
                        List<String> memories,
                        List<String> recentHistory,
                        String newMessage) {
        String memText = String.join("\n", memories);
        String historyText = String.join("\n", recentHistory);

        return systemPrompt + "\n\n" +
                "Relevant Memory:\n" + memText + "\n\n" +
                "Recent History:\n" + historyText + "\n\n" +
                "User: " + newMessage + "\nAssistant:";
    }
}
