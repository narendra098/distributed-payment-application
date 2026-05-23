package com.narendra.paymentsystem.wallet.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyCreditedEvent {

    private String transactionId;
    private String userId;
    private String eventId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}