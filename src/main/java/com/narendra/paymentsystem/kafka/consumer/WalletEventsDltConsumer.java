package com.narendra.paymentsystem.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WalletEventsDltConsumer {

    /**
     * Consumes permanently failed wallet events.
     */
    @KafkaListener(
            topics = "wallet-events-dlt",
            groupId = "wallet-events-dlt-group"
    )
    public void consumeDeadLetterEvent(
            Object event
    ) {

        log.error(
                "Received event in DLQ: {}",
                event
        );

        // Later:
        // save to DB
        // alert monitoring
        // manual replay
    }
}