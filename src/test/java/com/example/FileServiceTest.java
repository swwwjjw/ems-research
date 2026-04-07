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

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("FileServiceTest");
    }

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

    @Test
    public void readExpressionFromJson_correctFile_returnsExpression() throws IOException {
        Path inputFile = tempDir.resolve("input.json");
        String jsonContent = "{\"expression\": \"3 4 +\"}";
        Files.write(inputFile, jsonContent.getBytes());

        String expression = fileService.readExpressionFromJson(inputFile.toString());
        assertEquals("3 4 +", expression);
    }

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