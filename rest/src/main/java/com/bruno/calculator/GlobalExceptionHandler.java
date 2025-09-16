package com.bruno.calculator;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // caso alguma excecao nao seja tratada, vem para aqui
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        String requestId = MDC.get("requestId");
        logger.error("Erro de argumento inválido para requestId: {}", requestId, ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Argumento inválido");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("X-Request-ID", requestId)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        String requestId = MDC.get("requestId");
        logger.error("Erro de validação para requestId: {}", requestId, ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Erro de validação");
        response.put("message", ex.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("X-Request-ID", requestId)
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String requestId = MDC.get("requestId");
        logger.error("Parâmetro obrigatório ausente para requestId: {}", requestId, ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Parâmetro obrigatório ausente");
        response.put("message", String.format("Parâmetro '%s' é obrigatório", ex.getParameterName()));
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("X-Request-ID", requestId)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String requestId = MDC.get("requestId");
        logger.error("Erro de tipo de argumento para requestId: {}", requestId, ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Tipo de parâmetro inválido");
        response.put("message", String.format("Parâmetro '%s' deve ser um número válido", ex.getName()));
        response.put("status", HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header("X-Request-ID", requestId)
                .body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String requestId = MDC.get("requestId");
        logger.error("Erro interno do servidor para requestId: {}", requestId, ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Erro interno do servidor");
        response.put("message", "Ocorreu um erro inesperado. Tente novamente.");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Request-ID", requestId)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        String requestId = MDC.get("requestId");
        logger.error("Erro não tratado para requestId: {}", requestId, ex);

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Erro interno");
        response.put("message", "Ocorreu um erro inesperado");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Request-ID", requestId)
                .body(response);
    }
}
