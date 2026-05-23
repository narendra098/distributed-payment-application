package com.narendra.paymentsystem.kafka.consumer;

import com.narendra.paymentsystem.kafka.service.KafkaIdempotencyService;
import com.narendra.paymentsystem.wallet.event.MoneyCreditedEvent;
import com.narendra.paymentsystem.wallet.event.MoneyDebitedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WalletEventConsumer {

    private final KafkaIdempotencyService kafkaIdempotencyService;

    /**
     * Consumes wallet events with retry + DLQ support.
     */
    @RetryableTopic(
            attempts = "3",
            backoff = @org.springframework.retry.annotation.Backoff(
                    delay = 2000
            ),
            dltTopicSuffix = "-dlt",
            topicSuffixingStrategy =
                    TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(
            topics = "wallet-events",
            groupId = "wallet-events-group"
    )
    public void consumeWalletEvents(Object event) {

        log.info("Consumed wallet event: {}", event);

        String eventId = null;

        // Extract eventId from supported events
        if (event instanceof MoneyDebitedEvent debitedEvent) {

            eventId = debitedEvent.getEventId();

        } else if (event instanceof MoneyCreditedEvent creditedEvent) {

            eventId = creditedEvent.getEventId();
        }

        // Ignore unsupported events
        if (eventId == null) {
            log.warn("Unknown event type received");
            return;
        }

        // Prevent duplicate event processing
        if (kafkaIdempotencyService.isProcessed(eventId)) {
            log.warn("Duplicate kafka event ignored: {}", eventId);
            return;
        }

        // Simulate consumer failure
        if (event.toString().contains("FAIL")) {
            throw new RuntimeException("Simulated kafka consumer failure");
        }

        // Mark event as processed
        kafkaIdempotencyService.markProcessed(eventId);

        log.info("Kafka event processed successfully: {}", eventId);
    }
}