package com.jmcp.jmcp_server.Service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@ConfigurationProperties(prefix = "nlp")
public class NlpParser {

    private Map<String, String> aliases = new HashMap<>();

    public Map<String, String> getAliases() {
        return aliases;
    }

    public void setAliases(Map<String, String> aliases) {
        this.aliases = aliases;
    }

    private static final Pattern COMMAND_PATTERN = Pattern.compile("(go to|\\w+)(?:\\s+(.*))?");

    public static class ParsedCommand {
        private final String name;
        private final String[] args;

        public ParsedCommand(String name, String[] args) {
            this.name = name;
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public String[] getArgs() {
            return args;
        }
    }

    public List<ParsedCommand> parse(String input) {
        List<ParsedCommand> commands = new ArrayList<>();
        String[] parts = input.split("\\s+and\\s+");
        for (String part : parts) {
            Matcher matcher = COMMAND_PATTERN.matcher(part.trim());
            if (matcher.find()) {
                String command = matcher.group(1).toLowerCase();
                if (aliases.containsKey(command)) {
                    command = aliases.get(command);
                }
                String argsString = matcher.group(2);
                String[] args = argsString != null ? argsString.split("\\s+") : new String[0];
                commands.add(new ParsedCommand(command, args));
            }
        }
        return commands;
    }
}
