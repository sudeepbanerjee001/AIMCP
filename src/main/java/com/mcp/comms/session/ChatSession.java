package com.mcp.comms.session;

import java.util.ArrayList;
import java.util.List;

public class ChatSession {
    private final List<Message> history = new ArrayList<>();

    // Inner class to hold role + message
    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    // Add a message to history
    public void addMessage(String role, String content) {
        history.add(new Message(role, content));
    }

    // Return full history
    public List<Message> getHistory() {
        return history;
    }

    // Clear history
    public void clear() {
        history.clear();
    }
}
