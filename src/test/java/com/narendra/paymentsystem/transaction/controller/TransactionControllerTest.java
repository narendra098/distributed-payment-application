package com.narendra.paymentsystem.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.narendra.paymentsystem.transaction.entity.Transaction;
import com.narendra.paymentsystem.transaction.entity.TransactionAudit;
import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import com.narendra.paymentsystem.transaction.repository.TransactionAuditRepository;
import com.narendra.paymentsystem.transaction.service.TransactionService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionAuditRepository auditRepository;

    @Test
    void shouldGetTransactionSuccessfully()
            throws Exception {

        UUID transactionId = UUID.randomUUID();

        Transaction transaction =
                Transaction.builder()
                        .id(transactionId)
                        .senderId("user1")
                        .receiverId("user2")
                        .amount(BigDecimal.valueOf(100))
                        .status(TransactionStatus.COMPLETED)
                        .build();

        when(
                transactionService.getTransaction(
                        transactionId
                )
        ).thenReturn(transaction);

        mockMvc.perform(
                        get("/transactions/" + transactionId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderId")
                        .value("user1"))
                .andExpect(jsonPath("$.receiverId")
                        .value("user2"))
                .andExpect(jsonPath("$.status")
                        .value("COMPLETED"));
    }

    @Test
    void shouldGetTransactionAuditSuccessfully()
            throws Exception {

        UUID transactionId = UUID.randomUUID();

        TransactionAudit audit =
                TransactionAudit.builder()
                        .transactionId(transactionId.toString())
                        .status(TransactionStatus.COMPLETED)
                        .message("Transfer completed")
                        .createdAt(LocalDateTime.now())
                        .build();

        when(
                auditRepository
                        .findByTransactionIdOrderByCreatedAtAsc(
                                transactionId.toString()
                        )
        ).thenReturn(List.of(audit));

        mockMvc.perform(
                        get("/transactions/" + transactionId + "/audit")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message")
                        .value("Transfer completed"));
    }
}