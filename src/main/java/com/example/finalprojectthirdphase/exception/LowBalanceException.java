package com.example.finalprojectthirdphase.exception;

public class LowBalanceException extends RuntimeException {
    public LowBalanceException(String message) {
        super(message);
    }
}
