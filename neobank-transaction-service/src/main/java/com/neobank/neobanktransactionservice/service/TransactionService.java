package com.neobank.neobanktransactionservice.service;

import com.neobank.neobanktransactionservice.dto.TransactionRequestDto;
import com.neobank.neobanktransactionservice.dto.TransactionResponseDto;
import com.neobank.neobanktransactionservice.grpc.AccountServiceGrpcClient;
import com.neobank.neobanktransactionservice.grpc.AuthServiceGrpcClient;
import com.neobank.neobanktransactionservice.mapper.TransactionMapper;
import com.neobank.neobanktransactionservice.model.Transaction;
import com.neobank.neobanktransactionservice.model.TransactionStatus;
import com.neobank.neobanktransactionservice.model.TransactionType;
import com.neobank.neobanktransactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthServiceGrpcClient authServiceGrpcClient;
    private final AccountServiceGrpcClient accountServiceGrpcClient;
    private final TransactionMapper transactionMapper;

    public TransactionResponseDto deposit(String token, TransactionRequestDto transactionRequestDto) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Transaction transaction = new Transaction();
        transaction.setAccountNumber(transactionRequestDto.getAccountNumber());
        transaction.setAmount(transactionRequestDto.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setTransactionType(TransactionType.DEPOSIT);

        Transaction savedTransaction = transactionRepository.save(transaction);

        Double newBalance = accountServiceGrpcClient.deposit(savedTransaction, userId,
                transaction.getAccountNumber(),transaction.getAmount());

        savedTransaction.setStatus(TransactionStatus.SUCCESS);
        savedTransaction.setClosingBalance(newBalance);

        transactionRepository.save(savedTransaction);

        return transactionMapper.toTransactionResponseDto(savedTransaction);
    }

    public TransactionResponseDto withdraw(String token, TransactionRequestDto transactionRequestDto) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Transaction transaction = new Transaction();
        transaction.setAccountNumber(transactionRequestDto.getAccountNumber());
        transaction.setAmount(transactionRequestDto.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setTransactionType(TransactionType.WITHDRAW);

        Transaction savedTransaction = transactionRepository.save(transaction);

        Double newBalance = accountServiceGrpcClient.withdraw(savedTransaction, userId,
                transaction.getAccountNumber(),transaction.getAmount());

        savedTransaction.setStatus(TransactionStatus.SUCCESS);
        savedTransaction.setClosingBalance(newBalance);

        transactionRepository.save(savedTransaction);

        return transactionMapper.toTransactionResponseDto(savedTransaction);
    }

    public TransactionResponseDto transfer(String token, TransactionRequestDto transactionRequestDto) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Transaction transaction = new Transaction();
        transaction.setAccountNumber(transactionRequestDto.getAccountNumber());
        transaction.setTargetAccountNumber(transactionRequestDto.getBeneficiaryAccountNumber());
        transaction.setAmount(transactionRequestDto.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setTransactionType(TransactionType.TRANSFER);

        Transaction savedTransaction = transactionRepository.save(transaction);

        Double newBalance = accountServiceGrpcClient.transfer(savedTransaction, userId, transactionRequestDto.getAccountNumber(),
                transactionRequestDto.getBeneficiaryAccountNumber(), transactionRequestDto.getAmount());

        savedTransaction.setStatus(TransactionStatus.SUCCESS);
        savedTransaction.setClosingBalance(newBalance);

        transactionRepository.save(savedTransaction);

        return transactionMapper.toTransactionResponseDto(savedTransaction);
    }
}
