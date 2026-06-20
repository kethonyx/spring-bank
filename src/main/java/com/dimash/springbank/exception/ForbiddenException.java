package com.dimash.springbank.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message){
        super(message);
    }

}
