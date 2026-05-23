package com.narendra.paymentsystem.transaction.controller;

import com.narendra.paymentsystem.transaction.dto.CreateTransactionRequest;
import com.narendra.paymentsystem.transaction.entity.Transaction;
import com.narendra.paymentsystem.transaction.entity.TransactionAudit;
import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import com.narendra.paymentsystem.transaction.repository.TransactionAuditRepository;
import com.narendra.paymentsystem.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction APIs", description = "Transaction tracking operations")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionAuditRepository auditRepository;

    @PostMapping
    @Operation(summary = "Create Transaction", description = "Creates a transaction")
    public Transaction createTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @PutMapping("/{transactionId}/status")
    @Operation(summary = "Update Transaction Status", description = "Update Transaction Status")
    public String updateStatus(@PathVariable UUID transactionId, @RequestParam TransactionStatus status) {
        transactionService.updateStatus(transactionId, status);
        return "Transaction status updated";
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get transaction", description = "Fetches transaction details")
    public Transaction getTransaction(@PathVariable UUID transactionId) {
        return transactionService.getTransaction(transactionId);
    }

    @GetMapping("/{transactionId}/audit")
    @Operation(summary = "Get Audit of Transaction", description = "Fetches complete transaction lifecycle history")
    public List<TransactionAudit> getAuditHistory(@PathVariable UUID transactionId) {
        return auditRepository.findByTransactionIdOrderByCreatedAtAsc(transactionId.toString());
    }
}