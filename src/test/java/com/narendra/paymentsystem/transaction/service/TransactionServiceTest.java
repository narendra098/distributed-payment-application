package com.narendra.paymentsystem.transaction.service;


import com.narendra.paymentsystem.orchestrator.websocket.TransactionStatusPublisher;
import com.narendra.paymentsystem.transaction.dto.CreateTransactionRequest;
import com.narendra.paymentsystem.transaction.entity.Transaction;
import com.narendra.paymentsystem.transaction.entity.TransactionAudit;
import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import com.narendra.paymentsystem.transaction.repository.TransactionAuditRepository;
import com.narendra.paymentsystem.transaction.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionAuditRepository auditRepository;

    @Mock
    private TransactionStatusPublisher statusPublisher;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCreateTransactionSuccessfully() {

        CreateTransactionRequest request =
                new CreateTransactionRequest(
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
                        .status(TransactionStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(
                transactionRepository.save(
                        any(Transaction.class)
                )
        ).thenReturn(transaction);

        Transaction savedTransaction =
                transactionService.createTransaction(request);

        assertNotNull(savedTransaction);

        assertEquals(
                transactionId,
                savedTransaction.getId()
        );

        assertEquals(
                "user1",
                savedTransaction.getSenderId()
        );

        assertEquals(
                "user2",
                savedTransaction.getReceiverId()
        );

        assertEquals(
                BigDecimal.valueOf(100),
                savedTransaction.getAmount()
        );

        assertEquals(
                TransactionStatus.PENDING,
                savedTransaction.getStatus()
        );

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));

        verify(auditRepository, times(1))
                .save(any(TransactionAudit.class));
    }

    @Test
    void shouldUpdateTransactionStatusSuccessfully() {

        UUID transactionId = UUID.randomUUID();

        Transaction transaction =
                Transaction.builder()
                        .id(transactionId)
                        .senderId("user1")
                        .receiverId("user2")
                        .amount(BigDecimal.valueOf(100))
                        .status(TransactionStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(
                transactionRepository.findById(
                        transactionId
                )
        ).thenReturn(Optional.of(transaction));

        when(
                transactionRepository.save(
                        any(Transaction.class)
                )
        ).thenReturn(transaction);

        transactionService.updateStatus(
                transactionId,
                TransactionStatus.COMPLETED
        );

        assertEquals(
                TransactionStatus.COMPLETED,
                transaction.getStatus()
        );

        verify(transactionRepository, times(1))
                .save(transaction);

        verify(auditRepository, times(1))
                .save(any(TransactionAudit.class));

        verify(statusPublisher, times(1))
                .publishStatus(
                        transactionId,
                        TransactionStatus.COMPLETED
                );
    }

    @Test
    void shouldGetTransactionSuccessfully() {

        UUID transactionId = UUID.randomUUID();

        Transaction transaction =
                Transaction.builder()
                        .id(transactionId)
                        .senderId("user1")
                        .receiverId("user2")
                        .amount(BigDecimal.valueOf(100))
                        .status(TransactionStatus.COMPLETED)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(
                transactionRepository.findById(
                        transactionId
                )
        ).thenReturn(Optional.of(transaction));

        Transaction result =
                transactionService.getTransaction(
                        transactionId
                );

        assertNotNull(result);

        assertEquals(
                transactionId,
                result.getId()
        );

        assertEquals(
                TransactionStatus.COMPLETED,
                result.getStatus()
        );
    }
}