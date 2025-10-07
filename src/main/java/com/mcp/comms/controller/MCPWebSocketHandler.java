package com.mcp.comms.controller;

import com.mcp.comms.memory.MemoryManager;
import com.mcp.comms.service.ToolInvokerService;
import com.mcp.comms.service.ToolRegistryService;
import com.mcp.comms.service.IntentDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

@Controller
public class MCPWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MemoryManager memoryManager;

    @Autowired
    private ToolRegistryService toolRegistryService;

    @Autowired
    private ToolInvokerService toolInvokerService;

    @Autowired
    private IntentDetectionService intentDetectionService;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userMessage = message.getPayload();
        System.out.println("[MCP FLOW] 📨 Received from client: " + userMessage);

        // Store user message in memory
        memoryManager.storeAiMessage(userMessage);
        System.out.println("[MCP FLOW] 💾 Stored user message into MemoryManager");

        // Detect intent(s)
        List<String> detectedIntents = intentDetectionService.detectIntents(userMessage);
        String detectedIntent = detectedIntents.isEmpty() ? "unknown" : detectedIntents.get(0);
        System.out.println("[MCP FLOW LOG] 🎯 Intent detected: " + detectedIntent);

        // Extract city dynamically (basic regex, can improve with NLP)
        String city = extractCity(userMessage);
        if (city != null) System.out.println("[MCP FLOW] 🌆 Extracted city: " + city);

        // Find tool for intent
        Map<String, Object> toolInfo = toolRegistryService.getToolForIntent(detectedIntent);
        if (toolInfo == null) {
            session.sendMessage(new TextMessage("No tool available for intent: " + detectedIntent));
            return;
        }
        System.out.println("[MCP FLOW] 🧩 Selected tool -> name: " + toolInfo.get("toolName"));

        // Prepare payload for the tool
        Map<String, Object> payload = new HashMap<>();
        payload.put("userMessage", userMessage);
        payload.put("sessionId", session.getId());
        payload.put("intent", detectedIntent);
        payload.put("action", detectedIntent); // <-- important for Weather Forecast Tool
        if (city != null) payload.put("city", city);
        payload.put("timestamp", System.currentTimeMillis());

        // Invoke tool
        System.out.println("[MCP FLOW] 🚀 Invoking tool '" + toolInfo.get("toolName") + "' at " + toolInfo.get("endpoint"));
        String toolResponse = toolInvokerService.invokeTool(
                (String) toolInfo.get("toolName"),
                (String) toolInfo.get("endpoint"),
                payload
        );

        // Store tool response in memory
        memoryManager.storeAiMessage(toolResponse);
        System.out.println("[MCP FLOW] 💾 Stored tool response into MemoryManager");

        // Send tool response back to client
        session.sendMessage(new TextMessage(toolResponse));
        System.out.println("[MCP FLOW] 📤 Sent tool response back to client (session=" + session.getId() + ")");
    }

    /**
     * Basic city extraction from user message. You can replace with more robust NLP extraction.
     */
    private String extractCity(String message) {
        Pattern pattern = Pattern.compile("in ([A-Za-z ]+)(\\?|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}
