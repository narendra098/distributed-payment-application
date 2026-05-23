package com.narendra.paymentsystem.orchestrator.dto;

import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {

    private UUID transactionId;

    private String senderId;

    private String receiverId;

    private BigDecimal amount;

    private TransactionStatus status;
}