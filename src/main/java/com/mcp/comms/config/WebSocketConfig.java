package com.mcp.comms.config;

import com.mcp.comms.controller.MCPWebSocketHandler;
import com.mcp.comms.memory.ContextManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ContextManager contextManager;

    // Mark ContextManager as lazy
    public WebSocketConfig(@Lazy ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    @Bean
    @Lazy
    public WebSocketHandler mcpWebSocketHandler() {
        return new MCPWebSocketHandler(contextManager);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mcpWebSocketHandler(), "/mcp").setAllowedOrigins("*");
    }
}
