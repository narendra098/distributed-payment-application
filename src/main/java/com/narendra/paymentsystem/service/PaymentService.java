package com.narendra.paymentsystem.service;

import com.narendra.paymentsystem.config.KafkaConfig;
import com.narendra.paymentsystem.dto.PaymentRequestDto;
import com.narendra.paymentsystem.dto.PaymentResponseDto;
import com.narendra.paymentsystem.entity.Payment;
import com.narendra.paymentsystem.enums.PaymentStatus;
import com.narendra.paymentsystem.factory.PaymentStrategyFactory;
import com.narendra.paymentsystem.repository.PaymentRepository;
import com.narendra.paymentsystem.strategy.PaymentStrategy;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyService idempotencyService;
    private final KafkaTemplate<String, PaymentRequestDto> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PaymentStrategyFactory strategyFactory;

    private static final String PAYMENT_CACHE_PREFIX = "payment:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @CircuitBreaker(name = "paymentService", fallbackMethod = "initiatePaymentFallback")
    @RateLimiter(name = "paymentService")
    @Retry(name = "paymentService")
    @Transactional
    public PaymentResponseDto initiatePayment(PaymentRequestDto request, String idempotencyKey) {
        log.info("Initiating payment from {} to {} amount {}",
                request.getSenderId(), request.getReceiverId(), request.getAmount());

        // idempotency check
        if (idempotencyService.isDuplicate(idempotencyKey)) {
            log.info("Duplicate request detected for key: {}", idempotencyKey);
            return (PaymentResponseDto) idempotencyService.getResponse(idempotencyKey);
        }

        // validate using strategy pattern
        PaymentStrategy strategy = strategyFactory.getStrategy(request.getPaymentType());
        Payment payment = Payment.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .paymentType(request.getPaymentType())
                .status(PaymentStatus.INITIATED)
                .build();

        if (!strategy.validate(payment)) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new IllegalArgumentException("Payment validation failed for type: " + request.getPaymentType());
        }

        // save to db
        Payment savedPayment = paymentRepository.save(payment);

        // cache in redis
        cachePayment(savedPayment);

        // publish to kafka
        kafkaTemplate.send(KafkaConfig.PAYMENT_TOPIC, savedPayment.getId().toString(), request);

        PaymentResponseDto response = mapToResponse(savedPayment);

        // save idempotency key
        idempotencyService.saveKey(idempotencyKey, response);

        log.info("Payment initiated successfully with id: {}", savedPayment.getId());
        return response;
    }

    public PaymentResponseDto getPaymentStatus(UUID paymentId) {
        // check redis cache first
        String cacheKey = PAYMENT_CACHE_PREFIX + paymentId;
        PaymentResponseDto cached = (PaymentResponseDto) redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            log.info("Payment status fetched from cache for id: {}", paymentId);
            return cached;
        }

        // fallback to db
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        PaymentResponseDto response = mapToResponse(payment);
        cachePayment(payment);
        return response;
    }

    private void cachePayment(Payment payment) {
        String cacheKey = PAYMENT_CACHE_PREFIX + payment.getId();
        redisTemplate.opsForValue().set(cacheKey, mapToResponse(payment), CACHE_TTL);
    }

    // fallback method when circuit breaker opens
    public PaymentResponseDto initiatePaymentFallback(PaymentRequestDto request, String idempotencyKey, Throwable t) {
        log.error("Circuit breaker triggered for payment. Reason: {}", t.getMessage());
        return PaymentResponseDto.builder()
                .status(PaymentStatus.FAILED)
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .build();
    }

    private PaymentResponseDto mapToResponse(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .senderId(payment.getSenderId())
                .receiverId(payment.getReceiverId())
                .amount(payment.getAmount())
                .paymentType(payment.getPaymentType())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}