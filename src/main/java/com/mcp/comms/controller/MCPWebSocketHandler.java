package com.mcp.comms.controller;

import com.mcp.comms.memory.ContextManager;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

@Component
public class MCPWebSocketHandler extends TextWebSocketHandler {

    private final ContextManager contextManager;

    public MCPWebSocketHandler(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New WebSocket session established: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userMessage = message.getPayload();
        System.out.println("Received WebSocket message: " + userMessage);

        // Echo the user message back to the UI (optional)
        session.sendMessage(new TextMessage("You: " + userMessage));

        // Process the message asynchronously and stream the response
        contextManager.processMessageAsync(userMessage, token -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(token));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
