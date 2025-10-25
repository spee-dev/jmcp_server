package com.jmcp.jmcp_server.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonRpcError {
    private int code;
    private String message;
    private Object data;
}
