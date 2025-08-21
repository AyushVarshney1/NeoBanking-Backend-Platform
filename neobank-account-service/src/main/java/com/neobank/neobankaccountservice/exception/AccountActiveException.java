package com.neobank.neobankaccountservice.exception;

public class AccountActiveException extends RuntimeException{
    public AccountActiveException(String message){
        super(message);
    }
}
