package com.mcp.comms.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.mcp.comms.memory.MemoryManager;
import com.mcp.comms.service.IntentDetectionService;
import com.mcp.comms.service.ToolRegistryService;
import com.mcp.comms.service.ToolInvokerService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MCPWebSocketHandler extends TextWebSocketHandler {

    private final MemoryManager memoryManager;
    private final IntentDetectionService intentDetectionService;
    private final ToolRegistryService toolRegistryService;
    private final ToolInvokerService toolInvokerService;

    @Autowired
    public MCPWebSocketHandler(MemoryManager memoryManager,
                               IntentDetectionService intentDetectionService,
                               ToolRegistryService toolRegistryService,
                               ToolInvokerService toolInvokerService) {
        this.memoryManager = memoryManager;
        this.intentDetectionService = intentDetectionService;
        this.toolRegistryService = toolRegistryService;
        this.toolInvokerService = toolInvokerService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("[MCP FLOW] ✅ WebSocket connection established. Session ID: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userMessage = message.getPayload();
        System.out.println("\n[MCP FLOW] 💬 Received from client: " + userMessage);

        // Store user message
        memoryManager.storeUserMessage(userMessage);

        // Step 1: Detect intent
        String intent = intentDetectionService.detectIntent(userMessage);
        System.out.println("[MCP FLOW] 🎯 Intent detected: " + intent);

        // Step 2: Find the appropriate tool
        var toolInfo = toolRegistryService.getToolForIntent(intent);
        if (toolInfo == null) {
            System.out.println("[MCP FLOW] ⚠️ No matching tool found for intent: " + intent);
            session.sendMessage(new TextMessage("Sorry, I couldn’t find a suitable tool for that request."));
            return;
        }

        // Step 3: Prepare payload dynamically
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", intent);
        payload.put("userMessage", userMessage);
        payload.put("sessionId", session.getId());
        payload.put("timestamp", System.currentTimeMillis());

        // Step 4: Dynamically extract city name from user message
        String city = extractCityName(userMessage);
        if (city != null) {
            payload.put("city", city);
            System.out.println("[MCP FLOW] 🏙️ Extracted city: " + city);
        } else {
            System.out.println("[MCP FLOW] ⚠️ No city found in user message. Sending request without city.");
        }

        // Step 5: Invoke tool
        String response = toolInvokerService.invokeTool(toolInfo, payload);
        System.out.println("[MCP FLOW] 📦 Tool response: " + response);

        // Step 6: Store AI response
        memoryManager.storeAiMessage(response);

        // Step 7: Send response back to client
        session.sendMessage(new TextMessage(response));
    }

    /**
     * Extracts a city name dynamically from a user message.
     * Uses regex-based named entity matching for locations.
     */
    private String extractCityName(String message) {
        // Basic cleanup
        String cleaned = message.trim();

        // Simple regex to capture a potential city name after "in", "at", "from", or "for"
        Pattern pattern = Pattern.compile("\\b(?:in|at|from|for)\\s+([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)*)");
        Matcher matcher = pattern.matcher(cleaned);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        // If no city pattern detected, try to use a list of common city indicators dynamically
        // e.g. "weather for New York City", "forecast Delhi"
        Pattern fallback = Pattern.compile("(?i)(?:weather|forecast|temperature)\\s+(?:in\\s+)?([A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+)*)");
        Matcher fallbackMatcher = fallback.matcher(cleaned);

        if (fallbackMatcher.find()) {
            return fallbackMatcher.group(1).trim();
        }

        return null;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("[MCP FLOW] ❌ WebSocket error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("[MCP FLOW] 🔒 WebSocket closed. Session ID: " + session.getId());
    }
}
