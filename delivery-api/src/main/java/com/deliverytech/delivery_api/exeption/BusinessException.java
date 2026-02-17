package com.deliverytech.delivery_api.exeption;


public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}