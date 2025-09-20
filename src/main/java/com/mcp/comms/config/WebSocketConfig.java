package com.mcp.comms.config;

import com.mcp.comms.service.OllamaService;
import com.mcp.comms.websocket.MCPWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final OllamaService ollamaService;

    public WebSocketConfig(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mcpWebSocketHandler(), "/mcp")
                .setAllowedOrigins("*");
    }

    @Bean
    public MCPWebSocketHandler mcpWebSocketHandler() {
        return new MCPWebSocketHandler(ollamaService);
    }
}
