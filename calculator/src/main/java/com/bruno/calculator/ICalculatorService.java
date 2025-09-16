package com.bruno.calculator;

import java.math.BigDecimal;

public interface ICalculatorService {
    BigDecimal sum(BigDecimal a, BigDecimal b);

    BigDecimal subtract(BigDecimal a, BigDecimal b);

    BigDecimal multiply(BigDecimal a, BigDecimal b);

    BigDecimal divide(BigDecimal a, BigDecimal b);
}

