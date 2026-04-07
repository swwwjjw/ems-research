package com.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsoleTest {

    @Mock
    private FileService mockFileService;

    @Mock
    private Scanner mockScanner;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void manualInput_validInput_slowAlgorithm_printsResult() {
        RPNCalculator realCalculator = new RPNCalculator();
        Console app = new Console(mockFileService, realCalculator);

        when(mockScanner.nextLine()).thenReturn("3 4 +", "1");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Результат: 7"));
        assertFalse(output.contains("Ошибка"));
    }

    @Test
    public void manualInput_validInput_fastAlgorithm_printsResult() {
        RPNCalculator realCalculator = new RPNCalculator();
        Console app = new Console(mockFileService, realCalculator);

        when(mockScanner.nextLine()).thenReturn("5 2 -", "2");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Результат: 3"));
    }

    @Test
    public void manualInput_invalidExpression_printsError() {
        RPNCalculator realCalculator = new RPNCalculator();
        Console app = new Console(mockFileService, realCalculator);

        when(mockScanner.nextLine()).thenReturn("3 +", "1");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Ошибка вычисления:"));
    }

    @Test
    public void manualInput_emptyExpression_printsValidationMessage() {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("   ");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Выражение не может быть пустым."));
        verify(mockScanner, times(1)).nextLine();
    }

    @Test
    public void manualInput_invalidAlgorithm_printsValidationMessage() {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("3 4 +", "9");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Неверный выбор алгоритма."));
    }

    @Test
    public void processJsonFile_invalidAlgorithm_doesNotReadOrWriteFiles() throws Exception {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("input.json", "output.json", "0");

        app.processJsonFile(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Неверный выбор алгоритма."));
    }

    @Test
    public void processJsonFile_readFails_printsError() throws Exception {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("missing.json", "out.json", "1");
        when(mockFileService.readExpressionFromJson("missing.json")).thenThrow(new IOException("file not found"));

        app.processJsonFile(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Ошибка: file not found"));
    }

    @Test
    public void run_exitOption_terminates() {
        String input = "4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Console app = new Console(mockFileService, new RPNCalculator());
        app.run();

        String output = outputStream.toString();
        assertThat(output, containsString("Выход из программы."));
    }

    @Test
    public void run_invalidChoiceThenExit_printsRetryMessageAndTerminates() {
        String input = "9\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Console app = new Console(mockFileService, new RPNCalculator());
        app.run();

        String output = outputStream.toString();
        assertThat(output, containsString("Неверный выбор. Попробуйте снова."));
        assertThat(output, containsString("Выход из программы."));
    }

    @Test
    public void run_helpThenExit_printsHelpAndReturnsToMenu() {
        String input = "3\n\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Console app = new Console(mockFileService, new RPNCalculator());
        app.run();

        String output = outputStream.toString();
        assertTrue(output.contains("=================== СПРАВКА ==================="));
        assertTrue(output.contains("Для возврата в меню нажмите Enter..."));
        assertTrue(output.contains("Выход из программы."));
    }
}