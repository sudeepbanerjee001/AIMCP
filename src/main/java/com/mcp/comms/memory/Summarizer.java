package com.mcp.comms.memory;

/**
 * Simple summarizer placeholder. Replace with model-based summarization later.
 */
public class Summarizer {
    public String summarize(String text) {
        if (text == null) return "";
        // Cheap truncation summarizer for now
        if (text.length() > 200) {
            return text.substring(0, 200) + "...";
        }
        return text;
    }
}
