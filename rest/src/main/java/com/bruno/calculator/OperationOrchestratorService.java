package com.bruno.calculator;

import com.bruno.calculator.dto.KafkaOperationDTO;
import com.bruno.calculator.util.JsonUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.*;

@Service
public class OperationOrchestratorService {
    private static final Logger logger = LoggerFactory.getLogger(OperationOrchestratorService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    // Evita concorrência (especie de mutex mas só bloqueia algumas partes do mapa, mas deixa
    // outras threads acessarem outras partes do mapa), ao usar se um mutex iria afetar a performance
    //pois cada thread iria esperar que a outra terminasse
    private final ConcurrentMap<String, CompletableFuture<BigDecimal>> pendingResults = new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleanupExecutor = Executors.newScheduledThreadPool(1);

    @Value("${calculator.topics.requests}")
    private String kafkaRequestsTopic;

    @Value("${calculator.topics.responses}")
    private String kafkaResponsesTopic;

    public OperationOrchestratorService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        // a cada 30 segundos, limpa requisições pendentes que já foram completas
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredRequests, 30, 30, TimeUnit.SECONDS);
    }


    public BigDecimal syncRequest(String operation, BigDecimal a, BigDecimal b, String requestId) {
        // Propagar requestId através do MDC (garantindo que logs relacionados tenham o requestId correto)
        MDC.put("requestId", requestId);

        try {
            logger.info("A iniciar processamento síncrono para operação: {}", operation);

            KafkaOperationDTO kafkaOperation = new KafkaOperationDTO(requestId, operation, a, b);
            String message = JsonUtil.toJson(kafkaOperation);

            CompletableFuture<BigDecimal> future = new CompletableFuture<>(); // espera resposta
            pendingResults.put(requestId, future); // armazena future para completar depois

            ProducerRecord<String, String> record = new ProducerRecord<>(kafkaRequestsTopic, message);
            record.headers().add(new RecordHeader("correlation-id", requestId.getBytes()));
            record.headers().add(new RecordHeader("request-id", requestId.getBytes()));

            logger.info("A enviar mensagem para Kafka no tópico: {}", kafkaRequestsTopic);
            kafkaTemplate.send(record); // envia mensagem para Kafka, mas não espera resposta

            try {
                BigDecimal result = future.get(30, TimeUnit.SECONDS); // para uma arquitetura
                // escalavel, deveria nao devia esperar aqui, mas sim retornar um link com o requestId e depois o cliente
                // poderia consultar o estado do requestId
                logger.info("Resultado recebido com sucesso para requestId: {}", requestId);
                return result;
            } catch (TimeoutException e) {
                pendingResults.remove(requestId);
                logger.error("Timeout ao aguardar resposta do cálculo para requestId: {}", requestId);
                throw new RuntimeException("Timeout ao aguardar resposta do cálculo.");
            } catch (Exception e) {

                logger.error("Erro ao processar cálculo para requestId: {}", requestId, e);
                throw new RuntimeException("Erro ao processar cálculo: " + e.getMessage());
            }
        } finally {
            MDC.remove("requestId"); //previne id errado em logs futuros
        }
    }

    // Método chamado pelo listener ao receber resposta do Kafka
    public void completeResult(String requestId, BigDecimal result) {
        CompletableFuture<BigDecimal> future = pendingResults.get(requestId);
        if (future != null) {
            future.complete(result); // completa a future com o resultado e acorda a thread que estava a espera
            pendingResults.remove(requestId);
            logger.debug("Resultado completo para requestId: {}", requestId);
        } else {
            logger.warn("Tentativa de completar resultado para requestId inexistente: {}", requestId);
        }
    }

    public void completeExceptionally(String requestId, Exception exception) {
        CompletableFuture<BigDecimal> future = pendingResults.get(requestId);
        if (future != null) {
            future.completeExceptionally(exception);
            pendingResults.remove(requestId);
            logger.error("Resultado completo com erro para requestId: {}", requestId, exception);
        }
    }

    // Cleanup method para evitar memory leaks que a parte de cima não cobre
    private void cleanupExpiredRequests() {
        logger.debug("A exceutar limpeza de requisições pendentes. Total: {}", pendingResults.size());
        pendingResults.entrySet().removeIf(entry -> entry.getValue().isDone());
    }


    public void shutdown() { //evita ter de fechar o programa à força (evitar threads pendentes após encerramento)
        cleanupExecutor.shutdown();
        try {
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
