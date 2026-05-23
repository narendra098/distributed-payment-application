package com.narendra.paymentsystem.wallet.service;

import com.narendra.paymentsystem.common.exception.InsufficientBalanceException;
import com.narendra.paymentsystem.common.exception.WalletNotFoundException;
import com.narendra.paymentsystem.kafka.config.KafkaConfig;
import com.narendra.paymentsystem.kafka.producer.KafkaEventPublisher;
import com.narendra.paymentsystem.kafka.service.OutboxService;
import com.narendra.paymentsystem.wallet.dto.CreateWalletRequest;
import com.narendra.paymentsystem.wallet.entity.Wallet;
import com.narendra.paymentsystem.wallet.event.MoneyCreditedEvent;
import com.narendra.paymentsystem.wallet.event.MoneyDebitedEvent;
import com.narendra.paymentsystem.wallet.repository.WalletRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final OutboxService outboxService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Wallet createWallet(CreateWalletRequest request) {

        walletRepository.findByUserId(request.getUserId())
                .ifPresent(wallet -> {
                    throw new RuntimeException("Wallet already exists");
                });

        Wallet wallet = Wallet.builder()
                .userId(request.getUserId())
                .balance(request.getBalance())
                .build();

        return walletRepository.save(wallet);
    }


    /**
     LOCKS DB ROW
     read balance
     update balance
     UNLOCK DB ROW after commit

     If two requests try debiting same wallet, one waits, No balance corruption.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void debit(String userId, BigDecimal amount) {

        // Lock wallet row to prevent concurrent balance corruption
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        // Prevent negative balance
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance"
            );
        }

        // Deduct amount from sender wallet
        wallet.setBalance(
                wallet.getBalance().subtract(amount)
        );

        walletRepository.save(wallet);

        // Publish debit event for downstream consumers
        MoneyDebitedEvent event =
                MoneyDebitedEvent.builder()
                        .transactionId(UUID.randomUUID().toString())
                        .userId(userId)
                        .eventId(UUID.randomUUID().toString())
                        .amount(amount)
                        .timestamp(LocalDateTime.now())
                        .build();

        outboxService.saveEvent(
                "WALLET",
                userId,
                "MoneyDebitedEvent",
                event
        );
    }

    @Retry(name = "paymentService")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void credit(String userId, BigDecimal amount) {

        // Simulated failure to test saga compensation flow
        if ("FAIL_USER".equalsIgnoreCase(userId)) {
            throw new RuntimeException("Simulated credit failure");
        }

        // Lock wallet row during balance update
        Wallet wallet = walletRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() ->
                        new WalletNotFoundException("Wallet not found"));

        // Add money to receiver wallet
        wallet.setBalance(
                wallet.getBalance().add(amount)
        );

        walletRepository.save(wallet);

        // Publish credit event for downstream consumers
        MoneyCreditedEvent event =
                MoneyCreditedEvent.builder()
                        .transactionId(UUID.randomUUID().toString())
                        .userId(userId)
                        .eventId(UUID.randomUUID().toString())
                        .amount(amount)
                        .timestamp(LocalDateTime.now())
                        .build();

        outboxService.saveEvent(
                "WALLET",
                userId,
                "MoneyCreditedEvent",
                event
        );
    }

    public Wallet getWallet(String userId) {

        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }
}