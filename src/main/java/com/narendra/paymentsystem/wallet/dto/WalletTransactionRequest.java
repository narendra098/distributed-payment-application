package com.narendra.paymentsystem.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletTransactionRequest {

    @NotBlank
    private String userId;

    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}