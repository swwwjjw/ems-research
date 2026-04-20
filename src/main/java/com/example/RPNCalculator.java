package com.example;

import java.util.*;

/**
 * Калькулятор для вычисления выражений в обратной польской нотации (RPN).
 * Поддерживает два алгоритма вычисления: медленный (на основе списка, O(n²))
 * и быстрый (на основе стека, O(n)).
 * Допустимые операторы: +, -, *, /.
 */
public class RPNCalculator {

    /**
     * Медленный алгоритм вычисления выражения в обратной польской нотации.
     * Работает путём многократного прохода по списку токенов: каждый раз находит
     * первый оператор, заменяет два предшествующих операнда результатом операции,
     * удаляет использованные операнды и сам оператор. Повторяет до тех пор,
     * пока в списке не останется один элемент — результат.
     * Сложность: O(n²) в худшем случае.
     *
     * @param tokens массив строк, представляющих токены выражения (числа и операторы)
     * @return результат вычисления
     * @throws IllegalArgumentException если выражение некорректно
     *         (недостаточно операндов, неизвестный оператор, деление на ноль и т.д.)
     */
    public double slowEvaluate(String[] tokens) {
        List<String> list = new ArrayList<>(Arrays.asList(tokens));

        while (list.size() > 1) {
            int operatorIndex = -1;
            for (int i = 0; i < list.size(); i++) {
                String token = list.get(i);
                if (isOperator(token)) {
                    operatorIndex = i;
                    break;
                }
            }

            if (operatorIndex == -1) {
                throw new IllegalArgumentException("Некорректное выражение");
            }
            if (operatorIndex < 2) {
                throw new IllegalArgumentException("Недостаточно операндов для оператора");
            }

            double a, b;
            try {
                b = Double.parseDouble(list.get(operatorIndex - 1));
                a = Double.parseDouble(list.get(operatorIndex - 2));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректное выражение");
            }
            String op = list.get(operatorIndex);

            double result = applyOperator(a, b, op);

            list.set(operatorIndex - 2, Double.toString(result));
            list.remove(operatorIndex);
            list.remove(operatorIndex - 1);
        }

        return Double.parseDouble(list.get(0));
    }

    /**
     * Быстрый алгоритм вычисления выражения в обратной польской нотации.
     * Использует стек: при чтении числа оно помещается в стек, при чтении оператора
     * извлекаются два верхних числа, применяется оператор, результат помещается обратно.
     * Сложность: O(n), где n — количество токенов.
     *
     * @param tokens массив строк, представляющих токены выражения (числа и операторы)
     * @return результат вычисления
     * @throws IllegalArgumentException если выражение некорректно
     *         (недостаточно операндов, неизвестный оператор, деление на ноль,
     *          неверное количество элементов в стеке после обработки и т.д.)
     */
    public double fastEvaluate(String[] tokens) {
        Deque<Double> stack = new ArrayDeque<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Недостаточно операндов для оператора");
                }
                double b = stack.pop();
                double a = stack.pop();
                double result = applyOperator(a, b, token);
                stack.push(result);
            } else {
                try {
                    stack.push(Double.parseDouble(token));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Некорректное выражение");
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Некорректное выражение");
        }
        return stack.pop();
    }

    /**
     * Проверяет, является ли токен допустимым оператором.
     *
     * @param token проверяемая строка
     * @return true, если токен равен "+", "-", "*" или "/"; иначе false
     */
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    /**
     * Применяет бинарную операцию к двум операндам.
     *
     * @param a первый операнд
     * @param b второй операнд
     * @param op оператор в виде строки ("+", "-", "*", "/")
     * @return результат операции a op b
     * @throws IllegalArgumentException если передан неизвестный оператор
     *         или при делении на ноль
     */
    private double applyOperator(double a, double b, String op) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new IllegalArgumentException("Деление на ноль");
                return a / b;
            default:
                throw new IllegalArgumentException("Неизвестный оператор");
        }
    }
}