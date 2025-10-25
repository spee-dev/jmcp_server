package com.jmcp.jmcp_server.rpc.model;

import lombok.Data;

@Data
public class JsonRpcRequest {
    private String jsonrpc;
    private String method;
    private Object params;
    private String id;
}
