package com.jmcp.jmcp_server.Service.Utility;

import org.springframework.stereotype.Component;

@Component
public class InputProcessor {

    public String cleanUserInput(String userInput) {
        return userInput.toLowerCase()
                .replace("karo", "")
                .replace("kholo", "")
                .replace("chalao", "")
                .replace("please", "")
                .replaceAll("[^a-z0-9 .'-]", "") // Keep letters, numbers, spaces, dots, hyphens, and apostrophes
                .trim();
    }
}
