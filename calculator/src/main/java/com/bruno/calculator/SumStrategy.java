package com.bruno.calculator;

import java.math.BigDecimal;

public class SumStrategy implements OperationStrategy {
    @Override
    public BigDecimal execute(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }
}

