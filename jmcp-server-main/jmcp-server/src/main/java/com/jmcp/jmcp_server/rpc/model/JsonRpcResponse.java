package com.jmcp.jmcp_server.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonRpcResponse {
    private String jsonrpc = "2.0";
    private Object result;
    private JsonRpcError error;
    private String id;

    public static JsonRpcResponse success(String id, Object result) {
        return new JsonRpcResponse("2.0", result, null, id);
    }

    public static JsonRpcResponse error(String id, int code, String message) {
        return new JsonRpcResponse("2.0", null, new JsonRpcError(code, message, null), id);
    }
}
