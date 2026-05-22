package com.narendra.paymentsystem.repository;

import com.narendra.paymentsystem.entity.Payment;
import com.narendra.paymentsystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findBySenderId(String senderId);
    List<Payment> findByStatus(PaymentStatus status);
}