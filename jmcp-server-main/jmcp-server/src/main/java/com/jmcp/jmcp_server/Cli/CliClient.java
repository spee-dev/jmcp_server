package com.jmcp.jmcp_server.Cli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class CliClient {

    private static final String SERVER_URL = "http://localhost:8081/rpc";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        System.out.println("======================================");
        System.out.println("         JMCP Command Console");
        System.out.println("======================================");
        System.out.println("Type 'help' for commands, 'exit' to quit.");
        System.out.println("You can use natural language (e.g., 'chrome kholo', 'open calculator')\n");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("jmcp> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }

            if (input.equalsIgnoreCase("ping")) {
                String json = "{\"jsonrpc\":\"2.0\",\"method\":\"ping\",\"id\":\"cli-ping\"}";
                try {
                    String response = sendRpcRequest(json);
                    if (response.contains("\"result\":\"pong\"")) {
                        System.out.println("pong");
                    } else {
                        System.out.println("Error pinging server: " + response);
                    }
                } catch (Exception e) {
                    System.out.println("Error connecting to server: " + e.getMessage());
                }
                continue;
            }

            if (input.equalsIgnoreCase("list")) {
                String json = "{\"jsonrpc\":\"2.0\",\"method\":\"listCommands\",\"id\":\"cli-list\"}";
                try {
                    String response = sendRpcRequest(json);
                    System.out.println("Available commands: " + response);
                } catch (Exception e) {
                    System.out.println("Error connecting to server: " + e.getMessage());
                }
                continue;
            }

            // Detect "run <something>" or "open <something>"
            if (input.startsWith("run ") || input.startsWith("open ")) {
                String cmd = input.split(" ", 2)[1];
                if (cmd.contains(" ")) {
                    // Multi-word → treat as natural language
                    executeNaturalLanguageCommand(cmd);
                } else {
                    executeDirectCommand(cmd);
                }
                continue;
            }

            // Otherwise treat as natural language
            executeNaturalLanguageCommand(input);
        }

        scanner.close();
    }

    private static void executeDirectCommand(String cmd) {
        String escapedCmd = cmd.replace("\"", "\\\"");
        String json = String.format(
                "{\"jsonrpc\":\"2.0\",\"method\":\"runCommand\",\"params\":\"%s\",\"id\":\"cli-run\"}",
                escapedCmd
        );
        try {
            String response = sendRpcRequest(json);
            printRpcResponse(response);
        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    private static void executeNaturalLanguageCommand(String input) {
        String escapedInput = input.replace("\"", "\\\"");
        String json = String.format(
                "{\"jsonrpc\":\"2.0\",\"method\":\"parseAndRun\",\"params\":\"%s\",\"id\":\"cli-nlp\"}",
                escapedInput
        );
        try {
            String response = sendRpcRequest(json);
            printRpcResponse(response);
        } catch (Exception e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    private static void printRpcResponse(String response) {
        if (response.contains("\"result\":")) {
            String result = extractResult(response);
            if (result.startsWith("✅ Opened ")) {
                System.out.println(result);
            } else {
                System.out.println(result);
            }
        } else if (response.contains("\"error\":")) {
            String error = extractError(response);
            System.out.println("Error: " + error);
        } else {
            System.out.println("Unknown response from server: " + response);
        }
    }

    private static String extractResult(String response) {
        try {
            int start = response.indexOf("\"result\":") + 10;
            int end = response.indexOf("\"", start + 1);
            if (end == -1) {
                end = response.indexOf(",", start);
                if (end == -1) end = response.indexOf("}", start);
            }
            return response.substring(start, end).replace("\\\"", "\"");
        } catch (Exception e) {
            return response;
        }
    }

    private static String extractError(String response) {
        try {
            int start = response.indexOf("\"error\":") + 9;
            int end = response.indexOf("}", start);
            return response.substring(start, end);
        } catch (Exception e) {
            return response;
        }
    }

    private static String sendRpcRequest(String jsonPayload) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static void printHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("  run <tool>       - Run a registered system tool (e.g., run chrome)");
        System.out.println("  open <tool>      - Alias for run");
        System.out.println("  list             - List all registered commands");
        System.out.println("  ping             - Check if system is alive");
        System.out.println("  exit/quit        - Quit JMCP console");
        System.out.println("  help             - Show this help text");
        System.out.println("\n=== Natural Language ===");
        System.out.println("  You can also use natural language:");
        System.out.println("  - 'chrome kholo'");
        System.out.println("  - 'open calculator'");
        System.out.println("  - 'notepad run karo'");
        System.out.println("==========================\n");
    }
}
