package com.neobank.neobanktransactionservice.service;

import com.neobank.neobanktransactionservice.dto.AuthGrpcResponseDto;
import com.neobank.neobanktransactionservice.dto.TransactionProducerDto;
import com.neobank.neobanktransactionservice.dto.TransactionRequestDto;
import com.neobank.neobanktransactionservice.dto.TransactionResponseDto;
import com.neobank.neobanktransactionservice.grpc.AccountServiceGrpcClient;
import com.neobank.neobanktransactionservice.grpc.AuthServiceGrpcClient;
import com.neobank.neobanktransactionservice.kafka.TransactionEventProducer;
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
    private final TransactionEventProducer transactionEventProducer;

    public TransactionResponseDto deposit(String token, TransactionRequestDto transactionRequestDto) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndEmail(token);
        String userId = authGrpcResponseDto.getUserId();
        String email = authGrpcResponseDto.getEmail();

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

        TransactionProducerDto transactionProducerDto = new TransactionProducerDto();
        transactionProducerDto.setUserId(userId);
        transactionProducerDto.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
        transactionProducerDto.setAccountNumber(savedTransaction.getAccountNumber());
        transactionProducerDto.setAmount(savedTransaction.getAmount());
        transactionProducerDto.setBalance(savedTransaction.getClosingBalance());
        transactionProducerDto.setTypeOfTransaction(TransactionType.DEPOSIT);
        transactionProducerDto.setEmail(email);
        transactionProducerDto.setTransactionStatus(TransactionStatus.SUCCESS);

        // KAFKA EVENT PRODUCER
        transactionEventProducer.sendBalanceUpdateEvent(transactionProducerDto);

        return transactionMapper.toTransactionResponseDto(savedTransaction);
    }

    public TransactionResponseDto withdraw(String token, TransactionRequestDto transactionRequestDto) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndEmail(token);
        String userId = authGrpcResponseDto.getUserId();
        String email = authGrpcResponseDto.getEmail();

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

        TransactionProducerDto transactionProducerDto = new TransactionProducerDto();
        transactionProducerDto.setUserId(userId);
        transactionProducerDto.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
        transactionProducerDto.setAccountNumber(savedTransaction.getAccountNumber());
        transactionProducerDto.setAmount(savedTransaction.getAmount());
        transactionProducerDto.setBalance(savedTransaction.getClosingBalance());
        transactionProducerDto.setTypeOfTransaction(TransactionType.WITHDRAW);
        transactionProducerDto.setEmail(email);
        transactionProducerDto.setTransactionStatus(TransactionStatus.SUCCESS);

        // KAFKA EVENT PRODUCER
        transactionEventProducer.sendBalanceUpdateEvent(transactionProducerDto);

        return transactionMapper.toTransactionResponseDto(savedTransaction);
    }

    public TransactionResponseDto transfer(String token, TransactionRequestDto transactionRequestDto) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndEmail(token);
        String userId = authGrpcResponseDto.getUserId();
        String email = authGrpcResponseDto.getEmail();

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

        TransactionProducerDto transactionProducerDto = new TransactionProducerDto();
        transactionProducerDto.setUserId(userId);
        transactionProducerDto.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
        transactionProducerDto.setAccountNumber(savedTransaction.getAccountNumber());
        transactionProducerDto.setBeneficiaryNumber(transactionRequestDto.getBeneficiaryAccountNumber());
        transactionProducerDto.setAmount(savedTransaction.getAmount());
        transactionProducerDto.setBalance(savedTransaction.getClosingBalance());
        transactionProducerDto.setEmail(email);
        transactionProducerDto.setTransactionStatus(TransactionStatus.SUCCESS);

        // KAFKA EVENT PRODUCER
        transactionEventProducer.sendAmountTransferEvent(transactionProducerDto);

        return transactionMapper.toTransactionResponseDto(savedTransaction);
    }
}
