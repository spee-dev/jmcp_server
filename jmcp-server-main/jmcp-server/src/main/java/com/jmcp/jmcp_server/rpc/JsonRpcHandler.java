package com.jmcp.jmcp_server.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmcp.jmcp_server.Service.CommandRegistry;
import com.jmcp.jmcp_server.Service.BrowserService;
import com.jmcp.jmcp_server.rpc.model.JsonRpcError;
import com.jmcp.jmcp_server.rpc.model.JsonRpcRequest;
import com.jmcp.jmcp_server.rpc.model.JsonRpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonRpcHandler {

    @Autowired
    private CommandRegistry registry;

    @Autowired
    private BrowserService browserService;

    private final ObjectMapper mapper = new ObjectMapper();

    public String handle(String jsonRequest) {
        try {
            JsonRpcRequest request = mapper.readValue(jsonRequest, JsonRpcRequest.class);
            JsonRpcResponse response = processRequest(request);
            return mapper.writeValueAsString(response);
        } catch (Exception e) {
            return errorResponse("Parse error", -32700, null);
        }
    }

    private JsonRpcResponse processRequest(JsonRpcRequest req) {
        if (!"2.0".equals(req.getJsonrpc())) {
            return new JsonRpcResponse("2.0", null, new JsonRpcError(-32600,"Invalid Request",null), req.getId());
        }

        String method = req.getMethod();
        Object result = null;
        JsonRpcError error = null;

        try {
            switch (method) {
                case "ping": result = "pong"; break;
                case "echo": result = req.getParams(); break;
                case "listCommands": result = listAllCommands(); break;
                case "runCommand":
                    if(req.getParams() instanceof String) {
                        String cmd = (String) req.getParams();
                        result = executeCommand(cmd, null);
                    } else if(req.getParams() instanceof Map) {
                        Map<String,Object> map = (Map<String,Object>) req.getParams();
                        String name = (String) map.get("name");
                        List<String> args = map.get("args") != null ? (List<String>) map.get("args") : null;
                        result = executeCommand(name, args);
                    } else {
                        error = new JsonRpcError(-32602,"Invalid params",null);
                    }
                    break;
                default:
                    error = new JsonRpcError(-32601,"Method not found: " + method, null);
            }
        } catch(Exception e) {
            error = new JsonRpcError(-32000,"Server error", e.getMessage());
        }

        return new JsonRpcResponse("2.0", result, error, req.getId());
    }

    private Map<String, Object> listAllCommands() {
        Map<String, Object> commandInfo = new HashMap<>();
        commandInfo.put("operatingSystem", registry.getOperatingSystem());
        commandInfo.put("commands", registry.getAllCommands());
        return commandInfo;
    }

    private String executeCommand(String commandName, List<String> args) {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            // Get full path from CommandRegistry
            String cmd = registry.getCommand(commandName);
            if (cmd == null) return "Command not found: " + commandName;

            // Browser special case
            if ("browser".equalsIgnoreCase(cmd)) {
                if (args == null || args.isEmpty()) return "No website provided!";
                String website = args.get(0);
                browserService.openWebsite(website);
                return "Opening website: " + website;
            }

            // Normal system command
            List<String> commandList = new ArrayList<>();
            if (os.contains("win")) {
                commandList.add("cmd"); commandList.add("/c"); commandList.add("start"); commandList.add("");
                String quotedCmd = cmd.contains(" ") ? "\"" + cmd + "\"" : cmd;
                commandList.add(quotedCmd);
                if (args != null) args.forEach(arg -> commandList.add(arg.contains(" ") ? "\"" + arg + "\"" : arg));
            } else if (os.contains("mac")) {
                if (cmd.startsWith("open -a")) {
                    commandList.add("sh"); commandList.add("-c"); commandList.add(cmd);
                } else {
                    commandList.add(cmd);
                    if (args != null) commandList.addAll(args);
                }
            } else {
                commandList.add(cmd);
                if (args != null) commandList.addAll(args);
            }

            new ProcessBuilder(commandList).start();
            return "Executed command: " + commandName;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to execute command: " + commandName + " — " + e.getMessage();
        }
    }

    private String errorResponse(String message, int code, String id) {
        try {
            JsonRpcResponse res = new JsonRpcResponse("2.0", null, new JsonRpcError(code,message,null), id);
            return mapper.writeValueAsString(res);
        } catch(Exception e) {
            return "{\"jsonrpc\":\"2.0\",\"error\":{\"code\":-32000,\"message\":\"Internal Error\"}}";
        }
    }
}
