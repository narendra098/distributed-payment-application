package com.narendra.paymentsystem.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "payment-topic", groupId = "payment-group")
    public void consume(String message) {
        System.out.println("Received message: " + message);
    }
}