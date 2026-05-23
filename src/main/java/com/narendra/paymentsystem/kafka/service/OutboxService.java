package com.narendra.paymentsystem.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.narendra.paymentsystem.kafka.entity.OutboxEvent;
import com.narendra.paymentsystem.kafka.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    /**
     * Stores event safely inside DB outbox table.
     */
    public void saveEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            Object payload
    ) {

        try {

            String jsonPayload =
                    objectMapper.writeValueAsString(payload);

            OutboxEvent outboxEvent =
                    OutboxEvent.builder()
                            .aggregateType(aggregateType)
                            .aggregateId(aggregateId)
                            .eventType(eventType)
                            .payload(jsonPayload)
                            .processed(false)
                            .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to serialize outbox event",
                    e
            );
        }
    }
}