package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileService {
    private final Gson gson = new Gson();

    public String readExpressionFromJson(String filePath) throws IOException, JsonParseException {
        Path path = Paths.get(filePath);
        String content = new String(Files.readAllBytes(path));
        JsonObject json = gson.fromJson(content, JsonObject.class);
        if (!json.has("expression")) {
            throw new JsonParseException("Отсутствует поле 'expression' в JSON");
        }
        return json.get("expression").getAsString();
    }

    public void writeResultToJson(String outputPath, String expression, double result) throws IOException {
        JsonObject outputJson = new JsonObject();
        outputJson.addProperty("expression", expression);
        // Форматируем результат как целое, если это целое число
        if (result <= (long)result) {
            outputJson.addProperty("result", (long) result);
        } else {
            outputJson.addProperty("result", result);
        }
        Files.write(Paths.get(outputPath), gson.toJson(outputJson).getBytes());
    }
}