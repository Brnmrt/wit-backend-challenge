package com.bruno.calculator;

import com.bruno.calculator.dto.OperationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class CalculatorKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorKafkaListener.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private OperationOrchestratorService orchestratorService;
    
    public CalculatorKafkaListener(OperationOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @KafkaListener(topics = "#{'${calculator.topics.responses}'}", groupId = "#{'${kafka.group-id}'}")
    public void listen(String message) {
        logger.info("Mensagem recebida do tópico de respostas: '{}'", message);
        String requestId = null;

        try {
            OperationResponseDTO response = objectMapper.readValue(message, OperationResponseDTO.class);
            requestId = response.getRequestId();

            // Adicionar requestId ao MDC para rastreamento
            if (requestId != null) {
                MDC.put("requestId", requestId);
            }

            if ("ERROR".equals(response.getStatus())) {
                logger.error("Erro recebido para requestId: {}. Erro: {}", requestId, response.getErrorMessage());
                orchestratorService.completeExceptionally(requestId,
                    new RuntimeException(response.getErrorMessage() != null ? response.getErrorMessage() : "Erro desconhecido"));
            } else if ("CONCLUIDO".equals(response.getStatus())) {
                logger.info("Resultado recebido para o id: {}. Resultado: {}", requestId, response.getResult());
                try {
                    BigDecimal result = new BigDecimal(response.getResult());
                    orchestratorService.completeResult(requestId, result);
                } catch (NumberFormatException e) {
                    logger.error("Erro ao converter resultado para BigDecimal: {}", response.getResult(), e);
                    orchestratorService.completeExceptionally(requestId,
                        new RuntimeException("Formato de resultado inválido: " + response.getResult()));
                }
            } else {
                logger.warn("Status desconhecido na resposta: '{}' para requestId: {}", response.getStatus(), requestId);
                orchestratorService.completeExceptionally(requestId,
                    new RuntimeException("Status de resposta desconhecido: " + response.getStatus()));
            }
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem JSON do Kafka para requestId: {}", requestId, e);
            if (requestId != null) {
                orchestratorService.completeExceptionally(requestId, e);
            }
        } finally {
            // Limpar MDC
            MDC.remove("requestId");
        }
    }
}
