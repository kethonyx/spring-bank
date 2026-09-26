package com.dimash.springbank.exception;

public class InvalidTransferException extends RuntimeException {

    public InvalidTransferException(String message){
        super(message);
    }
}
