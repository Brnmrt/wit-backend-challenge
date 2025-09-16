package com.bruno.calculator;

import com.bruno.calculator.dto.KafkaOperationDTO;
import com.bruno.calculator.dto.OperationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.bruno.calculator.util.JsonUtil;

@Component
public class CalculatorKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorKafkaListener.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Map<OperationType, OperationStrategy> strategies = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${calculator.topics.responses:calculator-responses}")
    private String responseTopic;

    public CalculatorKafkaListener(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        initializeStrategies();
    }

    private void initializeStrategies() {
        strategies.put(OperationType.SUM, new SumStrategy());
        strategies.put(OperationType.SUBTRACT, new SubtractStrategy());
        strategies.put(OperationType.MULTIPLY, new MultiplyStrategy());
        strategies.put(OperationType.DIVIDE, new DivideStrategy());
        logger.info("Estratégias de operação inicializadas: {}", strategies.keySet());
    }

    // o groupID é escalavel pois várias instâncias deste serviço podem ser ativas
    // e o kafka encarrega se de distribuir as mensagens entre elas
    @KafkaListener(topics = "${calculator.topics.requests:calculator-requests}", groupId = "${kafka.calculator.group-id:calculator}")
    // assim que uma mensagem chegar neste tópico, este método será chamado
    public void listen(String message) {
        String requestId = null;
        try {
            logger.info("Mensagem recebida no módulo calculator: {}", message);

            KafkaOperationDTO dto = JsonUtil.fromJson(message, KafkaOperationDTO.class);
            requestId = dto.getRequestId();

            // Adicionar requestId ao MDC para rastreamento completo
            MDC.put("requestId", requestId);

            logger.info("A processar operação: {} para requestId: {}", dto.getOperation(), requestId);

            // Validações de entrada
            if (dto.getA() == null || dto.getB() == null) {
                throw new IllegalArgumentException("Parâmetros 'a' e 'b' são obrigatórios");
            }

            OperationType type = OperationType.fromString(dto.getOperation());
            OperationStrategy strategy = strategies.get(type);

            if (strategy == null) {
                throw new IllegalArgumentException("Operação não suportada: " + dto.getOperation());
            }

            // Boa pratica, caso por exemplo algum dia queiram adicionar outro microservico
            // e ele se esqueca de validar a divisão por zero antes de enviar a mensagem para o kafka
            if (type == OperationType.DIVIDE && dto.getB().compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException("Divisão por zero não é permitida");
            }

            BigDecimal result = strategy.execute(dto.getA(), dto.getB());
            logger.info("Operação {} executada com sucesso para requestId {}: {}", dto.getOperation(), requestId, result);

            // Enviar resposta de sucesso
            sendResponse(requestId, "CONCLUIDO", result.toString(), null);

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem Kafka para requestId: {}", requestId, e);

            // Enviar resposta de erro
            if (requestId != null) {
                sendResponse(requestId, "ERROR", null, e.getMessage());
            }
        } finally {
            // Sempre limpar MDC, pois a thread que execotou o metodo nao morre,
            // caso isto nao fosse feito a thread iria para o pool com o requestId errado (antigo)
            MDC.remove("requestId");
        }
    }

    private void sendResponse(String requestId, String status, String result, String errorMessage) {
        try {
            OperationResponseDTO response = new OperationResponseDTO();
            response.setRequestId(requestId);
            response.setStatus(status);
            response.setResult(result);
            response.setErrorMessage(errorMessage);

            String responseJson = objectMapper.writeValueAsString(response); // Serializa para JSON

            logger.info("A enviar resposta para tópico {} com status {}: requestId={}",
                       responseTopic, status, requestId);

            kafkaTemplate.send(responseTopic, responseJson)
                .whenComplete((sendResult, exception) -> {
                    if (exception != null) {
                        logger.error("Erro ao enviar resposta para Kafka: requestId={}", requestId, exception);
                    } else {
                        logger.debug("Resposta enviada com sucesso para Kafka: requestId={}", requestId);
                    }
                });

        } catch (Exception ex) {
            logger.error("Erro ao serializar ou enviar resposta para requestId: {}", requestId, ex);
        }
    }
}
