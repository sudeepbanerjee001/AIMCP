package com.mcp.comms.memory;

public class Summarizer {
    public String summarize(String text) {
        if (text.length() > 100) {
            return text.substring(0, 100) + "...";
        }
        return text;
    }
}
