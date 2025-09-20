package com.mcp.comms.mistral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ChatSession holds the conversation state for one client.
 * Each session maintains a list of messages with role (user/assistant/system).
 */
public class ChatSession {

    private final String sessionId;
    private final List<Message> history = new ArrayList<>();

    public ChatSession(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    /**
     * Add a message to the session history.
     * @param role    "user", "assistant", or "system"
     * @param content message text
     */
    public void addMessage(String role, String content) {
        history.add(new Message(role, content));
    }

    /**
     * Get the full immutable history.
     */
    public List<Message> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Represents a single chat message.
     */
    public static class Message {
        public final String role;     // user / assistant / system
        public final String content;  // text content

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
