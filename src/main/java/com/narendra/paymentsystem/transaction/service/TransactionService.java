package com.narendra.paymentsystem.transaction.service;

import com.narendra.paymentsystem.common.exception.TransactionNotFoundException;
import com.narendra.paymentsystem.orchestrator.websocket.TransactionStatusPublisher;
import com.narendra.paymentsystem.transaction.dto.CreateTransactionRequest;
import com.narendra.paymentsystem.transaction.entity.Transaction;
import com.narendra.paymentsystem.transaction.entity.TransactionAudit;
import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import com.narendra.paymentsystem.transaction.repository.TransactionAuditRepository;
import com.narendra.paymentsystem.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionAuditRepository auditRepository;
    private final TransactionStatusPublisher statusPublisher;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Transaction createTransaction(CreateTransactionRequest request) {

        // Create initial transaction record
        Transaction transaction = Transaction.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .status(TransactionStatus.PENDING)
                .build();

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        // Store audit history
        saveAudit(
                savedTransaction.getId().toString(),
                TransactionStatus.PENDING,
                "Transaction created"
        );

        return savedTransaction;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(UUID transactionId, TransactionStatus status) {

        Transaction transaction =
                transactionRepository.findById(transactionId)
                        .orElseThrow(() ->
                                new TransactionNotFoundException("Transaction not found"));

        // Update latest transaction state
        transaction.setStatus(status);

        transactionRepository.save(transaction);

        // Store transaction lifecycle history
        saveAudit(transactionId.toString(), status, "Transaction status changed to " + status);

        // Push realtime status update to websocket clients
        statusPublisher.publishStatus(transactionId, status);
    }

    public Transaction getTransaction(UUID transactionId) {

        return transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found"
                        ));
    }

    /**
     * Stores transaction lifecycle history.
     */
    private void saveAudit(
            String transactionId,
            TransactionStatus status,
            String message
    ) {

        TransactionAudit audit = TransactionAudit.builder()
                .transactionId(transactionId)
                .status(status)
                .message(message)
                .build();

        auditRepository.save(audit);
    }
}