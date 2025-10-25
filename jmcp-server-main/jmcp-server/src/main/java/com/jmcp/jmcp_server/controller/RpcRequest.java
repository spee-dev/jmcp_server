package com.jmcp.jmcp_server.controller;

public class RpcRequest {
    private String jsonrpc;
    private String method;
    private String id;
    private String params;

    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
}
