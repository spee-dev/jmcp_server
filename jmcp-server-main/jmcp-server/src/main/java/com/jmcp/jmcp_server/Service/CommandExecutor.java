package com.jmcp.jmcp_server.Service;

import org.springframework.stereotype.Service;

@Service
public class CommandExecutor {

    public String execute(String action) {
        try {
            switch (action.toLowerCase()) {
                case "chrome" -> {
                    Runtime.getRuntime().exec("cmd /c start chrome");
                    return "✅ Chrome opened!";
                }
                case "notepad" -> {
                    Runtime.getRuntime().exec("notepad");
                    return "✅ Notepad opened!";
                }
                case "calculator" -> {
                    Runtime.getRuntime().exec("calc");
                    return "✅ Calculator opened!";
                }
                default -> {
                    return "❌ Unknown command: " + action;
                }
            }
        } catch (Exception e) {
            return "⚠️ Error executing: " + e.getMessage();
        }
    }
}
