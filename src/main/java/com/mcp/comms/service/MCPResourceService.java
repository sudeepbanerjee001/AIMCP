package com.mcp.comms.service;

import com.mcp.comms.model.MCPResource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class MCPResourceService {
    public List<MCPResource> getAllResources() {
        List<MCPResource> resources = new ArrayList<>();
        resources.add(new MCPResource("llm:inference", "LLM Inference Service"));
        return resources;
    }
}
