package com.narendra.paymentsystem.strategy;

import com.narendra.paymentsystem.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
public class UpiPaymentStrategy implements PaymentStrategy {

    private static final BigDecimal UPI_LIMIT = new BigDecimal("100000");

    @Override
    public void process(Payment payment) {
        log.info("Processing UPI payment: {}", payment.getId());
        // UPI specific processing logic
    }

    @Override
    public boolean validate(Payment payment) {
        return payment.getAmount().compareTo(UPI_LIMIT) <= 0;
    }
}