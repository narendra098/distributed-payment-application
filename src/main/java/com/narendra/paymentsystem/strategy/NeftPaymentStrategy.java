package com.narendra.paymentsystem.strategy;

import com.narendra.paymentsystem.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NeftPaymentStrategy implements PaymentStrategy {

    @Override
    public void process(Payment payment) {
        log.info("Processing NEFT payment: {}", payment.getId());
        // NEFT specific processing logic
    }

    @Override
    public boolean validate(Payment payment) {
        return payment.getAmount().compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}