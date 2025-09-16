package com.bruno.calculator;

import java.math.BigDecimal;

public class SubtractStrategy implements OperationStrategy {
    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }
}

