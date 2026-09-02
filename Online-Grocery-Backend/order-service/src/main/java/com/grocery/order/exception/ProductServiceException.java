package com.grocery.order.exception;

public class ProductServiceException extends RuntimeException{

    public ProductServiceException(String message)
    {
        super(message);
    }
}
