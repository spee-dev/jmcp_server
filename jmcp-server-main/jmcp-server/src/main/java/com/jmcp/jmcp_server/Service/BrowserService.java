package com.jmcp.jmcp_server.Service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BrowserService {

    /**
     * Opens a website in the default browser
     * @param url The URL or website name
     */
    public void openWebsite(String url) {
        if (url == null || url.isEmpty()) {
            System.out.println("No URL provided!");
            return;
        }

        // If user types "youtube", convert to full URL
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://www." + url + ".com";
        }

        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd", "/c", "start", url);
            } else if (os.contains("mac")) {
                pb = new ProcessBuilder("open", url);
            } else if (os.contains("nix") || os.contains("nux")) {
                pb = new ProcessBuilder("xdg-open", url);
            } else {
                System.out.println("Unsupported operating system. Cannot open browser.");
                return;
            }

            pb.start();
            System.out.println("Opening website: " + url);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to open website: " + url + " - " + e.getMessage());
        }
    }

    public void performSearch(String engine, String query) {
        String searchUrl = "";
        switch (engine.toLowerCase()) {
            case "youtube":
                searchUrl = "https://www.youtube.com/results?search_query=" + query.replace(" ", "+");
                break;
            case "leetcode":
                searchUrl = "https://leetcode.com/problemset/all/?search=" + query.replace(" ", "+");
                break;
            case "google":
            default:
                searchUrl = "https://www.google.com/search?q=" + query.replace(" ", "+");
                break;
        }
        openWebsite(searchUrl);
    }
}
