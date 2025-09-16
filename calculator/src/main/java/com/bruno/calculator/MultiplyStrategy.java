package com.bruno.calculator;

import java.math.BigDecimal;

public class MultiplyStrategy implements OperationStrategy {
    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }
}
