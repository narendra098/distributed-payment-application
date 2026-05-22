package com.narendra.paymentsystem.producer;

import com.narendra.paymentsystem.config.KafkaConfig;
import com.narendra.paymentsystem.dto.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentRequestDto> kafkaTemplate;

    public void sendPayment(String paymentId, PaymentRequestDto request) {
        CompletableFuture<SendResult<String, PaymentRequestDto>> future =
                kafkaTemplate.send(KafkaConfig.PAYMENT_TOPIC, paymentId, request);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send payment {} to kafka. Error: {}", paymentId, ex.getMessage());
                sendToDlt(paymentId, request);
            } else {
                log.info("Payment {} sent to kafka. Partition: {}, Offset: {}",
                        paymentId,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }

    private void sendToDlt(String paymentId, PaymentRequestDto request) {
        log.warn("Sending payment {} to dead letter topic", paymentId);
        kafkaTemplate.send(KafkaConfig.PAYMENT_DLT_TOPIC, paymentId, request);
    }
}