package com.mcp.comms.tools;

import com.mcp.comms.model.MessageEntity;
import com.mcp.comms.repo.MessageRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FetchMessagesTool implements McpTool {

    private final MessageRepository repo;

    public FetchMessagesTool(MessageRepository repo) {
        this.repo = repo;
    }

    @Override
    public String getName() {
        return "fetch_messages";
    }

    @Override
    public Object execute(Map<String, Object> params) {
        List<MessageEntity> messages = repo.findAll();

        return messages.stream()
                .map(m -> Map.of(
                        "id", m.getId(),
                        "sender", m.getSender(),
                        "content", m.getContent()
                ))
                .collect(Collectors.toList());
    }
}
