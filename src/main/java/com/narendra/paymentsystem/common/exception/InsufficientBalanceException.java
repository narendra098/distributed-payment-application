package com.narendra.paymentsystem.common.exception;

/**
  Thrown when wallet balance is insufficient
  for debit operation.
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}