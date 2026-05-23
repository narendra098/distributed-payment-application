package com.narendra.paymentsystem.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConsumerConfig {

    public static final String WALLET_EVENTS_DLT =
            "wallet-events-dlt";

    @Bean
    public NewTopic walletEventsDltTopic() {

        return TopicBuilder
                .name(WALLET_EVENTS_DLT)
                .partitions(3)
                .replicas(1)
                .build();
    }
}