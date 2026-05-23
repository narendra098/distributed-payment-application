package com.narendra.paymentsystem.kafka.repository;

import com.narendra.paymentsystem.kafka.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, UUID> {

    /**
     * Fetches unpublished outbox events.
     */
    List<OutboxEvent> findByProcessedFalse();
}