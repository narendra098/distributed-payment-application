package com.narendra.paymentsystem.transaction.repository;

import com.narendra.paymentsystem.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository
        extends JpaRepository<Transaction, UUID> {
}
