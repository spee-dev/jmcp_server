package com.jmcp.jmcp_server.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GeminiAIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Autowired
    private CommandRegistry commandRegistry;

    private final ObjectMapper mapper = new ObjectMapper();

    public String extractAction(String userInput) {
        try {
            // ---- 1. Preprocess input: remove extra words / Hinglish verbs ----
            String cleanInput = userInput.toLowerCase()
                    .replace("karo", "")
                    .replace("kholo", "")
                    .replace("chalao", "")
                    .replace("please", "")
                    .trim();

            // ---- 2. Provide available commands as context ----
            Map<String, String> availableCommands = commandRegistry.getAllCommands();
            StringBuilder commandList = new StringBuilder();
            for (String cmd : availableCommands.keySet()) {
                commandList.append(cmd).append(", ");
                if (cmd.equals("chrome")) commandList.append("browser, chrome, google chrome, ");
            }

            // ---- 3. Build prompt ----
            String prompt = String.format("""
You are an extremely smart command parser for a computer system. User input can be in any natural language (English, Hinglish, informal, or uneducated speech), may contain:
- Typos
- Missing spaces (like 'taskmanager' instead of 'task manager')
- Mixed languages or slang

Your goal:
1. Understand exactly what the user wants to do.
2. Map it to one of the allowed commands: %s
3. Output ONLY strict JSON in this format: {"action":"commandname"}
4. If the command is unknown, output: {"action":"unknown"}

Examples:
- "chrome kholo" → {"action":"chrome"}
- "opennotepad" → {"action":"notepad"}
- "start taskmanager" → {"action":"task manager"}
- "kuch bhi" → {"action":"unknown"}

User input: "%s"
""", commandList.toString(), cleanInput);



            String escapedPrompt = mapper.writeValueAsString(prompt);

            String jsonBody = String.format("""
            {
              "contents": [{"role":"user","parts":[{"text": %s}]}],
              "generationConfig": {"temperature":0,"maxOutputTokens":100}
            }
            """, escapedPrompt);

            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey);
            post.setHeader("Content-Type", "application/json");
            post.setEntity(new StringEntity(jsonBody));

            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(post);
            String responseString = EntityUtils.toString(response.getEntity());

            System.out.println("DEBUG Gemini Response: " + responseString);

            // ---- 4. Parse Gemini output safely ----
            JsonNode root = mapper.readTree(responseString);
            JsonNode candidates = root.path("candidates");
            if (candidates.isMissingNode() || candidates.size() == 0) return "unknown";

            String content = candidates.get(0).path("content").path("parts").get(0).path("text").asText().trim();
            content = content.replaceAll("```json|```", "").trim();

            JsonNode actionNode = mapper.readTree(content);
            String action = actionNode.path("action").asText("unknown").toLowerCase();

            // ---- 5. Normalize aliases ----
            if (action.equals("browser")) action = "chrome";

            return action;

        } catch (Exception e) {
            System.err.println("GeminiAIService failed: " + e.getMessage());
            e.printStackTrace();
            return "unknown";
        }
    }
}
