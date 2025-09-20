package com.mcp.comms.llm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class LlmManager {

    @Autowired
    private List<LlmAdapter> adapters;

    public String generate(String model, String prompt, Map<String,Object> options) throws Exception {
        for (LlmAdapter a : adapters) {
            if (a.supports(model)) return a.generate(model, prompt, options);
        }
        return "No adapter found for model: "+model;
    }
}
