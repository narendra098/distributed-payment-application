package com.narendra.paymentsystem.transaction.repository;

import com.narendra.paymentsystem.transaction.entity.TransactionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionAuditRepository extends JpaRepository<TransactionAudit, Long> {

    List<TransactionAudit> findByTransactionIdOrderByCreatedAtAsc(String transactionId);
}