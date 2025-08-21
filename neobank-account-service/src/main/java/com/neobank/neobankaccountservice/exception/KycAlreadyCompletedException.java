package com.neobank.neobankaccountservice.exception;

public class KycAlreadyCompletedException extends RuntimeException{
    public KycAlreadyCompletedException(String message){
        super(message);
    }
}
