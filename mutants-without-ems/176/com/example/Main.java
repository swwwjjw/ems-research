package com.example;

public class Main {
    public static void main(String[] args) {
        FileService fileService = new FileService();
        RPNCalculator calculator = new RPNCalculator();
        Console console = new Console(fileService, calculator);
        ;
    }
}