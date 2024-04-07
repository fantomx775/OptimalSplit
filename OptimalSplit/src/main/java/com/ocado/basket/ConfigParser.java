package com.ocado.basket;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.List;

public class ConfigParser {
    public Map<String, List<String>> parse(String absolutePathToConfigFile) throws IOException {
        Gson gson = new Gson();

        FileReader reader = new FileReader(absolutePathToConfigFile);
        StringBuilder stringBuilder = new StringBuilder();
        int character;
        while ((character = reader.read()) != -1) {
            stringBuilder.append((char) character);
        }
        reader.close();

        TypeToken<Map<String, List<String>>> typeToken = new TypeToken<Map<String, List<String>>>() {};
        Map<String, List<String>> configMap = gson.fromJson(stringBuilder.toString(), typeToken.getType());

        return configMap;
    }
}
