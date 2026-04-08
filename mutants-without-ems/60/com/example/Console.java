package com.example;

import java.util.Scanner;

public class Console {
    private final FileService fileService;
    private final RPNCalculator calculator;

    public Console(FileService fileService, RPNCalculator calculator) {
        this.fileService = fileService;
        this.calculator = calculator;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printMenu();
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                        manualInput(scanner);
                        break;
                    case "2":
                        processJsonFile(scanner);
                        break;
                    case "3":
                        showHelp();
                        pause(scanner);
                        break;
                    case "4":
                        System.out.println("Выход из программы.");
                        return;
                    default:
                        System.out.println("Неверный выбор. Попробуйте снова.");
                }
            }
        }
    }

    private void printMenu() {
        System.out.println("\n========== ГЛАВНОЕ МЕНЮ ==========");
        System.out.println("1. Ручной ввод выражения");
        System.out.println("2. Обработать JSON-файл");
        System.out.println("3. Показать справку");
        System.out.println("4. Выход");
        System.out.print("Выберите пункт: ");
    }

    public void manualInput(Scanner scanner) {
        System.out.println("\n--- Ручной ввод выражения ---");
        System.out.print("Введите выражение в обратной польской нотации (токены через пробел): ");
        String expression = scanner.nextLine().trim();
        if (expression.isEmpty()) {
            System.out.println("Выражение не может быть пустым.");
            return;
        }

        System.out.print("Выберите алгоритм (1 - медленный, 2 - быстрый): ");
        String algorithm = scanner.nextLine().trim();
        if (!algorithm.equals("1") && !algorithm.equals("2")) {
            System.out.println("Неверный выбор алгоритма.");
            return;
        }

        try {
            String[] tokens = expression.split("\\s+");
            double result;
            if (algorithm.equals("1")) {
                result = calculator.slowEvaluate(tokens);
            } else {
                result = calculator.fastEvaluate(tokens);
            }
            System.out.println("Результат: " + formatResult(result));
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка вычисления: " + e.getMessage());
        }
    }

    public void processJsonFile(Scanner scanner) {
        System.out.println("\n--- Обработка JSON-файла ---");
        System.out.print("Введите путь к входному JSON-файлу: ");
        String inputPath = scanner.nextLine().trim();

        System.out.print("Введите путь к выходному JSON-файлу (Enter для выхода в текущую папку): ");
        String outputPath = scanner.nextLine().trim();
        if (outputPath.isEmpty()) {
            outputPath = "output.json";
        }

        System.out.print("Выберите алгоритм (1 - медленный, 2 - быстрый): ");
        String algorithm = scanner.nextLine().trim();
        if (!algorithm.equals("1") && !algorithm.equals("2")) {
            System.out.println("Неверный выбор алгоритма.");
            return;
        }

        try {
            String expression = fileService.readExpressionFromJson(inputPath);
            String[] tokens = expression.split("\\s+");

            double result;
            if (algorithm.equals("1")) {
                result = calculator.slowEvaluate(tokens);
            } else {
                result = calculator.fastEvaluate(tokens);
            }

            fileService.writeResultToJson(outputPath, expression, result);
            System.out.println("Результат успешно записан в " + outputPath);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showHelp() {
        System.out.println("\n=================== СПРАВКА ===================");
        System.out.println("Калькулятор обратной польской нотации (RPN)");
        System.out.println("Обратная польская нотация (постфиксная) — форма записи,");
        System.out.println("в которой оператор следует за операндами. Например:");
        System.out.println("  инфиксная:  (3 + 4) * 5");
        System.out.println("  постфиксная: 3 4 + 5 *");
        System.out.println("Допустимые операторы: +  -  *  /");
        ;
        System.out.println("Отрицательные числа вводятся как обычно: -5 2 *");
        System.out.println("Алгоритмы вычисления:");
        System.out.println("  Медленный — на основе списка (O(n²)), демонстрационный.");
        System.out.println("  Быстрый — на основе стека (O(n)), используется на практике.");
        System.out.println("Режимы работы:");
        System.out.println("  1. Ручной ввод — введите строку токенов (через пробел) и");
        System.out.println("     выберите алгоритм. Результат выводится на экран.");
        System.out.println("  2. Обработка JSON файла — чтение выражения из файла,");
        System.out.println("     вычисление и сохранение результата в JSON.");
        System.out.println("Формат входного JSON файла:");
        System.out.println("  { \"expression\": \"5 1 2 + 4 * + 3 -\" }");
        System.out.println("Формат выходного JSON файла:");
        System.out.println("  {");
        System.out.println("    \"expression\": \"5 1 2 + 4 * + 3 -\",");
        System.out.println("    \"result\": 14");
        System.out.println("  }");
        System.out.println("  (целые числа записываются без десятичной точки)");
        System.out.println("Требования:");
        System.out.println("  - Java 11 или выше");
        System.out.println("  - Библиотека Gson (версия 2.8.9+) — для работы с JSON");
        System.out.println("=================================================");
    }

    private void pause(Scanner scanner) {
        System.out.print("Для возврата в меню нажмите Enter...");
        scanner.nextLine();
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }
}
