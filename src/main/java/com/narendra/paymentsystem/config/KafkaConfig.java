package com.narendra.paymentsystem.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String PAYMENT_TOPIC = "payment-topic";
    public static final String PAYMENT_RETRY_TOPIC = "payment-retry-topic";
    public static final String PAYMENT_DLT_TOPIC = "payment-dlt-topic";

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name(PAYMENT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentRetryTopic() {
        return TopicBuilder.name(PAYMENT_RETRY_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentDltTopic() {
        return TopicBuilder.name(PAYMENT_DLT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}