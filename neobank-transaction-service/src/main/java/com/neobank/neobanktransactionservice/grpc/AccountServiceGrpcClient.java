package com.neobank.neobanktransactionservice.grpc;

//import account.*;
import account.AccountServiceGrpc;
import account.BalanceUpdateRequest;
import account.BalanceUpdateResponse;
import account.TransferRequest;
import account.TransferResponse;
import com.neobank.neobanktransactionservice.dto.TransactionProducerDto;
import com.neobank.neobanktransactionservice.exception.InsufficientFundsException;
import com.neobank.neobanktransactionservice.exception.TransactionFailedException;
import com.neobank.neobanktransactionservice.exception.UnauthorizedException;
import com.neobank.neobanktransactionservice.exception.UserNotFoundException;
import com.neobank.neobanktransactionservice.kafka.TransactionEventProducer;
import com.neobank.neobanktransactionservice.model.Transaction;
import com.neobank.neobanktransactionservice.model.TransactionStatus;
import com.neobank.neobanktransactionservice.model.TransactionType;
import com.neobank.neobanktransactionservice.repository.TransactionRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceGrpcClient.class);
    private final AccountServiceGrpc.AccountServiceBlockingStub blockingStub;
    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer  transactionEventProducer;

    public AccountServiceGrpcClient(@Value("${account.service.address:localhost}") String serverAddress,
                                    @Value("${account.service.grpc.port:9001}") int serverPort, TransactionRepository transactionRepository, TransactionEventProducer transactionEventProducer) {

        this.transactionRepository = transactionRepository;
        this.transactionEventProducer = transactionEventProducer;

        log.info("Connecting to Account service GRPC service at {}:{}", serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();

        blockingStub = AccountServiceGrpc.newBlockingStub(channel);
    }

    public double deposit(Transaction transaction, String userId, Long accountNumber, Double amount) {
        BalanceUpdateRequest request = BalanceUpdateRequest.newBuilder()
                .setUserId(userId)
                .setAccountNumber(accountNumber)
                .setAmount(amount)
                .build();

        try {
            BalanceUpdateResponse response = blockingStub.increaseBalance(request);
            log.info("Increase Balance Response: {}", response);

            if (!response.getSuccess()) {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);

                TransactionProducerDto transactionProducerDto  = new TransactionProducerDto();
                transactionProducerDto.setAccountNumber(accountNumber);
                transactionProducerDto.setUserId(userId);
                transactionProducerDto.setAmount(amount);
                transactionProducerDto.setTransactionStatus(TransactionStatus.FAILED);
                transactionProducerDto.setTypeOfTransaction(TransactionType.DEPOSIT);
                transactionProducerDto.setTransactionId(transaction.getTransactionId().toString());

                transactionEventProducer.sendBalanceUpdateEvent(transactionProducerDto);

                throw new TransactionFailedException("Transaction Failed");
            }

            return response.getNewBalance();

        } catch (StatusRuntimeException e) {
            // Mark transaction as failed
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            TransactionProducerDto transactionProducerDto  = new TransactionProducerDto();
            transactionProducerDto.setAccountNumber(accountNumber);
            transactionProducerDto.setUserId(userId);
            transactionProducerDto.setAmount(amount);
            transactionProducerDto.setTransactionStatus(TransactionStatus.FAILED);
            transactionProducerDto.setTypeOfTransaction(TransactionType.DEPOSIT);
            transactionProducerDto.setTransactionId(transaction.getTransactionId().toString());

            transactionEventProducer.sendBalanceUpdateEvent(transactionProducerDto);

            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> {
                    log.warn("User or Account not found: {}", e.getStatus().getDescription());
                    throw new UserNotFoundException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "User or Account not found"
                    );
                }
                case PERMISSION_DENIED -> {
                    log.warn("KYC not completed or permission denied: {}", e.getStatus().getDescription());
                    throw new UnauthorizedException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "KYC not completed"
                    );
                }
                case INTERNAL -> {
                    log.error("Internal Server Error: {}", e.getStatus().getDescription());
                    throw new TransactionFailedException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "Internal Server Error"
                    );
                }
                default -> {
                    log.error("Unexpected gRPC error: {}", e.getStatus(), e);
                    throw new TransactionFailedException("Unexpected error: " + e.getStatus().getCode());
                }
            }
        }
    }

    public double withdraw(Transaction transaction, String userId, Long accountNumber, Double amount) {
        BalanceUpdateRequest request = BalanceUpdateRequest.newBuilder()
                .setUserId(userId)
                .setAccountNumber(accountNumber)
                .setAmount(amount)
                .build();

        try {
            BalanceUpdateResponse response = blockingStub.decreaseBalance(request);
            log.info("Decrease Balance Response: {}", response);

            if (!response.getSuccess()) {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);

                TransactionProducerDto transactionProducerDto  = new TransactionProducerDto();
                transactionProducerDto.setAccountNumber(accountNumber);
                transactionProducerDto.setUserId(userId);
                transactionProducerDto.setAmount(amount);
                transactionProducerDto.setTransactionStatus(TransactionStatus.FAILED);
                transactionProducerDto.setTypeOfTransaction(TransactionType.WITHDRAW);
                transactionProducerDto.setTransactionId(transaction.getTransactionId().toString());

                transactionEventProducer.sendBalanceUpdateEvent(transactionProducerDto);

                throw new TransactionFailedException("Transaction Failed");
            }

            return response.getNewBalance();

        } catch (StatusRuntimeException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            TransactionProducerDto transactionProducerDto  = new TransactionProducerDto();
            transactionProducerDto.setAccountNumber(accountNumber);
            transactionProducerDto.setUserId(userId);
            transactionProducerDto.setAmount(amount);
            transactionProducerDto.setTransactionStatus(TransactionStatus.FAILED);
            transactionProducerDto.setTypeOfTransaction(TransactionType.WITHDRAW);
            transactionProducerDto.setTransactionId(transaction.getTransactionId().toString());

            transactionEventProducer.sendBalanceUpdateEvent(transactionProducerDto);

            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> {
                    log.warn("User or Account not found: {}", e.getStatus().getDescription());
                    throw new UserNotFoundException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "User or Account not found"
                    );
                }
                case PERMISSION_DENIED -> {
                    log.warn("KYC not completed or permission denied: {}", e.getStatus().getDescription());
                    throw new UnauthorizedException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "KYC not completed"
                    );
                }
                case CANCELLED -> {
                    log.warn("Insufficient Funds in Account: {}", e.getStatus().getDescription());
                    throw new InsufficientFundsException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "Insufficient Funds in Account"
                    );
                }
                case INTERNAL -> {
                    log.error("Internal Server Error: {}", e.getStatus().getDescription());
                    throw new TransactionFailedException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "Internal Server Error"
                    );
                }
                default -> {
                    log.error("Unexpected gRPC error: {}", e.getStatus(), e);
                    throw new TransactionFailedException("Unexpected error: " + e.getStatus().getCode());
                }
            }
        }
    }

    public double transfer(Transaction transaction, String userId, Long accountNumber,
                           Long beneficiaryAccountNumber, Double amount) {

        TransferRequest request = TransferRequest.newBuilder()
                .setUserId(userId)
                .setAccountNumber(accountNumber)
                .setBeneficiaryAccountNumber(beneficiaryAccountNumber)
                .setAmount(amount)
                .build();

        try {
            TransferResponse response = blockingStub.transfer(request);
            log.info("Transfer Response: {}", response);

            if (!response.getSuccess()) {
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);

                TransactionProducerDto transactionProducerDto  = new TransactionProducerDto();
                transactionProducerDto.setAccountNumber(accountNumber);
                transactionProducerDto.setBeneficiaryNumber(transaction.getTargetAccountNumber());
                transactionProducerDto.setUserId(userId);
                transactionProducerDto.setAmount(amount);
                transactionProducerDto.setTransactionStatus(TransactionStatus.FAILED);
                transactionProducerDto.setTypeOfTransaction(TransactionType.TRANSFER);
                transactionProducerDto.setTransactionId(transaction.getTransactionId().toString());

                transactionEventProducer.sendAmountTransferEvent(transactionProducerDto);

                throw new TransactionFailedException("Transaction Failed");
            }

            return response.getFromNewBalance();

        } catch (StatusRuntimeException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            TransactionProducerDto transactionProducerDto  = new TransactionProducerDto();
            transactionProducerDto.setAccountNumber(accountNumber);
            transactionProducerDto.setBeneficiaryNumber(transaction.getTargetAccountNumber());
            transactionProducerDto.setUserId(userId);
            transactionProducerDto.setAmount(amount);
            transactionProducerDto.setTransactionStatus(TransactionStatus.FAILED);
            transactionProducerDto.setTypeOfTransaction(TransactionType.TRANSFER);
            transactionProducerDto.setTransactionId(transaction.getTransactionId().toString());

            transactionEventProducer.sendAmountTransferEvent(transactionProducerDto);

            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> {
                    log.warn("User or Account not found: {}", e.getStatus().getDescription());
                    throw new UserNotFoundException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "User or Account not found"
                    );
                }
                case PERMISSION_DENIED -> {
                    log.warn("KYC not completed or permission denied: {}", e.getStatus().getDescription());
                    throw new UnauthorizedException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "KYC not completed"
                    );
                }
                case CANCELLED -> {
                    log.warn("Insufficient Funds in Account: {}", e.getStatus().getDescription());
                    throw new InsufficientFundsException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "Insufficient Funds in Account"
                    );
                }
                case INTERNAL -> {
                    log.error("Internal Server Error: {}", e.getStatus().getDescription());
                    throw new TransactionFailedException(
                            e.getStatus().getDescription() != null ? e.getStatus().getDescription() : "Internal Server Error"
                    );
                }
                default -> {
                    log.error("Unexpected gRPC error: {}", e.getStatus(), e);
                    throw new TransactionFailedException("Unexpected error: " + e.getStatus().getCode());
                }
            }
        }
    }


}
