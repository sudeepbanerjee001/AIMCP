package com.mcp.comms.websocket;

import com.mcp.comms.service.OllamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MCPWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(MCPWebSocketHandler.class);

    private final OllamaService ollamaService;

    public MCPWebSocketHandler(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("✅ WebSocket connected: {}", session.getId());
        session.sendMessage(new TextMessage("Connected to MCP Server"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userMessage = message.getPayload();
        log.info("📩 Received from UI: {}", userMessage);

        try {
            // Call Ollama and stream response
            ollamaService.streamQuery(userMessage, token -> {
                try {
                    log.debug("🔹 Sending token to UI: {}", token);
                    session.sendMessage(new TextMessage(token));
                } catch (Exception e) {
                    log.error("❌ Failed to send token to UI", e);
                }
            });
        } catch (Exception e) {
            log.error("❌ Error handling user message: {}", userMessage, e);
            session.sendMessage(new TextMessage("Error: " + e.getMessage()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("⚠️ WebSocket transport error on session {}", session.getId(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("🔻 WebSocket closed: {} with status {}", session.getId(), status);
    }
}
