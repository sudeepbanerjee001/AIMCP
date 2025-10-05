package com.mcp.comms.controller;

import com.mcp.comms.memory.MemoryManager;
import com.mcp.comms.service.IntentDetectionService;
import com.mcp.comms.service.ToolInvokerService;
import com.mcp.comms.service.ToolRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

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
        System.out.println("\n[MCP FLOW] ? Received from client: " + userMessage);

        // 1️⃣ Store user message
        memoryManager.storeUserMessage(userMessage);
        System.out.println("[MCP FLOW] ? Stored user message into MemoryManager");

        // 2️⃣ Detect intent
        String detectedIntent = intentDetectionService.detectIntent(userMessage);
        System.out.println("[MCP FLOW] ? Intent detected: " + detectedIntent);

        // 3️⃣ Identify the tool for the intent
        Map<String, Object> toolInfo = toolRegistryService.findToolByIntent(detectedIntent);
        if (toolInfo == null) {
            session.sendMessage(new TextMessage("Sorry, I couldn't find a suitable tool for that request."));
            return;
        }

        System.out.println("[MCP FLOW] ? Selected tool -> name: " + toolInfo.get("toolName"));

        // 4️⃣ Extract city dynamically from user message
        String city = extractCityFromMessage(userMessage);
        System.out.println("[MCP FLOW] ?? Extracted city: " + city);

        // 5️⃣ Prepare payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("userMessage", userMessage);
        payload.put("city", city);
        payload.put("action", detectedIntent);
        payload.put("sessionId", session.getId());
        payload.put("timestamp", System.currentTimeMillis());

        System.out.println("[MCP FLOW] ? Payload prepared for tool: " + payload);

        // 6️⃣ Invoke tool
        Map<String, Object> toolResponseMap = toolInvokerService.invokeTool(
                (String) toolInfo.get("toolName"),
                (String) toolInfo.get("endpoint"),
                payload
        );

        // 7️⃣ Convert numeric string values to proper types if Weather Forecast Tool
        if ("Weather Forecast Tool".equals(toolInfo.get("toolName"))) {
            toolResponseMap = parseNumericValues(toolResponseMap);
        }

        // 8️⃣ Store AI response
        memoryManager.storeAiMessage(toolResponseMap.toString());
        System.out.println("[MCP FLOW] ? Stored tool response into MemoryManager");

        // 9️⃣ Send back to client
        session.sendMessage(new TextMessage(toolResponseMap.toString()));
        System.out.println("[MCP FLOW] ? Sent tool response back to client (session=" + session.getId() + ")");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        System.out.println("[MCP FLOW] 🔒 WebSocket connection closed. Session ID: " + session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("[MCP FLOW] ❌ WebSocket transport error: " + exception.getMessage());
    }

    // Helper method to extract city from user message
    private String extractCityFromMessage(String message) {
        // Simple regex-based approach
        // Looks for "at <City>" or "in <City>" patterns
        String city = "Unknown";
        String lowerMsg = message.toLowerCase();
        if (lowerMsg.contains(" at ")) {
            city = message.substring(lowerMsg.indexOf(" at ") + 4).split("[,?]")[0].trim();
        } else if (lowerMsg.contains(" in ")) {
            city = message.substring(lowerMsg.indexOf(" in ") + 4).split("[,?]")[0].trim();
        }
        return city;
    }

    // Helper method to parse numeric string values to Double
    private Map<String, Object> parseNumericValues(Map<String, Object> response) {
        Map<String, Object> parsed = new HashMap<>();
        for (String key : response.keySet()) {
            Object value = response.get(key);
            if (value instanceof String) {
                String strVal = (String) value;
                try {
                    Double num = Double.parseDouble(strVal);
                    parsed.put(key, num);
                } catch (NumberFormatException e) {
                    parsed.put(key, strVal); // keep as string if not numeric
                }
            } else {
                parsed.put(key, value);
            }
        }
        return parsed;
    }
}
