package com.jmcp.jmcp_server.Service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommandRegistry {

    private final Map<String, String> commandMap = new HashMap<>();
    private final Map<String, List<String>> aliasMap = new HashMap<>();
    private final String os;

    public CommandRegistry() {
        this.os = System.getProperty("os.name").toLowerCase();
        System.out.println("Detecting commands for OS: " + os);

        registerSystemApps();
        registerDailyApps();
        registerDevelopmentTools();
        registerMediaApps();
        detectInstalledApplications();
        detectSystemCommands();

        System.out.println("Registered " + commandMap.size() + " commands.");
    }

    // System utilities and built-in apps
    private void registerSystemApps() {
        if (os.contains("win")) {
            // System Settings
            commandMap.put("settings", "ms-settings:");
            commandMap.put("control panel", "control.exe");
            commandMap.put("task manager", "taskmgr.exe");
            commandMap.put("device manager", "devmgmt.msc");
            commandMap.put("disk management", "diskmgmt.msc");
            commandMap.put("services", "services.msc");
            commandMap.put("event viewer", "eventvwr.msc");
            commandMap.put("registry editor", "regedit.exe");
            commandMap.put("system information", "msinfo32.exe");

            // Built-in Windows Apps
            commandMap.put("camera", "microsoft.windows.camera:");
            commandMap.put("calendar", "outlookcal:");
            commandMap.put("mail", "outlookmail:");
            commandMap.put("calculator", "calc.exe");
            commandMap.put("clock", "ms-clock:");
            commandMap.put("weather", "bingweather:");
            commandMap.put("maps", "bingmaps:");
            commandMap.put("photos", "ms-photos:");
            commandMap.put("store", "ms-windows-store:");
            commandMap.put("microsoft store", "ms-windows-store:");
            commandMap.put("snipping tool", "ms-screenclip:");
            commandMap.put("screenshot", "ms-screenclip:");
            commandMap.put("voice recorder", "ms-callrecording:");
            commandMap.put("sticky notes", "ms-stickynotes:");
            commandMap.put("windows terminal", "wt.exe");
            commandMap.put("powershell", "powershell.exe");
            commandMap.put("command prompt", "cmd.exe");

            // File System
            commandMap.put("file explorer", "explorer.exe");
            commandMap.put("notepad", "notepad.exe");
            commandMap.put("wordpad", "write.exe");
            commandMap.put("paint", "mspaint.exe");

            // Network & Security
            commandMap.put("windows security", "windowsdefender:");
            commandMap.put("network connections", "ncpa.cpl");
            commandMap.put("firewall", "firewall.cpl");

            // Accessibility
            commandMap.put("magnifier", "magnify.exe");
            commandMap.put("on-screen keyboard", "osk.exe");
            commandMap.put("narrator", "narrator.exe");

            // Media
            commandMap.put("sound recorder", "soundrecorder.exe");
            commandMap.put("media player", "wmplayer.exe");

            // Add aliases
            addAlias("task manager", "taskmgr", "task mgr");
            addAlias("camera", "cam", "webcam");
            addAlias("calculator", "calc");
            addAlias("file explorer", "explorer", "files");
            addAlias("command prompt", "cmd", "terminal");
            addAlias("control panel", "control");

        } else if (os.contains("mac")) {
            // System Preferences & Utilities
            commandMap.put("settings", "open -a \"System Settings\"");
            commandMap.put("system preferences", "open -a \"System Preferences\"");
            commandMap.put("activity monitor", "open -a \"Activity Monitor\"");
            commandMap.put("disk utility", "open -a \"Disk Utility\"");
            commandMap.put("console", "open -a Console");
            commandMap.put("terminal", "open -a Terminal");

            // Built-in Apps
            commandMap.put("camera", "open -a \"Photo Booth\"");
            commandMap.put("calendar", "open -a Calendar");
            commandMap.put("mail", "open -a Mail");
            commandMap.put("calculator", "open -a Calculator");
            commandMap.put("clock", "open -a Clock");
            commandMap.put("weather", "open -a Weather");
            commandMap.put("maps", "open -a Maps");
            commandMap.put("photos", "open -a Photos");
            commandMap.put("app store", "open -a \"App Store\"");
            commandMap.put("notes", "open -a Notes");
            commandMap.put("reminders", "open -a Reminders");
            commandMap.put("voice memos", "open -a \"Voice Memos\"");

            // File System
            commandMap.put("file explorer", "open -a Finder");
            commandMap.put("finder", "open -a Finder");
            commandMap.put("notepad", "open -a TextEdit");
            commandMap.put("textedit", "open -a TextEdit");
            commandMap.put("preview", "open -a Preview");

            // Media
            commandMap.put("music", "open -a Music");
            commandMap.put("tv", "open -a TV");
            commandMap.put("podcasts", "open -a Podcasts");
            commandMap.put("quicktime", "open -a \"QuickTime Player\"");

            addAlias("activity monitor", "task manager", "taskmgr");
            addAlias("camera", "photo booth");
            addAlias("file explorer", "finder");

        } else if (os.contains("nix") || os.contains("nux")) {
            // System Settings & Utilities
            commandMap.put("settings", "gnome-control-center");
            commandMap.put("system monitor", "gnome-system-monitor");
            commandMap.put("task manager", "gnome-system-monitor");
            commandMap.put("disk utility", "gnome-disks");
            commandMap.put("terminal", "gnome-terminal");

            // Built-in Apps
            commandMap.put("camera", "cheese");
            commandMap.put("calendar", "gnome-calendar");
            commandMap.put("calculator", "gnome-calculator");
            commandMap.put("clock", "gnome-clocks");
            commandMap.put("weather", "gnome-weather");
            commandMap.put("maps", "gnome-maps");
            commandMap.put("photos", "gnome-photos");
            commandMap.put("screenshot", "gnome-screenshot");

            // File System
            commandMap.put("file explorer", "nautilus");
            commandMap.put("files", "nautilus");
            commandMap.put("notepad", "gedit");
            commandMap.put("text editor", "gedit");

            addAlias("system monitor", "task manager");
            addAlias("file explorer", "files", "nautilus");
        }
    }

    // Daily-use applications
    private void registerDailyApps() {
        if (os.contains("win")) {
            commandMap.put("chrome", "chrome.exe");
            commandMap.put("edge", "msedge.exe");
            commandMap.put("firefox", "firefox.exe");
            commandMap.put("whatsapp", "WhatsApp.exe");
            commandMap.put("telegram", "Telegram.exe");
            commandMap.put("discord", "Discord.exe");
            commandMap.put("slack", "slack.exe");
            commandMap.put("zoom", "Zoom.exe");
            commandMap.put("teams", "Teams.exe");
            commandMap.put("skype", "Skype.exe");
            commandMap.put("spotify", "Spotify.exe");
            commandMap.put("vlc", "vlc.exe");

            // Microsoft Office
            commandMap.put("word", "WINWORD.EXE");
            commandMap.put("excel", "EXCEL.EXE");
            commandMap.put("powerpoint", "POWERPNT.EXE");
            commandMap.put("outlook", "OUTLOOK.EXE");
            commandMap.put("onenote", "ONENOTE.EXE");

        } else if (os.contains("mac")) {
            commandMap.put("chrome", "open -a \"Google Chrome\"");
            commandMap.put("edge", "open -a \"Microsoft Edge\"");
            commandMap.put("firefox", "open -a Firefox");
            commandMap.put("safari", "open -a Safari");
            commandMap.put("whatsapp", "open -a WhatsApp");
            commandMap.put("telegram", "open -a Telegram");
            commandMap.put("discord", "open -a Discord");
            commandMap.put("slack", "open -a Slack");
            commandMap.put("zoom", "open -a zoom.us");
            commandMap.put("teams", "open -a \"Microsoft Teams\"");
            commandMap.put("skype", "open -a Skype");
            commandMap.put("spotify", "open -a Spotify");
            commandMap.put("vlc", "open -a VLC");

            commandMap.put("word", "open -a \"Microsoft Word\"");
            commandMap.put("excel", "open -a \"Microsoft Excel\"");
            commandMap.put("powerpoint", "open -a \"Microsoft PowerPoint\"");
            commandMap.put("outlook", "open -a \"Microsoft Outlook\"");

        } else if (os.contains("nix") || os.contains("nux")) {
            commandMap.put("chrome", "google-chrome");
            commandMap.put("edge", "microsoft-edge");
            commandMap.put("firefox", "firefox");
            commandMap.put("whatsapp", "whatsapp-nativefier");
            commandMap.put("telegram", "telegram-desktop");
            commandMap.put("discord", "discord");
            commandMap.put("slack", "slack");
            commandMap.put("zoom", "zoom");
            commandMap.put("teams", "teams");
            commandMap.put("spotify", "spotify");
            commandMap.put("vlc", "vlc");

            commandMap.put("word", "libreoffice --writer");
            commandMap.put("excel", "libreoffice --calc");
            commandMap.put("powerpoint", "libreoffice --impress");
        }
    }

    // Development tools
    private void registerDevelopmentTools() {
        if (os.contains("win")) {
            commandMap.put("vscode", "code.exe");
            commandMap.put("visual studio", "devenv.exe");
            commandMap.put("intellij", "idea64.exe");
            commandMap.put("pycharm", "pycharm64.exe");
            commandMap.put("android studio", "studio64.exe");
            commandMap.put("postman", "Postman.exe");
            commandMap.put("git", "git.exe");
            commandMap.put("docker", "docker.exe");
            commandMap.put("mysql workbench", "MySQLWorkbench.exe");
            commandMap.put("pgadmin", "pgAdmin4.exe");

        } else if (os.contains("mac")) {
            commandMap.put("vscode", "open -a \"Visual Studio Code\"");
            commandMap.put("intellij", "open -a \"IntelliJ IDEA\"");
            commandMap.put("pycharm", "open -a PyCharm");
            commandMap.put("android studio", "open -a \"Android Studio\"");
            commandMap.put("postman", "open -a Postman");
            commandMap.put("xcode", "open -a Xcode");

        } else if (os.contains("nix") || os.contains("nux")) {
            commandMap.put("vscode", "code");
            commandMap.put("intellij", "idea");
            commandMap.put("pycharm", "pycharm");
            commandMap.put("android studio", "android-studio");
            commandMap.put("postman", "postman");
        }
    }

    // Media and creative apps
    private void registerMediaApps() {
        if (os.contains("win")) {
            commandMap.put("photoshop", "Photoshop.exe");
            commandMap.put("illustrator", "Illustrator.exe");
            commandMap.put("premiere", "Adobe Premiere Pro.exe");
            commandMap.put("after effects", "AfterFX.exe");
            commandMap.put("gimp", "gimp-2.10.exe");
            commandMap.put("obs", "obs64.exe");
            commandMap.put("audacity", "audacity.exe");

        } else if (os.contains("mac")) {
            commandMap.put("photoshop", "open -a \"Adobe Photoshop\"");
            commandMap.put("illustrator", "open -a \"Adobe Illustrator\"");
            commandMap.put("premiere", "open -a \"Adobe Premiere Pro\"");
            commandMap.put("final cut", "open -a \"Final Cut Pro\"");
            commandMap.put("gimp", "open -a GIMP");
            commandMap.put("obs", "open -a OBS");

        } else if (os.contains("nix") || os.contains("nux")) {
            commandMap.put("gimp", "gimp");
            commandMap.put("inkscape", "inkscape");
            commandMap.put("obs", "obs");
            commandMap.put("audacity", "audacity");
        }
    }

    // Detect installed applications from common directories
    private void detectInstalledApplications() {
        if (os.contains("win")) {
            scanWindowsApplications();
        } else if (os.contains("mac")) {
            scanMacApplications();
        } else if (os.contains("nix") || os.contains("nux")) {
            scanLinuxApplications();
        }
    }

    private void scanWindowsApplications() {
        String[] programDirs = {
                System.getenv("ProgramFiles"),
                System.getenv("ProgramFiles(x86)"),
                System.getenv("LOCALAPPDATA") + "\\Programs"
        };

        for (String dir : programDirs) {
            if (dir == null) continue;
            scanDirectory(new File(dir), 2); // Scan 2 levels deep
        }
    }

    private void scanMacApplications() {
        String[] appDirs = {
                "/Applications",
                System.getProperty("user.home") + "/Applications"
        };

        for (String dir : appDirs) {
            File folder = new File(dir);
            if (!folder.exists()) continue;

            File[] apps = folder.listFiles((f) -> f.getName().endsWith(".app"));
            if (apps == null) continue;

            for (File app : apps) {
                String name = app.getName().replace(".app", "").toLowerCase();
                commandMap.putIfAbsent(name, "open -a \"" + app.getName().replace(".app", "") + "\"");
            }
        }
    }

    private void scanLinuxApplications() {
        String[] desktopDirs = {
                "/usr/share/applications",
                System.getProperty("user.home") + "/.local/share/applications"
        };

        for (String dir : desktopDirs) {
            File folder = new File(dir);
            if (!folder.exists()) continue;

            File[] files = folder.listFiles((f) -> f.getName().endsWith(".desktop"));
            if (files == null) continue;

            for (File f : files) {
                String name = f.getName().replace(".desktop", "").toLowerCase();
                commandMap.putIfAbsent(name, "gtk-launch " + f.getName());
            }
        }
    }

    private void scanDirectory(File dir, int depth) {
        if (depth <= 0 || !dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isFile() && f.getName().endsWith(".exe")) {
                String name = f.getName().replace(".exe", "").toLowerCase();
                commandMap.putIfAbsent(name, f.getAbsolutePath());
            } else if (f.isDirectory() && depth > 1) {
                scanDirectory(f, depth - 1);
            }
        }
    }

    // Scan PATH directories for executable commands
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

    // Helper method to add aliases
    private void addAlias(String primary, String... aliases) {
        List<String> aliasList = new ArrayList<>(Arrays.asList(aliases));
        aliasMap.put(primary, aliasList);

        String command = commandMap.get(primary);
        if (command != null) {
            for (String alias : aliases) {
                commandMap.putIfAbsent(alias.toLowerCase(), command);
            }
        }
    }

    // Public API
    public String getCommand(String name) {
        if (name == null) return null;
        name = name.toLowerCase().trim();
        return commandMap.get(name);
    }

    public boolean isCommandAvailable(String name) {
        return getCommand(name) != null;
    }

    public Map<String, String> getAllCommands() {
        return new HashMap<>(commandMap);
    }

    public List<String> searchCommands(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(commandMap.keySet());
        }

        String searchTerm = query.toLowerCase().trim();
        return commandMap.keySet().stream()
                .filter(key -> key.contains(searchTerm))
                .sorted()
                .collect(Collectors.toList());
    }

    public Map<String, String> getCommandsByCategory(String category) {
        return commandMap.entrySet().stream()
                .filter(e -> matchesCategory(e.getKey(), category))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean matchesCategory(String command, String category) {
        category = category.toLowerCase();
        switch (category) {
            case "system":
                return command.contains("settings") || command.contains("control") ||
                        command.contains("task") || command.contains("manager");
            case "browser":
                return command.contains("chrome") || command.contains("edge") ||
                        command.contains("firefox") || command.contains("safari");
            case "office":
                return command.contains("word") || command.contains("excel") ||
                        command.contains("powerpoint") || command.contains("outlook");
            case "development":
                return command.contains("vscode") || command.contains("intellij") ||
                        command.contains("studio") || command.contains("postman");
            case "media":
                return command.contains("spotify") || command.contains("vlc") ||
                        command.contains("music") || command.contains("player");
            default:
                return false;
        }
    }

    public String getOperatingSystem() {
        return os;
    }

    public int getCommandCount() {
        return commandMap.size();
    }

    public void printAllCommands() {
        System.out.println("\n=== Available Commands ===");
        commandMap.keySet().stream()
                .sorted()
                .forEach(key -> System.out.println(key + " -> " + commandMap.get(key)));
    }
}