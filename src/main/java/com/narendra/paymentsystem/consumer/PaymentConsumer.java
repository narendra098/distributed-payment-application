package com.narendra.paymentsystem.consumer;

import com.narendra.paymentsystem.config.KafkaConfig;
import com.narendra.paymentsystem.dto.PaymentRequestDto;
import com.narendra.paymentsystem.entity.Payment;
import com.narendra.paymentsystem.enums.PaymentStatus;
import com.narendra.paymentsystem.factory.PaymentStrategyFactory;
import com.narendra.paymentsystem.repository.PaymentRepository;
import com.narendra.paymentsystem.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory strategyFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PAYMENT_CACHE_PREFIX = "payment:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @KafkaListener(
            topics = KafkaConfig.PAYMENT_TOPIC,
            groupId = "payment-group",
            concurrency = "3"
    )
    @Transactional
    public void consumePayment(
            @Payload PaymentRequestDto request,
            @Header(KafkaHeaders.RECEIVED_KEY) String paymentId) {

        log.info("Consuming payment: {}", paymentId);

        try {
            Payment payment = paymentRepository.findById(UUID.fromString(paymentId))
                    .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

            // update status to processing
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
            updateCache(payment);

            // process using strategy
            PaymentStrategy strategy = strategyFactory.getStrategy(request.getPaymentType());
            strategy.process(payment);

            // update status to success
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
            updateCache(payment);

            log.info("Payment {} processed successfully", paymentId);

        } catch (Exception e) {
            log.error("Error processing payment {}: {}", paymentId, e.getMessage());
            handleFailedPayment(paymentId);
        }
    }

    @KafkaListener(topics = KafkaConfig.PAYMENT_DLT_TOPIC, groupId = "payment-dlt-group")
    public void consumeDlt(@Payload PaymentRequestDto request,
                           @Header(KafkaHeaders.RECEIVED_KEY) String paymentId) {
        log.error("Payment {} landed in dead letter topic. Manual intervention needed!", paymentId);
        handleFailedPayment(paymentId);
    }

    private void handleFailedPayment(String paymentId) {
        paymentRepository.findById(UUID.fromString(paymentId)).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            updateCache(payment);
        });
    }

    private void updateCache(Payment payment) {
        String cacheKey = PAYMENT_CACHE_PREFIX + payment.getId();
        redisTemplate.opsForValue().set(cacheKey, payment, CACHE_TTL);
    }
}