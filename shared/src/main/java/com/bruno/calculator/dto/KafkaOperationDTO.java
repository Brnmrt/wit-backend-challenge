package com.bruno.calculator.dto;

import java.math.BigDecimal;

public class KafkaOperationDTO {
    private String requestId;
    private String operation;
    private BigDecimal a;
    private BigDecimal b;

    public KafkaOperationDTO() {}

    public KafkaOperationDTO(String requestId, String operation, BigDecimal a, BigDecimal b) {
        this.requestId = requestId;
        this.operation = operation;
        this.a = a;
        this.b = b;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public BigDecimal getA() {
        return a;
    }

    public void setA(BigDecimal a) {
        this.a = a;
    }

    public BigDecimal getB() {
        return b;
    }

    public void setB(BigDecimal b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "KafkaOperationDTO{" +
                "requestId='" + requestId + '\'' +
                ", operation='" + operation + '\'' +
                ", a=" + a +
                ", b=" + b +
                '}';
    }
}