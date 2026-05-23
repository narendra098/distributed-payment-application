package com.narendra.paymentsystem.orchestrator.websocket;

import com.narendra.paymentsystem.transaction.entity.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionStatusPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Publishes realtime transaction status updates
     * to subscribed websocket clients.
     */
    public void publishStatus(
            UUID transactionId,
            TransactionStatus status
    ) {

        messagingTemplate.convertAndSend(
                "/topic/transactions/" + transactionId,
                status
        );
    }
}