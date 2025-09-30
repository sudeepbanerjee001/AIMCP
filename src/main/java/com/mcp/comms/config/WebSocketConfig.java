package com.mcp.comms.config;

import com.mcp.comms.controller.MCPWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MCPWebSocketHandler mcpWebSocketHandler;

    public WebSocketConfig(MCPWebSocketHandler mcpWebSocketHandler) {
        this.mcpWebSocketHandler = mcpWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // The client connects to ws://localhost:8080/mcp
        registry.addHandler(mcpWebSocketHandler, "/mcp")
                .setAllowedOrigins("*");
    }
}
