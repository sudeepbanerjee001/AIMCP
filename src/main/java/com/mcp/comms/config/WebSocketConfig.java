package com.mcp.comms.config;

import com.mcp.comms.service.OllamaService;
import com.mcp.comms.websocket.MCPWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    private final OllamaService ollamaService;

    public WebSocketConfig(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
        log.info("✅ WebSocketConfig initialized with OllamaService");
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("➡️ Registering WebSocket handler at /mcp-chat");
        registry.addHandler(mcpWebSocketHandler(), "/mcp-chat").setAllowedOrigins("*");
        log.info("✅ WebSocket handler registered at /mcp-chat");
    }

    @Bean
    public WebSocketHandler mcpWebSocketHandler() {
        log.info("➡️ Creating MCPWebSocketHandler bean");
        MCPWebSocketHandler handler = new MCPWebSocketHandler(ollamaService);
        log.info("✅ MCPWebSocketHandler bean created");
        return handler;
    }
}
