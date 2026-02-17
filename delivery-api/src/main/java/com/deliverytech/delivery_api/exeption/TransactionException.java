package com.deliverytech.delivery_api.exeption;


public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}
