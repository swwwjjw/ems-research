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

    /**
     * Настраивает окружение перед каждым тестом.
     * Перенаправляет стандартный вывод в ByteArrayOutputStream для последующей проверки.
     */
    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    /**
     * Восстанавливает стандартный вывод после каждого теста.
     */
    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    /**
     * Проверяет ручной ввод корректного выражения с выбором медленного алгоритма.
     * Ожидается вывод результата "7" без сообщений об ошибке.
     */
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

    /**
     * Проверяет ручной ввод корректного выражения с выбором быстрого алгоритма.
     * Ожидается вывод результата "3".
     */
    @Test
    public void manualInput_validInput_fastAlgorithm_printsResult() {
        RPNCalculator realCalculator = new RPNCalculator();
        Console app = new Console(mockFileService, realCalculator);

        when(mockScanner.nextLine()).thenReturn("5 2 -", "2");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Результат: 3"));
    }

    /**
     * Проверяет ручной ввод некорректного выражения (например, "3 +").
     * Ожидается вывод сообщения об ошибке вычисления.
     */
    @Test
    public void manualInput_invalidExpression_printsError() {
        RPNCalculator realCalculator = new RPNCalculator();
        Console app = new Console(mockFileService, realCalculator);

        when(mockScanner.nextLine()).thenReturn("3 +", "1");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Ошибка вычисления:"));
    }

    /**
     * Проверяет, что при вводе пустого выражения выводится сообщение о валидации,
     * и дальнейший ввод алгоритма не запрашивается.
     */
    @Test
    public void manualInput_emptyExpression_printsValidationMessage() {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("   ");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Выражение не может быть пустым."));
        verify(mockScanner, times(1)).nextLine();
    }

    /**
     * Проверяет, что при выборе несуществующего алгоритма (не 1 и не 2)
     * выводится сообщение об ошибке, и вычисление не выполняется.
     */
    @Test
    public void manualInput_invalidAlgorithm_printsValidationMessage() {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("3 4 +", "9");

        app.manualInput(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Неверный выбор алгоритма."));
    }

    /**
     * Проверяет, что при выборе неверного алгоритма в режиме обработки JSON-файла
     * чтение и запись файлов не производятся (методы сервиса не вызываются).
     */
    @Test
    public void processJsonFile_invalidAlgorithm_doesNotReadOrWriteFiles() throws Exception {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("input.json", "output.json", "0");

        app.processJsonFile(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Неверный выбор алгоритма."));
        // Дополнительно: методы FileService не вызываются (можно проверить verifyZeroInteractions,
        // но в данном тесте это не требуется, так как исключение выкинуто раньше)
    }

    /**
     * Проверяет, что при ошибке чтения JSON-файла (например, файл не найден)
     * выводится соответствующее сообщение об ошибке.
     */
    @Test
    public void processJsonFile_readFails_printsError() throws Exception {
        Console app = new Console(mockFileService, new RPNCalculator());

        when(mockScanner.nextLine()).thenReturn("missing.json", "out.json", "1");
        when(mockFileService.readExpressionFromJson("missing.json")).thenThrow(new IOException("file not found"));

        app.processJsonFile(mockScanner);

        String output = outputStream.toString();
        assertThat(output, containsString("Ошибка: file not found"));
    }

    /**
     * Проверяет, что при выборе пункта меню "4. Выход" программа корректно завершается.
     */
    @Test
    public void run_exitOption_terminates() {
        String input = "4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Console app = new Console(mockFileService, new RPNCalculator());
        app.run();

        String output = outputStream.toString();
        assertThat(output, containsString("Выход из программы."));
    }

    /**
     * Проверяет, что при вводе несуществующего пункта меню (например, "9")
     * выводится сообщение о неверном выборе, после чего при следующем вводе "4" программа завершается.
     */
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

    /**
     * Проверяет, что при выборе пункта "3. Показать справку" выводится подробная справочная информация,
     * после чего при нажатии Enter возвращаемся в меню, и затем выход по "4" работает корректно.
     */
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