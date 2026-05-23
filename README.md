# Distributed Payment System

A production-inspired distributed wallet-to-wallet payment system built using Spring Boot, Kafka, Redis, and PostgreSQL.

This project focuses on solving real distributed systems problems like:
- duplicate requests
- concurrent money updates
- reliable event publishing
- retries
- dead letter queues
- saga-based transaction coordination
- idempotent consumers
- audit tracking

---

# Architecture

```text
Client
   ↓
Transfer Controller
   ↓
Saga Orchestrator
   ↓
Wallet Service
   ↓
Transaction Service
   ↓
Outbox Pattern
   ↓
Kafka Events
   ↓
Consumers / DLQ
```

---

# Tech Stack

- Java 17
- Spring Boot
- PostgreSQL
- Redis
- Kafka / Redpanda
- WebSocket
- Swagger OpenAPI
- JUnit + Mockito
- Lombok

---

# Core Features

## Wallet Management

- Create wallet
- Get wallet balance
- Debit wallet
- Credit wallet

---

## Distributed Saga Orchestration

Money transfer follows Saga Pattern using orchestration approach.

### Flow

```text
Transfer Request
    ↓
Create Transaction
    ↓
Debit Sender
    ↓
Credit Receiver
    ↓
Mark Transaction Completed
```

### Compensation Handling

If credit fails after debit:

```text
Refund sender wallet
Mark transaction refunded
```

---

# Real Distributed Systems Problems Solved

## 1. Idempotent APIs

Prevents duplicate money transfer requests.

### Problem

Client retries same request multiple times due to:
- timeout
- network failure
- frontend retry

Without idempotency:
money could be debited twice.

### Solution

Redis-based idempotency keys.

```text
Idempotency-Key header
        ↓
Redis check
        ↓
Duplicate request blocked
```

---

## 2. Pessimistic Locking

Prevents race conditions during concurrent transfers.

### Problem

Two transfers trying to debit same wallet simultaneously.

### Solution

Database row locking using:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

Ensures safe balance updates.

---

## 3. Kafka Event-Driven Architecture

Wallet events are published asynchronously.

### Published Events

- MoneyDebitedEvent
- MoneyCreditedEvent

### Benefits

Other services can react independently:
- notifications
- analytics
- fraud detection
- audit pipelines

---

## 4. Retry + Dead Letter Queue (DLQ)

### Problem

Kafka consumer failures can lose events.

### Solution

Automatic retries using:

```text
Main Topic
   ↓
Retry Topic
   ↓
Dead Letter Queue
```

Failed events safely move to DLQ for investigation.

---

## 5. Outbox Pattern

### Problem

Database commit succeeds but Kafka publish fails.

Causes inconsistent state.

### Solution

Events first stored in database outbox table.

```text
DB Transaction
    ↓
Save Outbox Event
    ↓
Commit
    ↓
Background Publisher
    ↓
Kafka
```

Guarantees reliable event publishing.

---

## 6. Idempotent Kafka Consumers

### Problem

Kafka guarantees at-least-once delivery.

Same event may arrive multiple times.

### Solution

Redis-based event deduplication.

```text
eventId
   ↓
Redis processed check
   ↓
Ignore duplicates
```

---

## 7. Transaction Audit Trail

Every transaction state transition is recorded.

### Example

```text
PENDING
DEBIT_SUCCESS
CREDIT_SUCCESS
COMPLETED
```

Useful for:
- debugging
- compliance
- payment tracing

---

## 8. Realtime Transaction Updates

Implemented using WebSocket.

Clients can subscribe to live transaction updates.

```text
/topic/transactions/{transactionId}
```

---

## 9. Rate Limiting + Retry + Resilience

Implemented using Resilience4j.

Features:
- retry
- circuit breaker
- rate limiter

---

# Testing

Project includes:

## Controller Tests

- WalletControllerTest
- TransferControllerTest
- TransactionControllerTest

## Service Tests

- WalletServiceTest
- TransferSagaServiceTest
- TransactionServiceTest

Uses:
- JUnit 5
- Mockito

---

# Swagger API Docs

Open:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Kafka Topics

```text
wallet-events
wallet-events-retry
wallet-events-dlt
```

---

# Running Locally

Infrastructure runs through WSL:
- PostgreSQL
- Redis
- Redpanda

Start Spring Boot app:

```bash
mvn spring-boot:run
```

---

# Example Transfer Request

```http
POST /transfer
```

Headers:

```text
Idempotency-Key: transfer-001
```

Body:

```json
{
  "senderId": "user1",
  "receiverId": "user2",
  "amount": 100
}
```

---

# Why This Project Matters

This project is not just CRUD.

It demonstrates understanding of:
- distributed systems
- payment consistency
- reliability engineering
- event-driven architecture
- saga patterns
- resiliency patterns
- production-grade backend design

---

# Future Improvements

- Async Saga Orchestration
- Choreography-based Saga
- Prometheus + Grafana Monitoring
- Distributed Tracing
- Docker Compose
- Kubernetes Deployment
- Notification Service
- Fraud Detection Service
- Multi-service Deployment
