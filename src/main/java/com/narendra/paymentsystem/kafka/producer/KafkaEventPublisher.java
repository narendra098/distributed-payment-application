package com.narendra.paymentsystem.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes event to kafka topic.
     */
    public void publish(String topic, String key, Object event) {

        log.info("Publishing event to topic: {} with key: {}", topic, key);

        kafkaTemplate.send(topic, key, event);
    }
}