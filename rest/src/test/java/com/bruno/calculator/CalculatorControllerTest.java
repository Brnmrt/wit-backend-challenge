package com.bruno.calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest já inclui a extensão do Spring, por isso @ExtendWith é redundante
@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OperationOrchestratorService orchestratorService;

    @Test
    void testSumEndpoint() throws Exception {
        when(orchestratorService.syncRequest(org.mockito.ArgumentMatchers.eq("sum"), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("1")), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("2")), org.mockito.ArgumentMatchers.anyString())).thenReturn(new java.math.BigDecimal("3"));

        mockMvc.perform(get("/sum?a=1&b=2")).andExpect(status().isOk()).andExpect(jsonPath("$.result").value(3)).andExpect(header().exists("X-Request-ID"));
    }

    @Test
    void testSubtractEndpoint() throws Exception {
        when(orchestratorService.syncRequest(org.mockito.ArgumentMatchers.eq("subtract"), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("5")), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("3")), org.mockito.ArgumentMatchers.anyString())).thenReturn(new java.math.BigDecimal("2"));

        mockMvc.perform(get("/subtract?a=5&b=3")).andExpect(status().isOk()).andExpect(jsonPath("$.result").value(2)).andExpect(header().exists("X-Request-ID"));
    }

    @Test
    void testMultiplyEndpoint() throws Exception {
        when(orchestratorService.syncRequest(org.mockito.ArgumentMatchers.eq("multiply"), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("2")), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("4")), org.mockito.ArgumentMatchers.anyString())).thenReturn(new java.math.BigDecimal("8"));

        mockMvc.perform(get("/multiply?a=2&b=4")).andExpect(status().isOk()).andExpect(jsonPath("$.result").value(8)).andExpect(header().exists("X-Request-ID"));
    }

    @Test
    void testDivideEndpoint() throws Exception {
        when(orchestratorService.syncRequest(org.mockito.ArgumentMatchers.eq("divide"), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("8")), org.mockito.ArgumentMatchers.eq(new java.math.BigDecimal("2")), org.mockito.ArgumentMatchers.anyString())).thenReturn(new java.math.BigDecimal("4"));

        mockMvc.perform(get("/divide?a=8&b=2")).andExpect(status().isOk()).andExpect(jsonPath("$.result").value(4)).andExpect(header().exists("X-Request-ID"));
    }
}