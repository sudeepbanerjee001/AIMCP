package com.mcp.comms.config;

import com.mcp.comms.controller.MCPWebSocketHandler;
import com.mcp.comms.embedding.DummyEmbeddingService;
import com.mcp.comms.llm.ContextManager;
import com.mcp.comms.llm.ModelWrapper;
import com.mcp.comms.llm.PromptBuilder;
import com.mcp.comms.memory.ChromaClient;
import com.mcp.comms.memory.ChromaMemoryStore;
import com.mcp.comms.memory.MemoryManager;
import com.mcp.comms.memory.MemoryRetriever;
import com.mcp.comms.memory.Summarizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Registering handler at /mcp (same as README)
        registry.addHandler(mcpWebSocketHandler(), "/mcp")
                .setAllowedOrigins("*");
    }

    @Bean
    public MCPWebSocketHandler mcpWebSocketHandler() {
        return new MCPWebSocketHandler(contextManager());
    }

    // Core beans: ContextManager + dependencies

    @Bean
    public ContextManager contextManager() {
        MemoryRetriever retriever = new MemoryRetriever(chromaMemoryStore());
        MemoryManager manager = new MemoryManager(chromaMemoryStore(), summarizer());
        PromptBuilder promptBuilder = new PromptBuilder();
        ModelWrapper modelWrapper = new ModelWrapper();
        return new ContextManager(retriever, manager, promptBuilder, modelWrapper);
    }

    @Bean
    public ChromaMemoryStore chromaMemoryStore() {
        // ChromaClient points to running chroma REST server
        ChromaClient client = new ChromaClient("http://localhost:8000");
        return new ChromaMemoryStore(client, dummyEmbeddingService(), "aimcp");
    }

    @Bean
    public DummyEmbeddingService dummyEmbeddingService() {
        return new DummyEmbeddingService();
    }

    @Bean
    public Summarizer summarizer() {
        return new Summarizer();
    }
}
