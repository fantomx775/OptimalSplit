package com.ocado;

import com.ocado.basket.ConfigParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigParserTest {

    @Test
    void testParse() {
        ConfigParser configParser = new ConfigParser();

        // Provide absolute path to the test for \json\test-parse-config.json file
        String pathToConfigFile = "C:\\Staż\\Ocado\\Java\\Zadanie\\Zadanie\\OptSplt\\OptimalSplit\\src\\test\\java\\com\\ocado\\json\\test-parse-config.json";

        Map<String, List<String>> configMap = null;
        try {
            configMap = configParser.parse(pathToConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, List<String>> expectedConfigMap = new HashMap<>();
        expectedConfigMap.put("Item1", Arrays.asList("Courier1", "Courier2"));
        expectedConfigMap.put("Item2", Arrays.asList("Courier2", "Courier3"));
        expectedConfigMap.put("Item3", Arrays.asList("Courier1", "Courier3"));

        assertEquals(expectedConfigMap, configMap);
    }

    @Test
    void testParseWithInvalidFilePath() {
        ConfigParser configParser = new ConfigParser();
        String invalidPathToConfigFile = "path/to/invalid/config/file";
        assertThrows(IOException.class, () -> configParser.parse(invalidPathToConfigFile));
    }
}