package com.mcp.comms.memory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MemoryManager {

    private static final Logger logger = LoggerFactory.getLogger(MemoryManager.class);

    public void storeUserMessage(String message) {
        logger.info("Storing user message: {}", message);
        // TODO: Save in Chroma/DB with metadata
    }

    public void storeAiMessage(String message) {
        logger.info("Storing AI response: {}", message);
        // TODO: Save in Chroma/DB with metadata
    }
}
