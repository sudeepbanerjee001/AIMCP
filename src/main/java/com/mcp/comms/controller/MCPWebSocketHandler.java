package com.mcp.comms.controller;

import com.mcp.comms.memory.ChromaClient;
import com.mcp.comms.memory.ContextManager;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

public class MCPWebSocketHandler extends TextWebSocketHandler {

    private final ContextManager ctxManager;

    public MCPWebSocketHandler() {
        ChromaClient client = new ChromaClient("http://127.0.0.1:8000/api/v2");
        this.ctxManager = new ContextManager(client);
    }

    public MCPWebSocketHandler(ContextManager contextManager) {
        this.ctxManager = contextManager;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Server: Connected to client: " + session.getId());
        session.sendMessage(new TextMessage("Server: ✅ Connected to MCP WebSocket"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userMessage = message.getPayload();
        String sessionId = session.getId();

        System.out.println("Server received: " + userMessage);

        // Step 1: Generate embedding for user message
        List<Double> embedding = generateEmbedding(userMessage);

        // Step 2: Store message + embedding in Chroma memory
        ctxManager.addToMemory(sessionId, userMessage, embedding);

        // Step 3: Retrieve relevant context from memory
        String contextJson = ctxManager.getContext(sessionId, embedding, 5);

        // Step 4: Send context back to client (can integrate with LLM for response)
        session.sendMessage(new TextMessage(contextJson));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Server: Connection closed: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Server: Transport error: " + exception.getMessage());
    }

    /**
     * Placeholder embedding generator.
     * Replace this method with actual LLM or embedding service integration.
     */
    private List<Double> generateEmbedding(String message) {
        // Example: static dummy embedding
        return Arrays.asList(0.1, 0.2, 0.3);
    }
}
