package com.bruno.calculator;

import com.bruno.calculator.dto.KafkaOperationDTO;
import com.bruno.calculator.dto.OperationResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {RestApplication.class, CalculatorIntegrationTest.TestKafkaSimulator.class})
@EmbeddedKafka(partitions = 1, topics = {"calculator-requests", "calculator-responses"})
@ActiveProfiles("test")
class CalculatorIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Componente de teste para simular o serviço 'calculator'
    @Component
    public static class TestKafkaSimulator {
        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;
        private final ObjectMapper objectMapper = new ObjectMapper();

        @KafkaListener(topics = "calculator-requests", groupId = "test-calculator")
        public void listen(String message) {
            try {
                KafkaOperationDTO dto = objectMapper.readValue(message, KafkaOperationDTO.class);
                OperationResponseDTO response = new OperationResponseDTO();
                response.setRequestId(dto.getRequestId());
                response.setStatus("CONCLUIDO");
                switch (dto.getOperation()) {
                    case "sum":
                        response.setResult(dto.getA().add(dto.getB()).toString());
                        break;
                    default:
                        response.setResult("0");
                }
                String jsonResponse = objectMapper.writeValueAsString(response);
                kafkaTemplate.send("calculator-responses", jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testSumIntegration() throws InterruptedException {
        ResponseEntity<String> response = restTemplate.getForEntity("/sum?a=2&b=3", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("\"result\":5");
        // Extrai o requestId da resposta
    }

    @Test
    void testDivideByZero() {
        ResponseEntity<String> response = restTemplate.getForEntity("/divide?a=10&b=0", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).contains("Divisão por zero não é permitida");
        assertThat(response.getHeaders().get("X-Request-ID")).isNotNull();
    }

    @Test
    void testMissingParameter() {
        ResponseEntity<String> response = restTemplate.getForEntity("/sum?a=10", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).contains("Parâmetro 'b' é obrigatório");
        assertThat(response.getHeaders().get("X-Request-ID")).isNotNull();
    }
}