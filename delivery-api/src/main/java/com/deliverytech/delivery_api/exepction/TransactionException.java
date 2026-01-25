package com.deliverytech.delivery_api.exepction;


public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }
}
