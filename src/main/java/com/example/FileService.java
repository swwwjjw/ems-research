package com.example;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис для работы с JSON-файлами.
 * Обеспечивает чтение выражения из JSON-файла и запись результата вычислений в JSON-файл.
 */
public class FileService {
    
    /**
     * Объект Gson для преобразования между JSON и Java-объектами.
     * Используется стандартная конфигурация без дополнительных настроек.
     */
    private final Gson gson = new Gson();

    /**
     * Читает выражение в обратной польской нотации из JSON-файла.
     * Ожидается, что файл содержит JSON-объект с полем "expression".
     *
     * @param filePath путь к входному JSON-файлу
     * @return строка с выражением (токены через пробел)
     * @throws IOException если возникла ошибка при чтении файла
     * @throws JsonParseException если содержимое файла не является корректным JSON
     *         или в нём отсутствует поле "expression"
     */
    public String readExpressionFromJson(String filePath) throws IOException, JsonParseException {
        Path path = Paths.get(filePath);
        String content = new String(Files.readAllBytes(path));
        JsonObject json = gson.fromJson(content, JsonObject.class);
        if (!json.has("expression")) {
            throw new JsonParseException("Отсутствует поле 'expression' в JSON");
        }
        return json.get("expression").getAsString();
    }

    /**
     * Записывает исходное выражение и результат его вычисления в JSON-файл.
     * Формат выходного файла:
     * <pre>
     * {
     *   "expression": "исходное выражение",
     *   "result": значение
     * }
     * </pre>
     * Если результат является целым числом (например, 14.0), он сохраняется как целое (14),
     * иначе сохраняется как число с плавающей точкой.
     *
     * @param outputPath путь к выходному JSON-файлу (будет создан или перезаписан)
     * @param expression исходное выражение в обратной польской нотации
     * @param result вычисленный результат
     * @throws IOException если возникла ошибка при записи файла
     */
    public void writeResultToJson(String outputPath, String expression, double result) throws IOException {
        JsonObject outputJson = new JsonObject();
        outputJson.addProperty("expression", expression);
        // Форматируем результат как целое, если это целое число
        if (result == (long) result) {
            outputJson.addProperty("result", (long) result);
        } else {
            outputJson.addProperty("result", result);
        }
        Files.write(Paths.get(outputPath), gson.toJson(outputJson).getBytes());
    }
}