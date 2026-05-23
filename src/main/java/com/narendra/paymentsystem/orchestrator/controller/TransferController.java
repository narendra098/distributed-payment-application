package com.narendra.paymentsystem.orchestrator.controller;

import com.narendra.paymentsystem.orchestrator.dto.TransferRequest;
import com.narendra.paymentsystem.orchestrator.dto.TransferResponse;
import com.narendra.paymentsystem.orchestrator.service.TransferSagaService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
@Tag(name = "Transfer APIs", description = "Money transfer operations")
public class TransferController {

    private final TransferSagaService transferSagaService;



    @PostMapping
    @RateLimiter(name = "paymentService")
    @Operation(summary = "Transfer money", description = "Transfers money between wallets using saga orchestration")
    public TransferResponse transfer(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                     @Valid @RequestBody TransferRequest request) {
        return transferSagaService.transfer(
                idempotencyKey,
                request
        );
    }
}