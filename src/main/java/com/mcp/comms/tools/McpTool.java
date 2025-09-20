package com.mcp.comms.tools;

import java.util.Map;

public interface McpTool {
    String getName();
    Object execute(Map<String, Object> params);
}
