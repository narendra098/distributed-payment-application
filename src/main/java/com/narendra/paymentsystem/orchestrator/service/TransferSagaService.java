package com.narendra.paymentsystem.orchestrator.service;

import com.narendra.paymentsystem.common.idempotency.IdempotencyService;
import com.narendra.paymentsystem.orchestrator.dto.TransferRequest;
import com.narendra.paymentsystem.orchestrator.dto.TransferResponse;
import com.narendra.paymentsystem.transaction.dto.CreateTransactionRequest;
import com.narendra.paymentsystem.transaction.entity.Transaction;
import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import com.narendra.paymentsystem.transaction.service.TransactionService;
import com.narendra.paymentsystem.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferSagaService {

    private final WalletService walletService;
    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;

    public TransferResponse transfer(String idempotencyKey, TransferRequest request) {

        // Atomically reserve idempotency key.
        // Prevents duplicate transfer execution.
        boolean isNewRequest =
                idempotencyService.saveIfAbsent(
                        idempotencyKey
                );

        // If request already exists,
        // return cached response safely.
        if (!isNewRequest) {

            Object cachedResponse =
                    idempotencyService.get(idempotencyKey);

            // Previous request still processing
            if ("PROCESSING".equals(cachedResponse)) {

                throw new RuntimeException(
                        "Request already in progress"
                );
            }

            return (TransferResponse) cachedResponse;
        }

        log.info("Starting transfer saga");

        Transaction transaction = transactionService.createTransaction(
                buildTransactionRequest(request)
        );

        try {

            log.info("Debiting sender wallet");

            walletService.debit(
                    request.getSenderId(),
                    request.getAmount()
            );

            transactionService.updateStatus(
                    transaction.getId(),
                    TransactionStatus.DEBIT_SUCCESS
            );

            log.info("Crediting receiver wallet");

            walletService.credit(
                    request.getReceiverId(),
                    request.getAmount()
            );

            transactionService.updateStatus(
                    transaction.getId(),
                    TransactionStatus.CREDIT_SUCCESS
            );

            transactionService.updateStatus(
                    transaction.getId(),
                    TransactionStatus.COMPLETED
            );

            log.info("Transfer completed successfully");

        } catch (Exception ex) {

            log.error("Transfer failed. Starting compensation");

            compensate(transaction, request);

            throw ex;
        }

        Transaction updatedTransaction =
                transactionService.getTransaction(
                        transaction.getId()
                );

        TransferResponse response =
                buildTransferResponse(updatedTransaction);

        // Replace PROCESSING marker with actual response
        idempotencyService.saveResponse(
                idempotencyKey,
                response
        );

        return response;
    }

    private void compensate(Transaction transaction, TransferRequest request) {

        try {

            walletService.credit(
                    request.getSenderId(),
                    request.getAmount()
            );

            transactionService.updateStatus(
                    transaction.getId(),
                    TransactionStatus.REFUNDED
            );

            log.info("Compensation completed successfully");

        } catch (Exception ex) {

            log.error("Compensation failed");

            transactionService.updateStatus(
                    transaction.getId(),
                    TransactionStatus.FAILED
            );
        }
    }

    private CreateTransactionRequest buildTransactionRequest(
            TransferRequest request
    ) {

        CreateTransactionRequest transactionRequest =
                new CreateTransactionRequest();

        transactionRequest.setSenderId(request.getSenderId());
        transactionRequest.setReceiverId(request.getReceiverId());
        transactionRequest.setAmount(request.getAmount());

        return transactionRequest;
    }

    private TransferResponse buildTransferResponse(
            Transaction transaction
    ) {

        return TransferResponse.builder()
                .transactionId(transaction.getId())
                .senderId(transaction.getSenderId())
                .receiverId(transaction.getReceiverId())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .build();
    }
}