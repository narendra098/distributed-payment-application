package com.narendra.paymentsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

    public boolean isDuplicate(String idempotencyKey) {
        String cacheKey = IDEMPOTENCY_PREFIX + idempotencyKey;
        return redisTemplate.hasKey(cacheKey);
    }

    public void saveKey(String idempotencyKey, Object response) {
        String cacheKey = IDEMPOTENCY_PREFIX + idempotencyKey;
        redisTemplate.opsForValue().set(cacheKey, response, IDEMPOTENCY_TTL);
        log.info("Idempotency key saved: {}", idempotencyKey);
    }

    public Object getResponse(String idempotencyKey) {
        String cacheKey = IDEMPOTENCY_PREFIX + idempotencyKey;
        return redisTemplate.opsForValue().get(cacheKey);
    }
}