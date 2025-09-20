package com.mcp.comms.controller;

import com.mcp.comms.mistral.ChatSession;
import com.mcp.comms.mistral.ChatSessionManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final ChatSessionManager sessionManager;

    public SessionController(ChatSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Get the full history of a session.
     * @param sessionId the ID of the session
     * @return list of messages (user + assistant)
     */
    @GetMapping("/{sessionId}/history")
    public List<ChatSession.Message> getHistory(@PathVariable String sessionId) {
        ChatSession session = sessionManager.getSession(sessionId);
        return session.getHistory();
    }

    /**
     * Create a new empty session and return its ID.
     */
    @PostMapping
    public String createSession() {
        ChatSession session = sessionManager.createSession();
        return session.getSessionId();
    }
}
