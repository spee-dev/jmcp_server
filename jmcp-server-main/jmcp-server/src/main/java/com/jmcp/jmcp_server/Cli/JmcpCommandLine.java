package com.jmcp.jmcp_server.Cli;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class JmcpCommandLine implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        try {
            launchCliInNewWindow();
        } catch (IOException e) {
            System.err.println("Failed to launch CLI in a new window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void launchCliInNewWindow() throws IOException {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = CliClient.class.getCanonicalName();

        String os = System.getProperty("os.name").toLowerCase();

        ProcessBuilder pb;
        if (os.contains("win")) {
            // For Windows, use "cmd /c start" to open a new window
            pb = new ProcessBuilder("cmd", "/c", "start", "JMCP CLI", javaBin, "-cp", classpath, className);
        } else if (os.contains("mac")) {
            // For macOS, we can use AppleScript to open a new Terminal window
            String script = "tell application \"Terminal\" to do script \"" +
                            javaBin + " -cp " + classpath + " " + className + "\"";
            pb = new ProcessBuilder("osascript", "-e", script);
        } else if (os.contains("nix") || os.contains("nux")) {
            // For Linux, try to use a common terminal emulator like x-terminal-emulator or gnome-terminal
            // This might need to be adjusted depending on the user's setup
            String terminalCmd = "x-terminal-emulator"; // or "gnome-terminal", "konsole", etc.
            pb = new ProcessBuilder(terminalCmd, "-e", "java -cp " + classpath + " " + className);
        } else {
            System.err.println("Unsupported operating system for launching new CLI window: " + os);
            return;
        }

        System.out.println("Launching CLI in a new window...");
        pb.start();
    }
}
