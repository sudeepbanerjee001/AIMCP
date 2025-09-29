package com.mcp.comms.controller;

import com.mcp.comms.memory.ContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Lazy
public class MCPWebSocketHandler extends TextWebSocketHandler {

    private final ContextManager contextManager;

    @Autowired
    public MCPWebSocketHandler(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String input = message.getPayload();
        String response = contextManager.processMessage(input);
        session.sendMessage(new TextMessage(response));
    }
}
