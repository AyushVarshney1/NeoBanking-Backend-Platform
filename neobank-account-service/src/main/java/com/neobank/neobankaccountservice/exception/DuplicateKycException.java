package com.neobank.neobankaccountservice.exception;

public class DuplicateKycException extends RuntimeException {
    public DuplicateKycException(String message) {
        super(message);
    }
}
