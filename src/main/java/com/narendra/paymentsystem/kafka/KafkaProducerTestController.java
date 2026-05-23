package com.narendra.paymentsystem.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaProducerTestController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerTestController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public String send(@RequestParam String message) {
        kafkaTemplate.send("payment-topic", message);
        return "Message sent!";
    }
}