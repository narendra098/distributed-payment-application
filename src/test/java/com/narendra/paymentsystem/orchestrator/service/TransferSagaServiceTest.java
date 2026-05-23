package com.narendra.paymentsystem.orchestrator.service;

import com.narendra.paymentsystem.common.idempotency.IdempotencyService;
import com.narendra.paymentsystem.orchestrator.dto.TransferRequest;
import com.narendra.paymentsystem.orchestrator.dto.TransferResponse;
import com.narendra.paymentsystem.transaction.entity.Transaction;
import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import com.narendra.paymentsystem.transaction.service.TransactionService;
import com.narendra.paymentsystem.wallet.service.WalletService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferSagaServiceTest {

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private IdempotencyService idempotencyService;

    @InjectMocks
    private TransferSagaService transferSagaService;

    @Test
    void shouldTransferSuccessfully() {

        TransferRequest request =
                new TransferRequest(
                        "user1",
                        "user2",
                        BigDecimal.valueOf(100)
                );

        UUID transactionId = UUID.randomUUID();

        Transaction transaction =
                Transaction.builder()
                        .id(transactionId)
                        .senderId("user1")
                        .receiverId("user2")
                        .amount(BigDecimal.valueOf(100))
                        .status(TransactionStatus.COMPLETED)
                        .build();

        // New idempotent request
        when(
                idempotencyService.saveIfAbsent(
                        any()
                )
        ).thenReturn(true);

        when(
                transactionService.createTransaction(
                        any()
                )
        ).thenReturn(transaction);

        when(
                transactionService.getTransaction(
                        transactionId
                )
        ).thenReturn(transaction);

        TransferResponse response =
                transferSagaService.transfer(
                        "test-idempotency-key",
                        request
                );

        assertNotNull(response);

        assertEquals(
                "user1",
                response.getSenderId()
        );

        assertEquals(
                "user2",
                response.getReceiverId()
        );

        assertEquals(
                TransactionStatus.COMPLETED,
                response.getStatus()
        );

        verify(walletService, times(1))
                .debit(
                        "user1",
                        BigDecimal.valueOf(100)
                );

        verify(walletService, times(1))
                .credit(
                        "user2",
                        BigDecimal.valueOf(100)
                );

        verify(idempotencyService, times(1))
                .saveResponse(
                        any(),
                        any()
                );
    }
}