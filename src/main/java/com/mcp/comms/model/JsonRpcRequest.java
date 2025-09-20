package com.mcp.comms.model;

import java.util.Map;

public class JsonRpcRequest {
    private String jsonrpc;
    private String method;
    private Map<String, Object> params;
    private int id;

    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
