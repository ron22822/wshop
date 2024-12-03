package com.example.wshop.exception;

public class InvalidRequestDataException extends RuntimeException {
    public InvalidRequestDataException(String message) {
        super(message);
    }
}
