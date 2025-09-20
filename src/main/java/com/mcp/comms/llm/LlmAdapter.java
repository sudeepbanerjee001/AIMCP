package com.mcp.comms.llm;

import java.util.Map;

public interface LlmAdapter {
    boolean supports(String model);
    String generate(String model, String prompt, Map<String,Object> options) throws Exception;
}
