package com.neobank.neobanktransactionservice.controller;

import com.neobank.neobanktransactionservice.dto.TransactionRequestDto;
import com.neobank.neobanktransactionservice.dto.TransactionResponseDto;
import com.neobank.neobanktransactionservice.dto.validator.TransferValidationGroup;
import com.neobank.neobanktransactionservice.exception.InvalidTokenException;
import com.neobank.neobanktransactionservice.service.TransactionService;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;


    // DEPOSIT MONEY
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponseDto> deposit(@RequestHeader("Authorization") String token, @Validated({Default.class}) @RequestBody TransactionRequestDto transactionRequestDto) {
        if(token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid Token");
        }

        TransactionResponseDto transactionResponseDto = transactionService.deposit(token.substring(7),transactionRequestDto);
        return ResponseEntity.ok(transactionResponseDto);
    }

    // WITHDRAW MONEY
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDto> withdraw(@RequestHeader("Authorization") String token, @Validated({Default.class}) @RequestBody TransactionRequestDto transactionRequestDto) {
        if(token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid Token");
        }

        TransactionResponseDto transactionResponseDto = transactionService.withdraw(token.substring(7),transactionRequestDto);
        return ResponseEntity.ok(transactionResponseDto);
    }


    // TRANSFER MONEY TO ANOTHER ACCOUNT
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDto> transfer(@RequestHeader("Authorization") String token, @Validated({Default.class, TransferValidationGroup.class}) @RequestBody TransactionRequestDto transactionRequestDto) {
        if(token == null || !token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid Token");
        }

        TransactionResponseDto transactionResponseDto = transactionService.transfer(token.substring(7),transactionRequestDto);
        return ResponseEntity.ok(transactionResponseDto);
    }


}
