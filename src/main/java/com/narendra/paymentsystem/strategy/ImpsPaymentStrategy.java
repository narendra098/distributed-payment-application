package com.narendra.paymentsystem.strategy;

import com.narendra.paymentsystem.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
public class ImpsPaymentStrategy implements PaymentStrategy {

    private static final BigDecimal IMPS_LIMIT = new BigDecimal("200000");

    @Override
    public void process(Payment payment) {
        log.info("Processing IMPS payment: {}", payment.getId());
        // IMPS specific processing logic
    }

    @Override
    public boolean validate(Payment payment) {
        return payment.getAmount().compareTo(IMPS_LIMIT) <= 0;
    }
}