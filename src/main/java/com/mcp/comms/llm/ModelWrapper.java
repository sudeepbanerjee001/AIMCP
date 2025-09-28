package com.mcp.comms.llm;

public class ModelWrapper {
    public String generate(String prompt) {
        // TODO: integrate with local mistral (ollama / llama.cpp wrapper)
        return "[LLM response]: " + prompt;
    }
}
