package com.jmcp.jmcp_server.Service.Utility;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PromptBuilder {

    public String buildGeminiPrompt(Map<String, String> availableCommands, String cleanInput) {
        StringBuilder commandList = new StringBuilder();
        for (String cmd : availableCommands.keySet()) {
            commandList.append(cmd).append(", ");
            if (cmd.equals("chrome")) commandList.append("browser, chrome, google chrome, ");
        }

        return String.format("""
You are an extremely smart command parser for a computer system. User input can be in any natural language (English, Hinglish, informal, or uneducated speech), may contain:
- Typos
- Missing spaces
- Mixed languages or slang

Your goal is to understand the user's intent and map it to a specific JSON format.

Here are the possible actions:
### Examples of informal / Hinglish / typo commands:
- "yr sun bhai chrome khol na" → {"action":"chrome"}
- "calculator pen kr do" → {"action":"calculator"}
- "please camera open kr do" → {"action":"camera"}
- "sun na thoda camera khol dega kya" → {"action":"camera"}
- "opn youtube" → {"action":"open_website", "url":"https://youtube.com"}

1.  **Execute a command**: If the user wants to open a system tool.
    -   **Format**: `{"action":"command_name"}`
    -   **Allowed Commands**: %s
    -   **Examples**:
        -   "chrome kholo" → `{"action":"chrome"}`
        -   "opennotepad" → `{"action":"notepad"}`
        -   "start taskmanager" → `{"action":"task manager"}`
        -   "camera kholo" → `{"action":"camera"}`
        -   "sun na thoda camera khol dega kya" → `{"action":"camera"}`
        -   "sun na thoda camera khol dega kya" → `{"action":"camera"}`

2.  **Open a website**: If the user provides a direct URL.
    -   **Format**: `{"action":"open_website", "url":"[website_url]"}`
    -   **Examples**:
        -   "open google.com" → `{"action":"open_website", "url":"https://google.com"}`
        -   "linkedin chalao" -> `{"action":"open_website", "url":"https://linkedin.com"}`

3.  **Perform a search**: If the user is asking a question, looking for something, or wants to find something on a specific platform.
    -   **Format**: `{"action":"search", "engine":"[engine_name]", "query":"[search_query]"}`
    -   **Supported Engines**: `google`, `youtube`, `leetcode`.
    -   **Examples**:
        -   "who is Sundar Pichai" → `{"action":"search", "engine":"google", "query":"who is Sundar Pichai"}`
        -   "java tutorial on youtube" → `{"action":"search", "engine":"youtube", "query":"java tutorial"}`
        -   "two sum problem on leetcode" → `{"action":"search", "engine":"leetcode", "query":"two sum problem"}`
        -   "dp ka best tutorial youtube pe open karo" → `{"action":"search", "engine":"youtube", "query":"dp best tutorial"}`

4.  **Default to Google Search**: If the user's intent is unclear or doesn't match any of the above, perform a Google search with the user's input as the query.
    -   **Format**: `{"action":"search", "engine":"google", "query":"[user_input]"}`
    -   **Examples**:
        -   "kuch bhi" → `{"action":"search", "engine":"google", "query":"kuch bhi"}`
        -   "blabla" → `{"action":"search", "engine":"google", "query":"blabla"}`

5.  **File System Operations**: If the user wants to perform file operations.
    -   **Format**: `{"action":"[operation_name]", "path":"[file_or_folder_path]"}`
    -   **Supported Operations**: `create_file`, `create_folder`, `search_file`, `list_files`, `delete_file`, `open_file`.
    -   **Path Parsing Rules**:
        -   **Nested Folders**: Build the full path from all location keywords. "in <folder1> in <folder2>" becomes `folder1/folder2`.
        -   **Filename and Extension**: Separate the filename from the location. If the user provides an extension (e.g., "ankit.ppt"), use it. If no extension is provided, default to `.txt`.
        -   **Drive Letters**: Recognize `C:`, `D:`, etc., and map them to the correct Windows drive.
        -   **Spaces in Names**: Handle spaces in folder and file names correctly.
        -   **Default Location**: If no location is specified, use the current directory.
    -   **Examples**:
        -   "create file report" → `{"action":"create_file", "path":"report.txt"}`
        -   "create file ankit.ppt in downloads" → `{"action":"create_file", "path":"C:/Users/HP/Downloads/ankit.ppt"}`
        -   "create file ankit.java in my documents" → `{"action":"create_file", "path":"C:/Users/HP/Documents/ankit.java"}`
        -   "create folder projects on desktop" → `{"action":"create_folder", "path":"C:/Users/HP/Desktop/projects"}`
        -   "create file ankit.py in ankit folder in downloads" → `{"action":"create_file", "path":"C:/Users/HP/Downloads/ankit folder/ankit.py"}`
        -   "search file report.txt" → `{"action":"search_file", "path":"report.txt"}`
        -   "list files in downloads" → `{"action":"list_files", "path":"C:/Users/HP/Downloads"}`
        -   "delete file old.txt" → `{"action":"delete_file", "path":"old.txt"}`
        -   "open file report.txt" → `{"action":"open_file", "path":"report.txt"}`

Output ONLY the strict JSON response.

User input: "%s"
""", commandList.toString(), cleanInput);
    }

    public String buildUrlFinderPrompt(String query) {
        return String.format("""
You are an intelligent assistant that finds the best URL for a given query. Your goal is to provide the most relevant and high-quality link from well-known sources like YouTube, GeeksforGeeks, official documentation, or popular blogs.

1.  Analyze the user's query: "%s"
2.  Find the single best URL that directly addresses the query.
3.  Output ONLY the URL and nothing else.

Example:
- Query: "best dynamic programming tutorial"
- Output: https://www.geeksforgeeks.org/dynamic-programming/

Query: "%s"
""", query, query);
    }
}
