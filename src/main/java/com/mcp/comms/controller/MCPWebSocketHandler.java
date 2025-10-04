package com.mcp.comms.controller;

import com.mcp.comms.memory.MemoryManager;
import com.mcp.comms.service.MyAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MCPWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(MCPWebSocketHandler.class);

    private final MyAiService myAiService;
    private final MemoryManager memoryManager;

    public MCPWebSocketHandler(MyAiService myAiService, MemoryManager memoryManager) {
        this.myAiService = myAiService;
        this.memoryManager = memoryManager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            String input = message.getPayload();
            logger.info("Received from client: {}", input);

            // Step 1: Store message in memory
            memoryManager.storeUserMessage(input);

            // Step 2: Get AI-generated response
            String aiResponse = myAiService.process(input);

            // Step 3: Store AI response in memory
            memoryManager.storeAiMessage(aiResponse);

            // Step 4: Send back to client
            //String response = "AI says: " + aiResponse;
            //session.sendMessage(new TextMessage(response));
            session.sendMessage(new TextMessage(aiResponse));

        } catch (Exception e) {
            logger.error("Error in WebSocket message handling", e);
            try {
                session.sendMessage(new TextMessage("Error: " + e.getMessage()));
            } catch (Exception ex) {
                logger.error("Failed to send error message to client", ex);
            }
        }
    }
}
