package com.jmcp.jmcp_server.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.jmcp.jmcp_server.Service.Utility.ActionParser;
import com.jmcp.jmcp_server.Service.Utility.GeminiApiClient;
import com.jmcp.jmcp_server.Service.Utility.InputProcessor;
import com.jmcp.jmcp_server.Service.Utility.PromptBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeminiAIService {

    @Autowired
    private CommandRegistry commandRegistry;
    @Autowired
    private InputProcessor inputProcessor;
    @Autowired
    private PromptBuilder promptBuilder;
    @Autowired
    private GeminiApiClient geminiApiClient;
    @Autowired
    private ActionParser actionParser;

    public String extractAction(String userInput) {
        try {
            // ---- 1. Preprocess input: remove extra words / Hinglish verbs ----
            String cleanInput = inputProcessor.cleanUserInput(userInput);

            // ---- 2. Check for direct command match ----
            if (commandRegistry.isCommandAvailable(cleanInput)) {
                // If a direct command is found, return it as a JSON action.
                return String.format("{\"action\":\"%s\"}", cleanInput);
            }

            // ---- 3. Provide available commands as context ----
            Map<String, String> availableCommands = commandRegistry.getAllCommands();

            // ---- 3. Build prompt ----
            String prompt = promptBuilder.buildGeminiPrompt(availableCommands, cleanInput);

            // ---- 4. Call Gemini API ----
            JsonNode geminiResponse = geminiApiClient.callGeminiApi(prompt);

            // ---- 5. Parse Gemini output safely and normalize aliases ----
            return actionParser.parseGeminiResponse(geminiResponse);

        } catch (Exception e) {
            System.err.println("GeminiAIService failed: " + e.getMessage());
            e.printStackTrace();
            return "unknown";
        }
    }

    public String findBestUrl(String query) {
        try {
            // ---- 1. Build prompt for finding URL ----
            String prompt = promptBuilder.buildUrlFinderPrompt(query);

            // ---- 2. Call Gemini API ----
            JsonNode geminiResponse = geminiApiClient.callGeminiApi(prompt);

            // ---- 3. Parse Gemini output to get the URL ----
            JsonNode candidates = geminiResponse.path("candidates");
            if (candidates.isMissingNode() || candidates.size() == 0) {
                return null;
            }
            return candidates.get(0).path("content").path("parts").get(0).path("text").asText().trim();

        } catch (Exception e) {
            System.err.println("GeminiAIService failed to find URL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
