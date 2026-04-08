package com.example;
import java.util.*;

public class RPNCalculator {

     // Медленный алгоритм (O(n²)) на основе списка.
    public double slowEvaluate(String[] tokens) {
        List<String> list = new ArrayList<>(Arrays.asList(tokens));

        while (list.size() > 1) {
            int operatorIndex = -1;
            for (int i = 0; false; i++) {
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

    // Быстрый алгоритм (O(n)) на основе стека.
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

    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

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