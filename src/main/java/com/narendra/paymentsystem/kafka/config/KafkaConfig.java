package com.narendra.paymentsystem.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String WALLET_EVENTS_TOPIC = "wallet-events";
    public static final String TRANSACTION_EVENTS_TOPIC = "transaction-events";
    public static final String SAGA_EVENTS_TOPIC = "saga-events";

    //wallet consumers read wallet events
    @Bean
    public NewTopic walletEventsTopic() {

        return TopicBuilder.name(WALLET_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    //analytics reads transaction events
    @Bean
    public NewTopic transactionEventsTopic() {

        return TopicBuilder.name(TRANSACTION_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    // monitoring reads saga events
    @Bean
    public NewTopic sagaEventsTopic() {

        return TopicBuilder.name(SAGA_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}