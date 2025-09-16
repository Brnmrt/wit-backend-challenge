package com.bruno.calculator;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {
    private final CalculatorService service = new CalculatorService();

    @Test
    void testSum() {
        assertEquals(new BigDecimal("5"), service.sum(new BigDecimal("2"), new BigDecimal("3")));
    }

    @Test
    void testSubtract() {
        assertEquals(new BigDecimal("-1"), service.subtract(new BigDecimal("2"), new BigDecimal("3")));
    }

    @Test
    void testMultiply() {
        assertEquals(new BigDecimal("6"), service.multiply(new BigDecimal("2"), new BigDecimal("3")));
    }

    @Test
    void testDivide() {
        assertEquals(new BigDecimal("2.0000000000"), service.divide(new BigDecimal("6"), new BigDecimal("3")));
    }

    @Test
    void testDivideByZero() {
        assertThrows(IllegalArgumentException.class, () -> service.divide(new BigDecimal("1"), BigDecimal.ZERO));
    }
}


