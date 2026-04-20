package com.example;

import com.google.gson.JsonParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class FileServiceTest {

    private final FileService fileService = new FileService();
    private Path tempDir;

    /**
     * Создаёт временную директорию перед каждым тестом.
     * Все тесты используют изолированное файловое пространство.
     *
     * @throws IOException если не удалось создать временную директорию
     */
    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("FileServiceTest");
    }

    /**
     * Удаляет временную директорию и все её содержимое после каждого теста.
     *
     * @throws IOException если не удалось удалить файлы или директорию
     */
    @After
    public void tearDown() throws IOException {
        // Удаляем временные файлы и директорию
        if (tempDir != null && Files.exists(tempDir)) {
            Files.list(tempDir).forEach(file -> {
                try { Files.delete(file); } catch (IOException ignored) {}
            });
            Files.delete(tempDir);
        }
    }

    /**
     * Проверяет, что метод readExpressionFromJson корректно извлекает выражение
     * из правильно сформированного JSON-файла.
     *
     * @throws IOException если возникла ошибка ввода-вывода (не ожидается в тесте)
     */
    @Test
    public void readExpressionFromJson_correctFile_returnsExpression() throws IOException {
        Path inputFile = tempDir.resolve("input.json");
        String jsonContent = "{\"expression\": \"3 4 +\"}";
        Files.write(inputFile, jsonContent.getBytes());

        String expression = fileService.readExpressionFromJson(inputFile.toString());
        assertEquals("3 4 +", expression);
    }

    /**
     * Проверяет, что при отсутствии поля "expression" в JSON-файле
     * выбрасывается JsonParseException с соответствующим сообщением.
     *
     * @throws IOException если возникла ошибка ввода-вывода (не ожидается в тесте)
     */
    @Test
    public void readExpressionFromJson_missingExpressionField_throwsException() throws IOException {
        Path inputFile = tempDir.resolve("missing.json");
        String jsonContent = "{\"other\": \"value\"}";
        Files.write(inputFile, jsonContent.getBytes());

        try {
            fileService.readExpressionFromJson(inputFile.toString());
            fail("Expected JsonParseException");
        } catch (JsonParseException ex) {
            assertThat(ex.getMessage(), containsString("Отсутствует поле 'expression'"));
        }
    }

    /**
     * Проверяет, что при попытке прочитать некорректный JSON (например, незакрытую фигурную скобку)
     * выбрасывается JsonParseException.
     *
     * @throws IOException если возникла ошибка ввода-вывода (не ожидается в тесте)
     */
    @Test
    public void readExpressionFromJson_malformedJson_throwsException() throws IOException {
        Path inputFile = tempDir.resolve("bad.json");
        String jsonContent = "{";
        Files.write(inputFile, jsonContent.getBytes());

        try {
            fileService.readExpressionFromJson(inputFile.toString());
            fail("Expected JsonParseException");
        } catch (JsonParseException e) {
            // Ожидаемое исключение
        }
    }

    /**
     * Проверяет, что при указании несуществующего файла выбрасывается IOException.
     */
    @Test
    public void readExpressionFromJson_fileNotFound_throwsIOException() {
        String nonExistentFile = tempDir.resolve("no.json").toString();
        try {
            fileService.readExpressionFromJson(nonExistentFile);
            fail("Expected IOException");
        } catch (IOException e) {
            // Ожидаемое исключение
        }
    }

    /**
     * Проверяет, что при записи целочисленного результата (например, 7.0)
     * в выходной JSON-файл значение сохраняется без десятичной точки (как целое число).
     *
     * @throws IOException если возникла ошибка ввода-вывода
     */
    @Test
    public void writeResultToJson_integerResult_writesInteger() throws IOException {
        Path outputFile = tempDir.resolve("output.json");
        String expression = "3 4 +";
        double result = 7.0;

        fileService.writeResultToJson(outputFile.toString(), expression, result);

        String content = new String(Files.readAllBytes(outputFile));
        assertThat(content, containsString("\"result\":7"));
        assertThat(content, containsString("\"expression\":\"3 4 +\""));
    }

    /**
     * Проверяет, что при записи дробного результата (например, 1.5)
     * в выходной JSON-файл значение сохраняется с десятичной точкой.
     * Тест пропускается на Windows из-за возможных различий в формате десятичного разделителя.
     *
     * @throws IOException если возникла ошибка ввода-вывода
     */
    @Test
    public void writeResultToJson_doubleResult_writesDouble() throws IOException {
        // Пропускаем на Windows, если платформа Windows – в JUnit 4 используем Assume
        org.junit.Assume.assumeFalse(System.getProperty("os.name").toLowerCase().contains("win"));

        Path outputFile = tempDir.resolve("output.json");
        String expression = "3 2 /";
        double result = 1.5;

        fileService.writeResultToJson(outputFile.toString(), expression, result);

        String content = new String(Files.readAllBytes(outputFile));
        assertThat(content, containsString("\"result\":1.5"));
        assertThat(content, containsString("\"expression\":\"3 2 /\""));
    }
}