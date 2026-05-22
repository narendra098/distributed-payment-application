package com.narendra.paymentsystem.controller;

import com.narendra.paymentsystem.dto.PaymentRequestDto;
import com.narendra.paymentsystem.dto.PaymentResponseDto;
import com.narendra.paymentsystem.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment API", description = "High throughput payment processing system")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate a new payment")
    public ResponseEntity<PaymentResponseDto> initiatePayment(
            @RequestBody PaymentRequestDto request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        log.info("Payment request received from: {}", request.getSenderId());
        PaymentResponseDto response = paymentService.initiatePayment(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}/status")
    @Operation(summary = "Get payment status")
    public ResponseEntity<PaymentResponseDto> getPaymentStatus(@PathVariable UUID paymentId) {
        PaymentResponseDto response = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(response);
    }
}