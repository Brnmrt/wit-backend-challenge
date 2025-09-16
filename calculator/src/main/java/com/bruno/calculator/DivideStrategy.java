package com.bruno.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DivideStrategy implements OperationStrategy {
    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Divisão por zero não é permitida");
        }
        // Usar precisão de 34 dígitos e HALF_UP para melhor precisão
        return a.divide(b, 34, RoundingMode.HALF_UP);
    }
}
