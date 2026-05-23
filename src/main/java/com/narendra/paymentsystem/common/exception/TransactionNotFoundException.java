package com.narendra.paymentsystem.common.exception;

/**
  Thrown when transaction is not found.
 */
public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(String message) {
        super(message);
    }
}