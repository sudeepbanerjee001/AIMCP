package com.mcp.comms.websocket;

import com.mcp.comms.service.OllamaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.IOException;

public class MCPWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(MCPWebSocketHandler.class);

    private final OllamaService ollamaService;

    public MCPWebSocketHandler(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
        log.info("✅ MCPWebSocketHandler initialized with OllamaService");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("🌐 Connection established: {}", session.getId());
        session.sendMessage(new TextMessage("✅ Connected to MCP WebSocket"));
        log.info("📤 Sent confirmation to client: {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("💬 handleMessage triggered for session: {}", session.getId());
        String userMessage = message.getPayload().toString();
        log.info("📥 Received from client [{}]: {}", session.getId(), userMessage);

        session.sendMessage(new TextMessage("Server received: " + userMessage));
        log.info("📤 Echoed back to client [{}]: {}", session.getId(), userMessage);

        log.info("➡️ Starting Ollama stream query for: {}", userMessage);
        new Thread(() -> {
            try {
                ollamaService.streamQuery(userMessage, token -> {
                    log.info("🔹 Token received from Ollama: {}", token);
                    try {
                        session.sendMessage(new TextMessage(token));
                        log.info("📤 Token sent to client [{}]: {}", session.getId(), token);
                    } catch (IOException e) {
                        log.error("❌ Failed to send token to client [{}]", session.getId(), e);
                    }
                });
                log.info("✅ Ollama stream query completed for: {}", userMessage);
            } catch (Exception e) {
                log.error("❌ Error while streaming Ollama response", e);
                try {
                    session.sendMessage(new TextMessage("[Error: " + e.getMessage() + "]"));
                    log.info("📤 Error message sent to client");
                } catch (IOException ioException) {
                    log.error("❌ Failed to send error message to client", ioException);
                }
            }
        }).start();
        log.info("🧵 Streaming thread started for session {}", session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("⚠️ WebSocket transport error on session {}: {}", session.getId(), exception.getMessage(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("🔌 Connection closed: {} with status {}", session.getId(), closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        log.info("supportsPartialMessages called -> false");
        return false;
    }
}
