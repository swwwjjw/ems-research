package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

/**
 * Параметризованный тест для калькулятора RPN.
 * Проверяет как корректные, так и некорректные выражения.
 * Для каждого набора параметров тестируются оба алгоритма: медленный и быстрый.
 */
@RunWith(Parameterized.class)
public class RPNCalculatorTest {

    private final RPNCalculator calculator = new RPNCalculator();

    /**
     * Предоставляет набор параметров для корректных выражений.
     * Каждый параметр: строка с выражением и ожидаемый результат.
     * Покрытие: эквивалентные классы (EP) и граничные значения (BVA).
     *
     * @return коллекция массивов {выражение, ожидаемый результат}
     */
    @Parameters(name = "{index}: {0} -> {1}")
    public static Collection<Object[]> validExpressions() {
        return Arrays.asList(new Object[][]{
                {"3 4 *", 12.0},                     // EP-1: простейшее умножение
                {"3 4 * 3 +", 15.0},                  // EP-2: несколько операций
                {"3 2.33 *", 6.99},                   // EP-3: дробные числа
                {"-10 4 /", -2.5},                    // EP-4: отрицательные числа
                {"3 4 * 7 + 3 - 2 /", 8.0},            // EP-5: длинная цепочка
                {"3", 3.0},                            // BVA-1: единственное число
                {"3 3 * 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 /", 9.0}, // BVA-2: длинное выражение
                {"3 0 - 0 * 0 +", 0.0},                // BVA-3: операции с нулём
                {"10e300 10e300 *", Double.POSITIVE_INFINITY}, // BVA-5: переполнение
                {"2e-17 3e-17 *", 6e-34}               // BVA-6: очень малые числа
        });
    }

    // Текущие параметры теста (устанавливаются конструктором)
    private final String expression;
    private final double expected;

    /**
     * Конструктор параметризованного теста для корректных выражений.
     * @param expression выражение в обратной польской нотации
     * @param expected ожидаемый результат
     */
    public RPNCalculatorTest(String expression, double expected) {
        this.expression = expression;
        this.expected = expected;
    }

    /**
     * Проверяет, что оба алгоритма (медленный и быстрый) возвращают одинаковый
     * корректный результат для заданного выражения в пределах погрешности 1e-9.
     * Также проверяется, что результат совпадает с ожидаемым.
     */
    @Test
    public void testBothAlgorithms_returnSameResult() {
        // assumeTrue заменён на обычную проверку с сообщением
        assertNotNull(expression);
        assertFalse(expression.trim().isEmpty());

        String[] tokens = expression.split("\\s+");
        double slowResult = calculator.slowEvaluate(tokens);
        double fastResult = calculator.fastEvaluate(tokens);

        assertEquals(expected, slowResult, 1e-9);
        assertEquals(expected, fastResult, 1e-9);
        assertEquals(slowResult, fastResult, 1e-9);
    }

    // ==================== Внутренний класс для некорректных выражений ====================

    /**
     * Параметризованный тест для некорректных выражений.
     * Проверяет, что оба алгоритма выбрасывают IllegalArgumentException
     * с ожидаемой частью сообщения.
     */
    @RunWith(Parameterized.class)
    public static class InvalidExpressionsTest {

        private final RPNCalculator calculator = new RPNCalculator();
        private final String expression;
        private final String expectedMessagePart;

        /**
         * Предоставляет набор параметров для некорректных выражений.
         * Каждый параметр: выражение и ожидаемая подстрока сообщения об ошибке.
         *
         * @return коллекция массивов {выражение, ожидаемая подстрока сообщения}
         */
        @Parameters(name = "{index}: {0} -> {1}")
        public static Collection<Object[]> invalidExpressions() {
            return Arrays.asList(new Object[][]{
                    {"3 *", "Недостаточно операндов"},      // EP-6: недостаточно операндов
                    {"3 4 * 5", "Некорректное выражение"}, // EP-7: лишний операнд
                    {"2 5 %", "Некорректное выражение"},     // EP-8: неизвестный оператор
                    {"a 5 +", "Некорректное выражение"},   // EP-9: нечисловой токен
                    {"7 5 8 10", "Некорректное выражение"}, // EP-10: только операнды
                    {"- * * * +", "Недостаточно операндов"},// EP-11: только операторы
                    {"3 0 /", "Деление на ноль"}           // BVA-4: деление на ноль
            });
        }

        /**
         * Конструктор параметризованного теста для некорректных выражений.
         * @param expression некорректное выражение
         * @param expectedMessagePart ожидаемая часть сообщения об ошибке
         */
        public InvalidExpressionsTest(String expression, String expectedMessagePart) {
            this.expression = expression;
            this.expectedMessagePart = expectedMessagePart;
        }

        /**
         * Проверяет, что оба алгоритма выбрасывают IllegalArgumentException
         * с сообщением, содержащим ожидаемую подстроку.
         */
        @Test
        public void testBothAlgorithms_throwException() {
            String[] tokens = expression.isEmpty() ? new String[0] : expression.split("\\s+");

            // Проверка slowEvaluate
            try {
                calculator.slowEvaluate(tokens);
                fail("Expected IllegalArgumentException from slowEvaluate");
            } catch (IllegalArgumentException e) {
                assertThat(e.getMessage(), containsString(expectedMessagePart));
            }

            // Проверка fastEvaluate
            try {
                calculator.fastEvaluate(tokens);
                fail("Expected IllegalArgumentException from fastEvaluate");
            } catch (IllegalArgumentException e) {
                assertThat(e.getMessage(), containsString(expectedMessagePart));
            }
        }
    }

    // ==================== Отдельные специфичные тесты ====================

    /**
     * Проверяет, что медленный алгоритм корректно обрабатывает выражение,
     * состоящее только из чисел (без операторов) — должно быть исключение.
     */
    @Test
    public void testSlowEvaluate_noOperator() {
        String[] tokens = "5 3".split("\\s+");
        try {
            calculator.slowEvaluate(tokens);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Некорректное выражение"));
        }
    }

    /**
     * Проверяет, что медленный алгоритм выбрасывает исключение,
     * когда оператору не хватает операндов (например, "3 +").
     */
    @Test
    public void testSlowEvaluate_insufficientOperands() {
        String[] tokens = "3 +".split("\\s+");
        try {
            calculator.slowEvaluate(tokens);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Недостаточно операндов"));
        }
    }

    /**
     * Проверяет, что быстрый алгоритм выбрасывает исключение,
     * когда оператору не хватает операндов (например, "3 +").
     */
    @Test
    public void testFastEvaluate_insufficientOperands() {
        String[] tokens = "3 +".split("\\s+");
        try {
            calculator.fastEvaluate(tokens);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Недостаточно операндов"));
        }
    }

    /**
     * Проверяет, что быстрый алгоритм выбрасывает исключение,
     * когда после обработки в стеке остаётся более одного значения
     * (например, "3 4 5 +" → лишний операнд 3).
     */
    @Test
    public void testFastEvaluate_extraOperands() {
        String[] tokens = "3 4 5 +".split("\\s+");
        try {
            calculator.fastEvaluate(tokens);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Некорректное выражение"));
        }
    }
}