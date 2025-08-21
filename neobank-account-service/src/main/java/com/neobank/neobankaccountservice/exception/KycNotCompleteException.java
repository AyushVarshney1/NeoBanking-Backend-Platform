package com.neobank.neobankaccountservice.exception;

public class KycNotCompleteException extends RuntimeException {
    public KycNotCompleteException(String message) {
        super(message);
    }
}
