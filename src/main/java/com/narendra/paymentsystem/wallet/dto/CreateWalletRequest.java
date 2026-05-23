package com.narendra.paymentsystem.wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWalletRequest {

    @NotBlank
    private String userId;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal balance;
}