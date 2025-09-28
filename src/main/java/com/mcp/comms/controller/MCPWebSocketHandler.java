package com.mcp.comms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.comms.embedding.DummyEmbeddingService;
import com.mcp.comms.llm.ContextManager;
import com.mcp.comms.llm.ModelWrapper;
import com.mcp.comms.llm.PromptBuilder;
import com.mcp.comms.memory.*;
import com.mcp.comms.model.JsonRpcRequest;
import com.mcp.comms.model.JsonRpcResponse;
import com.mcp.comms.service.MCPResourceService;
import com.mcp.comms.llm.LlmManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

@Component
public class MCPWebSocketHandler extends TextWebSocketHandler {

    private final MCPResourceService resourceService;
    private final LlmManager llmManager;
    private final ObjectMapper mapper = new ObjectMapper();

    EmbeddingService emb = new DummyEmbeddingService();
    ChromaClient chroma = new ChromaClient("http://localhost:8000");
    ChromaMemoryStore store = new ChromaMemoryStore(chroma, emb, "aimcp");
    Summarizer summarizer = new Summarizer();
    MemoryRetriever retriever = new MemoryRetriever(store);
    MemoryManager manager = new MemoryManager(store, summarizer);
    PromptBuilder builder = new PromptBuilder();
    ModelWrapper model = new ModelWrapper();
    ContextManager ctxManager = new ContextManager(retriever, manager, builder, model);

    String reply = ctxManager.handleMessage(sessionId, userMessage);

    @Autowired
    public MCPWebSocketHandler(MCPResourceService resourceService, LlmManager llmManager) {
        this.resourceService = resourceService;
        this.llmManager = llmManager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        try {
            JsonRpcRequest request = mapper.readValue(message.getPayload(), JsonRpcRequest.class);
            JsonRpcResponse response;

            if ("initialize".equals(request.getMethod())) {
                Map<String,Object> result = Map.of(
                        "serverInfo", Map.of("name", "AIMCP-Server", "version", "0.1.0"),
                        "capabilities", Map.of("resources", true, "tools", true)
                );
                response = JsonRpcResponse.success(request.getId(), result);
                session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
                return;
            }

            if ("list_resources".equals(request.getMethod())) {
                response = JsonRpcResponse.success(request.getId(),
                        Map.of("resources", resourceService.getAllResources()));
                session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
                return;
            }

            if ("call_tool".equals(request.getMethod())) {
                @SuppressWarnings("unchecked")
                Map<String,Object> params = (Map<String,Object>) request.getParams();
                String tool = params.getOrDefault("tool","llm").toString();
                if ("llm".equals(tool)) {
                    String model = params.getOrDefault("model","mistral:7b-instruct-v0.3-q8_0").toString();
                    String inputText = params.getOrDefault("input","").toString();
                    String output = llmManager.generate(model, inputText, params);
                    response = JsonRpcResponse.success(request.getId(), Map.of("output", output));
                    session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
                    return;
                }
            }


            response = JsonRpcResponse.error(request.getId(), -32601, "Method not found: "+request.getMethod());
            session.sendMessage(new TextMessage(mapper.writeValueAsString(response)));

        } catch (Exception ex) {
            JsonRpcResponse err = JsonRpcResponse.error(null, -32603, "Internal error: "+ex.getMessage());
            session.sendMessage(new TextMessage(mapper.writeValueAsString(err)));
        }
    }
}
