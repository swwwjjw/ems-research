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

@RunWith(Parameterized.class)
public class RPNCalculatorTest {

    private final RPNCalculator calculator = new RPNCalculator();

    // Параметры для корректных выражений (EP, BVA)
    @Parameters(name = "{index}: {0} -> {1}")
    public static Collection<Object[]> validExpressions() {
        return Arrays.asList(new Object[][]{
                {"3 4 *", 12.0},                     // EP-1
                {"3 4 * 3 +", 15.0},                  // EP-2
                {"3 2.33 *", 6.99},                   // EP-3
                {"-10 4 /", -2.5},                    // EP-4
                {"3 4 * 7 + 3 - 2 /", 8.0},            // EP-5
                {"3", 3.0},                            // BVA-1
                {"3 3 * 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 / 2 * 2 /", 9.0}, // BVA-2
                {"3 0 - 0 * 0 +", 0.0},                // BVA-3
                {"10e300 10e300 *", Double.POSITIVE_INFINITY}, // BVA-5
                {"2e-17 3e-17 *", 6e-34}               // BVA-6
        });
    }

    private final String expression;
    private final double expected;

    public RPNCalculatorTest(String expression, double expected) {
        this.expression = expression;
        this.expected = expected;
    }

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

    // Второй параметризованный класс для некорректных выражений
    @RunWith(Parameterized.class)
    public static class InvalidExpressionsTest {

        private final RPNCalculator calculator = new RPNCalculator();
        private final String expression;
        private final String expectedMessagePart;

        @Parameters(name = "{index}: {0} -> {1}")
        public static Collection<Object[]> invalidExpressions() {
            return Arrays.asList(new Object[][]{
                    {"3 *", "Недостаточно операндов"},      // EP-6
                    {"3 4 * 5", "Некорректное выражение"}, // EP-7
                    {"2 5 %", "Некорректное выражение"},     // EP-8
                    {"a 5 +", "Некорректное выражение"},   // EP-9
                    {"7 5 8 10", "Некорректное выражение"}, // EP-10
                    {"- * * * +", "Недостаточно операндов"},// EP-11
                    {"3 0 /", "Деление на ноль"}           // BVA-4
            });
        }

        public InvalidExpressionsTest(String expression, String expectedMessagePart) {
            this.expression = expression;
            this.expectedMessagePart = expectedMessagePart;
        }

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

    // Отдельные специфичные тесты
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