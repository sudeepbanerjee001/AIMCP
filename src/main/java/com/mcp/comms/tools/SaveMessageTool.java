package com.mcp.comms.tools;

import com.mcp.comms.model.MessageEntity;
import com.mcp.comms.repo.MessageRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SaveMessageTool implements McpTool {

    private final MessageRepository repo;

    public SaveMessageTool(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public String getName() {
        return "save_message";
    }

    @Override
    public Object execute(Map<String, Object> params) {
        String sender = (String) params.get("sender");
        String content = (String) params.get("content");

        if (sender == null || content == null) {
            return Map.of("error", "Missing required fields: sender, content");
        }

        MessageEntity saved = repo.save(new MessageEntity(sender, content));
        return Map.of(
                "id", saved.getId(),
                "sender", saved.getSender(),
                "content", saved.getContent()
        );
    }
}
