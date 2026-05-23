package com.narendra.paymentsystem.common.exception;

/**
     Thrown when wallet is not found.
 */
public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(String message) {
        super(message);
    }
}