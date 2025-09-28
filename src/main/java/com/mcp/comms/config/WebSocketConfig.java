package com.mcp.comms.config;

import com.mcp.comms.controller.MCPWebSocketHandler;
import com.mcp.comms.memory.ChromaClient;
import com.mcp.comms.memory.ContextManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mcpWebSocketHandler(), "/mcp")
                .setAllowedOrigins("*"); // allow cross-origin requests if needed
    }

    /**
     * Spring Bean for ContextManager (shared memory manager)
     */
    @Bean
    public ContextManager contextManager() {
        ChromaClient client = new ChromaClient("http://127.0.0.1:8000/api/v2");
        return new ContextManager(client);
    }

    /**
     * Spring Bean for MCPWebSocketHandler
     * Injects the ContextManager bean
     */
    @Bean
    public MCPWebSocketHandler mcpWebSocketHandler() {
        return new MCPWebSocketHandler(contextManager());
    }
}
