package com.narendra.paymentsystem.dto;

import com.narendra.paymentsystem.enums.PaymentType;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {
    private String senderId;
    private String receiverId;
    private BigDecimal amount;
    private PaymentType paymentType;
}