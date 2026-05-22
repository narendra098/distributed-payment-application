package com.narendra.paymentsystem.factory;

import com.narendra.paymentsystem.enums.PaymentType;
import com.narendra.paymentsystem.strategy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyFactory {

    private final UpiPaymentStrategy upiPaymentStrategy;
    private final NeftPaymentStrategy neftPaymentStrategy;
    private final ImpsPaymentStrategy impsPaymentStrategy;
    private final CardPaymentStrategy cardPaymentStrategy;

    public PaymentStrategy getStrategy(PaymentType paymentType) {
        return switch (paymentType) {
            case UPI -> upiPaymentStrategy;
            case NEFT -> neftPaymentStrategy;
            case IMPS -> impsPaymentStrategy;
            case CARD -> cardPaymentStrategy;
            default -> throw new IllegalArgumentException("Unknown payment type: " + paymentType);
        };
    }
}