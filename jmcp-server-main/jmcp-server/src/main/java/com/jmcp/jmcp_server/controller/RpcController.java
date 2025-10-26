package com.jmcp.jmcp_server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmcp.jmcp_server.Service.CommandRegistry;
import com.jmcp.jmcp_server.Service.FileService;
import com.jmcp.jmcp_server.Service.GeminiAIService;
import com.jmcp.jmcp_server.Service.BrowserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rpc")
public class RpcController {

    @Autowired
    private CommandRegistry commandRegistry;

    @Autowired
    private GeminiAIService geminiAIService;

    @Autowired
    private BrowserService browserService;

    @Autowired
    private FileService fileService;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping
    public String handleRpc(@RequestBody String body) {
        try {
            JsonNode request = mapper.readTree(body);
            String method = request.path("method").asText();
            String id = request.path("id").asText();

            return switch (method) {
                case "ping" -> success(id, "pong");
                case "runCommand" -> runCommand(id, request.path("params").asText());
                case "parseAndRun" -> parseAndRun(id, request.path("params").asText());
                case "listCommands" -> listCommands(id);
                default -> error(id, "Unknown method: " + method);
            };
        } catch (Exception e) {
            return error("error", e.getMessage());
        }
    }

    // ---------------- Run command directly ----------------
    private String runCommand(String id, String commandName) {
        String cmd = commandRegistry.getCommand(commandName);
        if (cmd == null) return error(id, "Command not found: " + commandName);

        try {
            String os = commandRegistry.getOperatingSystem();
            List<String> commandList = new ArrayList<>();

            if (os.contains("win")) {
                commandList.add("cmd");
                commandList.add("/c");
                commandList.add("start");
                commandList.add("");
                commandList.add(cmd.contains(" ") ? "\"" + cmd + "\"" : cmd);
            } else if (os.contains("mac")) {
                commandList.add("sh");
                commandList.add("-c");
                commandList.add(cmd);
            } else {
                commandList.add(cmd);
            }

            new ProcessBuilder(commandList).start();
            return success(id, "✅ " + commandName + " launched successfully!");
        } catch (IOException e) {
            return error(id, "Failed to execute: " + e.getMessage());
        }
    }

    // ---------------- Parse natural language & run ----------------
    private String parseAndRun(String id, String userInput) {
        try {
            // Normalize input
            String normalizedInput = userInput.toLowerCase().replaceAll("[^a-z0-9 ]", "").trim();

            // Ask Gemini to extract command
            String action = geminiAIService.extractAction(normalizedInput);

            // Handle "open_website" action
            if (action.startsWith("open_website:")) {
                String url = action.substring("open_website:".length());
                browserService.openWebsite(url);
                return success(id, "Opened " + url + " in browser!");
            }

            // Handle "search" action
            if (action.startsWith("search:")) {
                String[] parts = action.split(":", 3);
                if (parts.length == 3) {
                    String engine = parts[1];
                    String query = parts[2];

                    // Ask AI to find the best URL
                    String bestUrl = geminiAIService.findBestUrl(query);

                    if (bestUrl != null && !bestUrl.isEmpty()) {
                        browserService.openWebsite(bestUrl);
                        return success(id, "Opening best result for '" + query + "'");
                    } else {
                        // Fallback to regular search
                        browserService.performSearch(engine, query);
                        return success(id, "Searching for '" + query + "' on " + engine);
                    }
                }
            }

            // Handle file operations
            if (action.contains(":")) {
                String[] parts = action.split(":", 2);
                String operation = parts[0];
                String path = parts[1];

                switch (operation) {
                    case "create_file":
                        fileService.createFile(path);
                        return success(id, "✅ File created: " + new File(path).getAbsolutePath());
                    case "create_folder":
                        fileService.createFolder(path);
                        return success(id, "✅ Folder created: " + new File(path).getAbsolutePath());
                    case "search_file":
                        List<String> searchResults = fileService.searchFile(path, "C:/");
                        return success(id, "Found files: " + searchResults);
                    case "list_files":
                        List<String> fileList = fileService.listFiles(path);
                        return success(id, "Files in " + path + ": " + fileList);
                    case "delete_file":
                        fileService.deleteFile(path);
                        return success(id, "File deleted: " + path);
                    case "open_file":
                        fileService.openFile(path);
                        return success(id, "Opening file: " + path);
                }
            }

            // Fallback: fuzzy match if Gemini fails
            if ("unknown".equals(action)) {
                action = fuzzyMatch(normalizedInput);
            }

            if (action == null) {
                return error(id, " Could not understand command. Try 'help' for available commands.");
            }

            if (!commandRegistry.isCommandAvailable(action)) {
                return error(id, "Command '" + action + "' not found in system registry.");
            }

            return runCommand(id, action);

        } catch (Exception e) {
            return error(id, "⚠️ AI parsing error: " + e.getMessage());
        }
    }

    // ---------------- List all commands ----------------
    private String listCommands(String id) {
        Map<String, String> commands = commandRegistry.getAllCommands();
        String list = commands.keySet().stream().sorted().collect(Collectors.joining(", "));
        return success(id, "Found " + commands.size() + " commands:\n" + list);
    }

    // ---------------- Helpers ----------------
    private String success(String id, String result) {
        return String.format("{\"jsonrpc\":\"2.0\",\"result\":\"%s\",\"id\":\"%s\"}",
                escapeJson(result), id);
    }

    private String error(String id, String message) {
        return String.format("{\"jsonrpc\":\"2.0\",\"error\":{\"message\":\"%s\"},\"id\":\"%s\"}",
                escapeJson(message), id);
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ---------------- Fuzzy match fallback ----------------
    private String fuzzyMatch(String input) {
        int minDistance = Integer.MAX_VALUE;
        String bestMatch = null;

        for (String cmd : commandRegistry.getAllCommands().keySet()) {
            int dist = levenshteinDistance(input, cmd.toLowerCase());
            if (dist < minDistance && dist <= 3) { // allow minor typos
                minDistance = dist;
                bestMatch = cmd;
            }
        }
        return bestMatch;
    }

    // Levenshtein distance for typo tolerance
    private int levenshteinDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
