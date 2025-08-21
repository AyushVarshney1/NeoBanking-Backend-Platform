package com.neobank.neobankaccountservice.exception;

public class AccountInactiveException extends RuntimeException{
    public AccountInactiveException(String message){
        super(message);
    }
}
