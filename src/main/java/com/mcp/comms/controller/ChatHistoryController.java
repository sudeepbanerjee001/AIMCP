package com.mcp.comms.controller;

import com.mcp.comms.session.ChatSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatHistoryController {

    // Temporary in-memory storage of a single session for testing
    private final ChatSession session = new ChatSession();

    // Endpoint to get chat history
    @GetMapping("/api/chat/history")
    public List<String> getChatHistory() {
        List<String> historyList = new ArrayList<>();
        for (ChatSession.Message msg : session.getHistory()) {
            historyList.add(msg.getRole() + ": " + msg.getContent());
        }
        return historyList;
    }

    // Endpoint to clear chat history
    @GetMapping("/api/chat/clear")
    public String clearChatHistory() {
        session.clear();
        return "Chat history cleared!";
    }

    // Helper to access the session (so WebSocket handler can reuse)
    public ChatSession getSession() {
        return session;
    }
}
