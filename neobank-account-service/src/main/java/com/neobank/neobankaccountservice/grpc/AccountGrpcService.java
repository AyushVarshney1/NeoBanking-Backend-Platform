package com.neobank.neobankaccountservice.grpc;

import account.*;
import com.neobank.neobankaccountservice.dto.AccountBalanceResponseDto;
import com.neobank.neobankaccountservice.exception.AccountNotFoundException;
import com.neobank.neobankaccountservice.exception.InsufficientFundsException;
import com.neobank.neobankaccountservice.exception.KycNotCompleteException;
import com.neobank.neobankaccountservice.service.AccountService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
@RequiredArgsConstructor
public class AccountGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(AccountGrpcService.class);
    private final AccountService accountService;

    @Override
    public void increaseBalance(BalanceUpdateRequest balanceUpdateRequest, StreamObserver<BalanceUpdateResponse> responseObserver){
        log.info("Balance Deposit Request Received with -> User ID: {} Account: {}, Amount: {}", balanceUpdateRequest.getUserId(), balanceUpdateRequest.getAccountNumber(), balanceUpdateRequest.getAmount());

        try{

            String userId = balanceUpdateRequest.getUserId();
            Long accountNumber = balanceUpdateRequest.getAccountNumber();
            double amount = balanceUpdateRequest.getAmount();

            AccountBalanceResponseDto accountBalanceResponseDto = accountService.deposit(userId, accountNumber, amount);

            BalanceUpdateResponse response = BalanceUpdateResponse.newBuilder()
                    .setSuccess(true)
                    .setNewBalance(accountBalanceResponseDto.getBalance())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }catch(AccountNotFoundException e){
            log.error("Account Not Found, Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch(KycNotCompleteException e){
            log.error("KYC of account not completed, complete KYC first. Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.PERMISSION_DENIED
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch (Exception e){
            log.error("Internal Server Error, Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }


    @Override
    public void decreaseBalance(BalanceUpdateRequest balanceUpdateRequest, StreamObserver<BalanceUpdateResponse> responseObserver){
        log.info("Balance Withdraw Request Received with -> User ID: {} Account: {}, Amount: {}", balanceUpdateRequest.getUserId(), balanceUpdateRequest.getAccountNumber(), balanceUpdateRequest.getAmount());

        try{
            String userId = balanceUpdateRequest.getUserId();
            Long accountNumber = balanceUpdateRequest.getAccountNumber();
            double amount = balanceUpdateRequest.getAmount();

            AccountBalanceResponseDto accountBalanceResponseDto = accountService.withdraw(userId, accountNumber, amount);

            BalanceUpdateResponse response = BalanceUpdateResponse.newBuilder()
                    .setSuccess(true)
                    .setNewBalance(accountBalanceResponseDto.getBalance())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }catch(AccountNotFoundException e){
            log.error("Account Not Found, Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch(KycNotCompleteException e){
            log.error("KYC of account not completed, complete KYC first. Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.PERMISSION_DENIED
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch(InsufficientFundsException e){
            log.error("Account does not have sufficient balance. Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.CANCELLED
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch (Exception e){
            log.error("Internal Server Error, Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void transfer(TransferRequest transferRequest, StreamObserver<TransferResponse> responseObserver){
        log.info("Transfer Request Received with -> User ID: {} Account: {}, BeneficiaryAccount: {}, Amount: {}", transferRequest.getUserId(),
                transferRequest.getAccountNumber(), transferRequest.getBeneficiaryAccountNumber(), transferRequest.getAmount());

        try{
            String userId = transferRequest.getUserId();
            Long accountNumber = transferRequest.getAccountNumber();
            Long beneficiaryAccountNumber = transferRequest.getBeneficiaryAccountNumber();
            double amount = transferRequest.getAmount();

            AccountBalanceResponseDto accountBalanceResponseDto = accountService.transfer(userId, accountNumber, beneficiaryAccountNumber, amount);

            TransferResponse response = TransferResponse.newBuilder()
                    .setSuccess(true)
                    .setFromNewBalance(accountBalanceResponseDto.getBalance())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        }catch(AccountNotFoundException e){
            log.error("Account Not Found, Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch(KycNotCompleteException e){
            log.error("KYC of account not completed, complete KYC first. Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.PERMISSION_DENIED
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch(InsufficientFundsException e){
            log.error("Account does not have sufficient balance. Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.CANCELLED
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );

        }catch (Exception e){
            log.error("Internal Server Error, Error message : {}", e.getMessage());
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .augmentDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}
