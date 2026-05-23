package com.narendra.paymentsystem.kafka.service;

import com.narendra.paymentsystem.kafka.entity.OutboxEvent;
import com.narendra.paymentsystem.kafka.producer.KafkaEventPublisher;
import com.narendra.paymentsystem.kafka.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaEventPublisher kafkaEventPublisher;

    /**
     * Periodically publishes unpublished outbox events.
     */
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOutboxEvents() {

        List<OutboxEvent> events =
                outboxEventRepository.findByProcessedFalse();

        for (OutboxEvent event : events) {

            try {

                log.info(
                        "Publishing outbox event: {}",
                        event.getEventType()
                );

                kafkaEventPublisher.publish(
                        "wallet-events",
                        event.getAggregateId(),
                        event.getPayload()
                );

                // Mark event as published
                event.setProcessed(true);

                outboxEventRepository.save(event);

            } catch (Exception e) {

                log.error(
                        "Failed to publish outbox event: {}",
                        event.getId(),
                        e
                );
            }
        }
    }
}