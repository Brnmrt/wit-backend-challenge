package com.bruno.calculator;

public enum OperationType {
    SUM, SUBTRACT, MULTIPLY, DIVIDE;

    public static OperationType fromString(String op) {
        switch (op.toLowerCase()) {
            case "sum":
                return SUM;
            case "subtract":
                return SUBTRACT;
            case "multiply":
                return MULTIPLY;
            case "divide":
                return DIVIDE;
            default:
                throw new IllegalArgumentException("Operação inválida: " + op);
        }
    }
}

