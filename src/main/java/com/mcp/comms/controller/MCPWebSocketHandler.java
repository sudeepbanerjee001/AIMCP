package com.mcp.comms.controller;

import com.mcp.comms.llm.ContextManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * WebSocket handler — simply delegates incoming messages to ContextManager.
 * Make sure this handler is registered by WebSocketConfig (below).
 */
public class MCPWebSocketHandler extends TextWebSocketHandler {

    private final ContextManager ctxManager;

    public MCPWebSocketHandler(ContextManager ctxManager) {
        this.ctxManager = ctxManager;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Extract message payload
        String userMessage = message.getPayload();

        // Use websocket session id as the conversation/session id
        String sessionId = session.getId();

        // Delegate to ContextManager to obtain a reply
        String reply = ctxManager.handleMessage(sessionId, userMessage);

        // Send reply back
        session.sendMessage(new TextMessage(reply));
    }
}
