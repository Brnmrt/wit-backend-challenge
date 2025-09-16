package com.bruno.calculator;

import java.math.BigDecimal;

public interface OperationStrategy {
    BigDecimal execute(BigDecimal a, BigDecimal b);
}

