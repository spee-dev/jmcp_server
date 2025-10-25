package com.jmcp.jmcp_server;

import com.jmcp.jmcp_server.Service.NlpParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class NlpParserTest {

    private final NlpParser parser = new NlpParser();

    @Test
    public void testParseSingleCommand() {
        List<NlpParser.ParsedCommand> commands = parser.parse("Open Chrome");
        assertEquals(1, commands.size());
        assertEquals("chrome", commands.get(0).getName());
    }

    @Test
    public void testParseCommandWithUrl() {
        List<NlpParser.ParsedCommand> commands = parser.parse("Open Chrome and go to https://google.com");
        assertEquals(2, commands.size());
        assertEquals("chrome", commands.get(0).getName());
        assertEquals("chrome", commands.get(1).getName());
        assertArrayEquals(new String[]{"https://google.com"}, commands.get(1).getArgs());
    }

    @Test
    public void testParseSequentialCommands() {
        List<NlpParser.ParsedCommand> commands = parser.parse("Start Docker and Word");
        assertEquals(2, commands.size());
        assertEquals("docker", commands.get(0).getName());
        assertEquals("winword", commands.get(1).getName());
    }

    @Test
    public void testParseWordCommand() {
        List<NlpParser.ParsedCommand> commands = parser.parse("Word");
        assertEquals(1, commands.size());
        assertEquals("winword", commands.get(0).getName());
    }
}
