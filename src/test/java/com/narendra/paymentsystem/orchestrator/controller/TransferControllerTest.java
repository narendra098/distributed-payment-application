package com.narendra.paymentsystem.orchestrator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.narendra.paymentsystem.orchestrator.dto.TransferRequest;
import com.narendra.paymentsystem.orchestrator.dto.TransferResponse;
import com.narendra.paymentsystem.orchestrator.service.TransferSagaService;
import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferSagaService transferSagaService;

    @Test
    void shouldTransferMoneySuccessfully()
            throws Exception {

        TransferRequest request =
                new TransferRequest(
                        "user1",
                        "user2",
                        BigDecimal.valueOf(100)
                );

        TransferResponse response =
                TransferResponse.builder()
                        .transactionId(UUID.randomUUID())
                        .senderId("user1")
                        .receiverId("user2")
                        .amount(BigDecimal.valueOf(100))
                        .status(TransactionStatus.COMPLETED)
                        .build();

        when(
                transferSagaService.transfer(
                        any(),
                        any()
                )
        ).thenReturn(response);

        mockMvc.perform(
                        post("/transfer")
                                .header(
                                        "Idempotency-Key",
                                        "test-idempotency-key"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId")
                        .value("user1"))
                .andExpect(jsonPath("$.receiverId")
                        .value("user2"))
                .andExpect(jsonPath("$.status")
                        .value("COMPLETED"));
    }
}