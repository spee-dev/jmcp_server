package com.jmcp.jmcp_server.Service.Utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import org.apache.hc.core5.http.ParseException;

@Component
public class GeminiApiClient {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode callGeminiApi(String prompt) throws IOException, ParseException {
        String escapedPrompt = mapper.writeValueAsString(prompt);

        String jsonBody = String.format("""
            {
              "contents": [{"role":"user","parts":[{"text": %s}]}],
              "generationConfig": {"temperature":0,"maxOutputTokens":100}
            }
            """, escapedPrompt);

        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + geminiApiKey);
        post.setHeader("Content-Type", "application/json");
        post.setEntity(new StringEntity(jsonBody));

        ClassicHttpResponse response = (ClassicHttpResponse) client.execute(post);
        String responseString = EntityUtils.toString(response.getEntity());

        System.out.println("DEBUG Gemini Response: " + responseString);

        return mapper.readTree(responseString);
    }
}
