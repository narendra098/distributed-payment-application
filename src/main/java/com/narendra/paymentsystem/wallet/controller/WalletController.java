package com.narendra.paymentsystem.wallet.controller;

import com.narendra.paymentsystem.wallet.dto.CreateWalletRequest;
import com.narendra.paymentsystem.wallet.dto.WalletTransactionRequest;
import com.narendra.paymentsystem.wallet.entity.Wallet;
import com.narendra.paymentsystem.wallet.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Tag(name = "Wallet APIs", description = "Wallet management operations")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    @Operation(summary = "Create wallet", description = "Creates wallet for user")
    public Wallet createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return walletService.createWallet(request);
    }

    @PostMapping("/debit")
    @Operation(summary = "Debit wallet", description = "Debits amount from user wallet")
    public String debit(@Valid @RequestBody WalletTransactionRequest request) {
        walletService.debit(
                request.getUserId(),
                request.getAmount()
        );
        return "Amount debited successfully";
    }

    @PostMapping("/credit")
    @Operation(summary = "Credit wallet", description = "Credit amount into user wallet")
    public String credit(@Valid @RequestBody WalletTransactionRequest request) {
        walletService.credit(
                request.getUserId(),
                request.getAmount()
        );
        return "Amount credited successfully";
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user wallet", description = "Get user wallet info")
    public Wallet getWallet(@PathVariable String userId) {
        return walletService.getWallet(userId);
    }
}