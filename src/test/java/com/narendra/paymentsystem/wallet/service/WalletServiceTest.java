package com.narendra.paymentsystem.wallet.service;

import com.narendra.paymentsystem.common.exception.InsufficientBalanceException;
import com.narendra.paymentsystem.kafka.service.OutboxService;
import com.narendra.paymentsystem.wallet.entity.Wallet;
import com.narendra.paymentsystem.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldDebitMoneySuccessfully() {

        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user1")
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(walletRepository.findByUserIdForUpdate("user1"))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(any(Wallet.class)))
                .thenReturn(wallet);

        walletService.debit("user1", BigDecimal.valueOf(100));

        assertEquals(
                BigDecimal.valueOf(900),
                wallet.getBalance()
        );

        verify(walletRepository, times(1))
                .save(wallet);

        verify(outboxService, times(1))
                .saveEvent(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void shouldThrowExceptionWhenBalanceInsufficient() {

        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user1")
                .balance(BigDecimal.valueOf(50))
                .build();

        when(walletRepository.findByUserIdForUpdate("user1"))
                .thenReturn(Optional.of(wallet));

        assertThrows(
                InsufficientBalanceException.class,
                () -> walletService.debit("user1", BigDecimal.valueOf(100))
        );

        verify(walletRepository, never())
                .save(any());

        verify(outboxService, never())
                .saveEvent(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }

    @Test
    void shouldCreditMoneySuccessfully() {

        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user2")
                .balance(BigDecimal.valueOf(500))
                .build();

        when(walletRepository.findByUserIdForUpdate("user2"))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(any(Wallet.class)))
                .thenReturn(wallet);

        walletService.credit("user2", BigDecimal.valueOf(100));

        assertEquals(
                BigDecimal.valueOf(600),
                wallet.getBalance()
        );

        verify(walletRepository, times(1))
                .save(wallet);

        verify(outboxService, times(1))
                .saveEvent(
                        any(),
                        any(),
                        any(),
                        any()
                );
    }
}