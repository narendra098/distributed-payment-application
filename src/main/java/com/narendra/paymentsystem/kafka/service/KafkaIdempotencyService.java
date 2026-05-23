package com.narendra.paymentsystem.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class KafkaIdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Checks whether event already processed.
     */
    public boolean isProcessed(String eventId) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(
                        getKey(eventId)
                )
        );
    }

    /**
     * Marks event as processed.
     */
    public void markProcessed(String eventId) {

        redisTemplate.opsForValue().set(
                getKey(eventId),
                "PROCESSED",
                Duration.ofHours(24)
        );
    }

    /**
     * Creates redis key.
     */
    private String getKey(String eventId) {

        return "kafka:processed:" + eventId;
    }
}