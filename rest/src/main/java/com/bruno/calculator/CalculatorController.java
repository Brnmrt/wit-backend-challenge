package com.bruno.calculator;


import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
public class CalculatorController {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    private final OperationOrchestratorService orchestratorService;

    @Autowired
    public CalculatorController(OperationOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @GetMapping("/sum")
    public ResponseEntity<Map<String, Object>> sum(
            @RequestParam @NotNull(message = "Parâmetro 'a' é obrigatório") BigDecimal a,
            @RequestParam @NotNull(message = "Parâmetro 'b' é obrigatório") BigDecimal b) {
        return executeSync("sum", a, b);
    }

    @GetMapping("/subtract")
    public ResponseEntity<Map<String, Object>> subtract(
            @RequestParam @NotNull(message = "Parâmetro 'a' é obrigatório") BigDecimal a,
            @RequestParam @NotNull(message = "Parâmetro 'b' é obrigatório") BigDecimal b) {
        return executeSync("subtract", a, b);
    }

    @GetMapping("/multiply")
    public ResponseEntity<Map<String, Object>> multiply(
            @RequestParam @NotNull(message = "Parâmetro 'a' é obrigatório") BigDecimal a,
            @RequestParam @NotNull(message = "Parâmetro 'b' é obrigatório") BigDecimal b) {
        return executeSync("multiply", a, b);
    }

    @GetMapping("/divide")
    public ResponseEntity<Map<String, Object>> divide(
            @RequestParam @NotNull(message = "Parâmetro 'a' é obrigatório") BigDecimal a,
            @RequestParam @NotNull(message = "Parâmetro 'b' é obrigatório") BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Divisão por zero não é permitida");
        }
        return executeSync("divide", a, b);
    }


    private ResponseEntity<Map<String, Object>> executeSync(String operation, BigDecimal a, BigDecimal b) {

        String requestId = MDC.get("requestId");

        if (a == null || b == null) {
            throw new IllegalArgumentException("Ambos os parâmetros 'a' e 'b' são obrigatórios e devem ser números válidos.");
        }

        logger.info("A receber operação {} com a={}, b={}, requestId={}", operation, a, b, requestId);

        BigDecimal result = orchestratorService.syncRequest(operation, a, b, requestId);

        logger.info("Resultado calculado para requestId {}: {}", requestId, result);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);

        return ResponseEntity.ok()
                //.header("X-Request-ID", requestId)
                .body(response);


    }
}