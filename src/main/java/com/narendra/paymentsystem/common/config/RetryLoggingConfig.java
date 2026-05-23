package com.narendra.paymentsystem.common.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RetryLoggingConfig {

    private final RetryRegistry retryRegistry;

    /**
     * Logs print like:

     * Retry attempt: 1
     * Retry attempt: 2
     * Retry succeeded after attempts: 2

     * or:

     * Retry failed completely after attempts: 3
     */
    @PostConstruct
    public void registerRetryEvents() {

        Retry retry = retryRegistry.retry("paymentService");

        retry.getEventPublisher()

                .onRetry(event ->
                        log.warn(
                                "Retry attempt: {} for: {}",
                                event.getNumberOfRetryAttempts(),
                                event.getLastThrowable() != null
                                        ? event.getLastThrowable().getMessage()
                                        : "unknown"
                        )
                )

                .onSuccess(event ->
                        log.info(
                                "Retry succeeded after attempts: {}",
                                event.getNumberOfRetryAttempts()
                        )
                )

                .onError(event ->
                        log.error(
                                "Retry failed completely after attempts: {}",
                                event.getNumberOfRetryAttempts()
                        )
                );
    }
}