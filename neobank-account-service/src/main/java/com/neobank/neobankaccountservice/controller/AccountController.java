package com.neobank.neobankaccountservice.controller;

import com.neobank.neobankaccountservice.dto.*;
import com.neobank.neobankaccountservice.exception.InvalidTokenException;
import com.neobank.neobankaccountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;


    // GET AN ACCOUNT
    @GetMapping("/{accountnumber}")
    public ResponseEntity<AccountResponseDto> getAccount(@RequestHeader("Authorization") String token,
            @PathVariable("accountnumber") Long accountnumber) {

        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        AccountResponseDto accountResponseDto = accountService.getAccount(token.substring(7), accountnumber);

        return ResponseEntity.ok(accountResponseDto);
    }


    // GET ALL ACCOUNTS
    @GetMapping("/get-all-accounts")
    public ResponseEntity<List<AccountResponseDto>> getAllAccount(@RequestHeader("Authorization") String token) {
        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        List<AccountResponseDto> accountResponseDtoList = accountService.getAllAccounts(token.substring(7));

        return ResponseEntity.ok(accountResponseDtoList);
    }


    // CREATE ACCOUNT
    @PostMapping("/create-account")
    public ResponseEntity<AccountResponseDto> createAccount(@RequestHeader("Authorization") String token) {

        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        AccountResponseDto accountResponseDto = accountService.createAccount(token.substring(7));

        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
    }


    // GET ACCOUNT BALANCE
    @GetMapping("/{accountnumber}/balance")
    public ResponseEntity<AccountBalanceResponseDto>  getAccountBalance(
            @RequestHeader("Authorization") String token,
            @PathVariable("accountnumber") Long accountNumber) {


        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        AccountBalanceResponseDto accountBalanceResponseDto = accountService.getBalance(token.substring(7), accountNumber);
        return ResponseEntity.ok(accountBalanceResponseDto);
    }


    // DEACTIVATE ACCOUNT
    @PostMapping("/{accountnumber}/deactivate-account")
    public ResponseEntity<String> deactivateAccount(
            @RequestHeader("Authorization") String token,
            @PathVariable("accountnumber") Long accountNumber){

        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        accountService.deactivateAccount(token.substring(7),accountNumber);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Account has been deactivated.");
    }


    // ACTIVATE ACCOUNT
    @PostMapping("/{accountnumber}/activate-account")
    public ResponseEntity<String> activateAccount(
            @RequestHeader("Authorization") String token,
            @PathVariable("accountnumber") Long accountNumber){

        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        accountService.activateAccount(token.substring(7),accountNumber);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Account has been activated.");
    }


    // COMPLETE KYC FOR ACCOUNT
    @PostMapping("/kyc/complete-kyc")
    public ResponseEntity<KycResponseDto> completeKyc(@RequestHeader("Authorization") String token, @Valid @RequestBody KycRequestDto kycRequestDto){
        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        KycResponseDto kycResponseDto = accountService.completeKyc(token.substring(7),kycRequestDto);
        return ResponseEntity.ok(kycResponseDto);
    }


    // GET KYC OF AN ACCOUNT
    @GetMapping("/kyc/{accountnumber}")
    public ResponseEntity<KycResponseDto> getKyc(@RequestHeader("Authorization") String token,  @PathVariable("accountnumber") Long accountNumber){
        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        KycResponseDto kycResponseDto = accountService.getKyc(token.substring(7), accountNumber);
        return ResponseEntity.ok(kycResponseDto);
    }


    // DEPOSIT AMOUNT
    @PostMapping("/deposit")
    public ResponseEntity<AccountBalanceResponseDto> depositAmount(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AccountTransactionAmountRequestDto accountTransactionAmountRequestDto){
        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        AccountBalanceResponseDto accountBalanceResponseDto = accountService.deposit(token.substring(7),accountTransactionAmountRequestDto);
        return ResponseEntity.ok(accountBalanceResponseDto);
    }


    // WITHDRAW AMOUNT
    @PostMapping("/withdraw")
    public ResponseEntity<AccountBalanceResponseDto> withdrawAmount(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AccountTransactionAmountRequestDto accountTransactionAmountRequestDto){
        if(token == null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid Token");
        }

        AccountBalanceResponseDto accountBalanceResponseDto = accountService.withdraw(token.substring(7),accountTransactionAmountRequestDto);
        return ResponseEntity.ok(accountBalanceResponseDto);
    }


}
