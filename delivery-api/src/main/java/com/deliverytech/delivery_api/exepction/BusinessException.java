package com.deliverytech.delivery_api.exepction;


public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}