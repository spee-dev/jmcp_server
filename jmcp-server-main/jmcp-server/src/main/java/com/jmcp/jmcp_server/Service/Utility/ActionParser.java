package com.jmcp.jmcp_server.Service.Utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ActionParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public String parseGeminiResponse(JsonNode root) throws IOException {
        JsonNode candidates = root.path("candidates");
        if (candidates.isMissingNode() || candidates.size() == 0) {
            return "unknown";
        }

        String content = candidates.get(0).path("content").path("parts").get(0).path("text").asText().trim();
        content = content.replaceAll("```json|```", "").trim();

        JsonNode actionNode = mapper.readTree(content);
        String action = actionNode.path("action").asText("unknown").toLowerCase();

        // Normalize aliases
        if (action.equals("browser")) {
            action = "chrome";
        }

        if (action.equals("open_website")) {
            String url = actionNode.path("url").asText("");
            if (!url.isEmpty()) {
                return "open_website:" + url;
            }
        }

        if (action.equals("search")) {
            String engine = actionNode.path("engine").asText("google");
            String query = actionNode.path("query").asText("");
            if (!query.isEmpty()) {
                return "search:" + engine + ":" + query;
            }
        }

        if (action.startsWith("create_") || action.startsWith("search_") || action.startsWith("list_") || action.startsWith("delete_") || action.startsWith("open_")) {
            String path = actionNode.path("path").asText("");
            if (!path.isEmpty()) {
                return action + ":" + path;
            }
        }

        return action;
    }
}
