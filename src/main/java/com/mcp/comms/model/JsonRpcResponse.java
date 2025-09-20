package com.mcp.comms.model;

public class JsonRpcResponse {
    private String jsonrpc="2.0";
    private String id;
    private Object result;
    private MCPError error;

    public static JsonRpcResponse success(String id, Object result) {
        JsonRpcResponse r = new JsonRpcResponse();
        r.id = id; r.result = result; return r;
    }

    public static JsonRpcResponse error(String id, int code, String message) {
        JsonRpcResponse r = new JsonRpcResponse();
        r.id = id; r.error = new MCPError(code,message); return r;
    }

    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    public MCPError getError() { return error; }
    public void setError(MCPError error) { this.error = error; }
}
