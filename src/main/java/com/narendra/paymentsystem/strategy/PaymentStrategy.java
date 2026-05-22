package com.narendra.paymentsystem.strategy;

import com.narendra.paymentsystem.entity.Payment;

public interface PaymentStrategy {
    void process(Payment payment);
    boolean validate(Payment payment);
}