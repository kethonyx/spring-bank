package com.dimash.springbank.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String message){
        super(message);
    }
}
