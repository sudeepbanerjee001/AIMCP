package com.mcp.comms.mistral;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages multiple chat sessions by sessionId.
 */
@Component
public class ChatSessionManager {

    private final Map<String, ChatSession> sessions = new ConcurrentHashMap<>();

    /**
     * Create a new session and store it.
     */
    public ChatSession createSession() {
        String id = UUID.randomUUID().toString();
        ChatSession session = new ChatSession(id);
        sessions.put(id, session);
        return session;
    }

    /**
     * Get an existing session, or create it if not found.
     */
    public ChatSession getSession(String id) {
        return sessions.computeIfAbsent(id, ChatSession::new);
    }
}
