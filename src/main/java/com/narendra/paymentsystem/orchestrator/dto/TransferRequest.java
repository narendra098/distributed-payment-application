package com.narendra.paymentsystem.orchestrator.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    @NotBlank
    private String senderId;

    @NotBlank
    private String receiverId;

    @DecimalMin(value = "0.01")
    private BigDecimal amount;
}