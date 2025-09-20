package com.mcp.comms.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Configuration
@EnableWebSocket
public class MCPController implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MCPWebSocketHandler(), "/mcp").setAllowedOrigins("*");
    }

    private static class MCPWebSocketHandler extends TextWebSocketHandler {
        private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.add(session);
            session.sendMessage(new TextMessage("Connected to MCP Server"));
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            for (WebSocketSession s : sessions) {
                s.sendMessage(new TextMessage("Server received: " + payload));
            }
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
            sessions.remove(session);
        }
    }
}