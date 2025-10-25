package com.jmcp.jmcp_server.Service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommandRegistry {

    private final Map<String, String> commandMap = new HashMap<>();
    private final String os;

    public CommandRegistry() {
        this.os = System.getProperty("os.name").toLowerCase();
        System.out.println("Detecting commands for OS: " + os);

        registerDailyApps();
        detectSystemCommands();
        System.out.println("Registered " + commandMap.size() + " commands.");
    }

    // Daily-use applications
    private void registerDailyApps() {
        if (os.contains("win")) {
            commandMap.put("chrome", "chrome.exe");
            commandMap.put("edge", "msedge.exe");
            commandMap.put("file explorer", "explorer.exe");
            commandMap.put("calculator", "calc.exe");
            commandMap.put("notepad", "notepad.exe");
            commandMap.put("paint", "mspaint.exe");
            commandMap.put("whatsapp", "WhatsApp.exe");
            commandMap.put("word", "WINWORD.EXE");
            commandMap.put("excel", "EXCEL.EXE");
            commandMap.put("powerpoint", "POWERPNT.EXE");
            commandMap.put("vscode", "code.exe");
            commandMap.put("postman", "Postman.exe");
            commandMap.put("calendar", "outlookcal.exe");
            commandMap.put("clock", "ms-clock:");
        } else if (os.contains("mac")) {
            commandMap.put("chrome", "open -a \"Google Chrome\"");
            commandMap.put("edge", "open -a \"Microsoft Edge\"");
            commandMap.put("file explorer", "open -a Finder");
            commandMap.put("calculator", "open -a Calculator");
            commandMap.put("notepad", "open -a TextEdit");
            commandMap.put("paint", "open -a Paintbrush");
            commandMap.put("whatsapp", "open -a WhatsApp");
            commandMap.put("word", "open -a \"Microsoft Word\"");
            commandMap.put("excel", "open -a \"Microsoft Excel\"");
            commandMap.put("powerpoint", "open -a \"Microsoft PowerPoint\"");
            commandMap.put("vscode", "open -a \"Visual Studio Code\"");
            commandMap.put("postman", "open -a Postman");
            commandMap.put("calendar", "open -a Calendar");
            commandMap.put("clock", "open -a Clock");
        } else if (os.contains("nix") || os.contains("nux")) {
            commandMap.put("chrome", "google-chrome");
            commandMap.put("edge", "microsoft-edge");
            commandMap.put("file explorer", "nautilus");
            commandMap.put("calculator", "gnome-calculator");
            commandMap.put("notepad", "gedit");
            commandMap.put("paint", "pinta");
            commandMap.put("whatsapp", "whatsapp-nativefier");
            commandMap.put("word", "libreoffice --writer");
            commandMap.put("excel", "libreoffice --calc");
            commandMap.put("powerpoint", "libreoffice --impress");
            commandMap.put("vscode", "code");
            commandMap.put("postman", "postman");
            commandMap.put("calendar", "gnome-calendar");
            commandMap.put("clock", "gnome-clocks");
        }
    }

    // Scan PATH directories
    private void detectSystemCommands() {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) return;
        String separator = os.contains("win") ? ";" : ":";

        for (String pathDir : pathEnv.split(separator)) {
            File folder = new File(pathDir);
            if (!folder.exists() || !folder.isDirectory()) continue;

            File[] files = folder.listFiles();
            if (files == null) continue;

            for (File f : files) {
                if (f.isFile() && (os.contains("win") ? f.getName().endsWith(".exe") : f.canExecute())) {
                    String cmd = f.getName().toLowerCase();
                    if (os.contains("win")) cmd = cmd.replace(".exe", "");
                    commandMap.putIfAbsent(cmd, f.getAbsolutePath());
                }
            }
        }
    }

    // Public API
    public String getCommand(String name) {
        if (name == null) return null;
        name = name.toLowerCase();
        return commandMap.get(name);
    }

    public boolean isCommandAvailable(String name) {
        return getCommand(name) != null;
    }

    public Map<String, String> getAllCommands() {
        return commandMap;
    }

    public String getOperatingSystem() {
        return os;
    }

    public int getCommandCount() {
        return commandMap.size();
    }
}
