package com.mcp.comms.controller;

import com.mcp.comms.memory.ContextManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MCPWebSocketHandler extends TextWebSocketHandler {

    private final ContextManager contextManager;

    // Constructor injection (with lazy bean from config)
    public MCPWebSocketHandler(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String input = message.getPayload();
        // Example: echo with context
        String response = contextManager.processMessage(input);
        session.sendMessage(new TextMessage(response));
    }
}
