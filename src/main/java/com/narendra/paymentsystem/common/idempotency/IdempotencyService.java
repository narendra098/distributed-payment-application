package com.narendra.paymentsystem.common.idempotency;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:";

    /**
       Atomically stores idempotency key.
     * Returns:
     * true  -> key stored successfully
     * false -> key already exists
     */
    public boolean saveIfAbsent(String key) {

        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                IDEMPOTENCY_PREFIX + key,
                "PROCESSING",
                Duration.ofMinutes(10)
        );

        return Boolean.TRUE.equals(success);
    }

    /**
       Stores actual transaction response.
     */
    public void saveResponse(
            String key,
            Object response
    ) {

        redisTemplate.opsForValue().set(
                IDEMPOTENCY_PREFIX + key,
                response,
                Duration.ofMinutes(10)
        );
    }

    /**
      Returns cached response.
     */
    public Object get(String key) {

        return redisTemplate.opsForValue().get(
                IDEMPOTENCY_PREFIX + key
        );
    }
}