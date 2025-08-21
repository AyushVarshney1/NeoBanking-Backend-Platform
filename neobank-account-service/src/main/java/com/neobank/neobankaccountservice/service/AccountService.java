package com.neobank.neobankaccountservice.service;

import com.neobank.neobankaccountservice.dto.*;
import com.neobank.neobankaccountservice.exception.*;
import com.neobank.neobankaccountservice.grpc.AuthServiceGrpcClient;
import com.neobank.neobankaccountservice.mapper.AccountMapper;
import com.neobank.neobankaccountservice.model.Account;
import com.neobank.neobankaccountservice.model.Kyc;
import com.neobank.neobankaccountservice.model.enums.AccountStatus;
import com.neobank.neobankaccountservice.repository.AccountRepository;
import com.neobank.neobankaccountservice.repository.KycRepository;
import com.neobank.neobankaccountservice.util.GenerateRandomAccountNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AuthServiceGrpcClient authServiceGrpcClient;
    private final AccountMapper accountMapper;
    private final KycRepository kycRepository;
    private final GenerateRandomAccountNumber generateRandomAccountNumber;

    public AccountResponseDto createAccount(String token) {

        System.out.println("Creating account with token: " + token);

        String userId = authServiceGrpcClient.extractUserId(token);

        Long accountNumber;

        do{
            accountNumber = generateRandomAccountNumber.generateRandomAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUserId(UUID.fromString(userId));
        account.setStatus(AccountStatus.ACTIVE);

        accountRepository.save(account);
        return accountMapper.toAccountResponseDto(account);
    }

    public AccountResponseDto getAccount(String token, Long accountNumber) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber,UUID.fromString(userId));

        if(account == null){
            throw new AccountNotFoundException("Account not found with Account Number: " + accountNumber + " and User ID : " + userId);
        }

        return accountMapper.toAccountResponseDto(account);
    }

    public List<AccountResponseDto> getAllAccounts(String token) {
        String userId = authServiceGrpcClient.extractUserId(token);

        List<Account> accounts = accountRepository.findAllByUserId(UUID.fromString(userId));

        return accounts.stream().map(accountMapper::toAccountResponseDto).toList();
    }

    public AccountBalanceResponseDto getBalance(String token, Long accountNumber) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber,UUID.fromString(userId));

        if(account == null){
            throw new AccountNotFoundException("Account not found with Account Number: " + accountNumber + " and User ID : " + userId);
        }

        if(account.getStatus().equals(AccountStatus.INACTIVE)){
            throw new AccountInactiveException("Account with Account Number: " + accountNumber + " is inactive.");
        }

        return accountMapper.toAccountBalanceResponseDto(account);
    }

    public void deactivateAccount(String token, Long accountNumber) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Account account =  accountRepository.findByAccountNumberAndUserId(accountNumber,UUID.fromString(userId));

        if(account == null){
            throw new AccountNotFoundException("Account not found with Account Number: " + accountNumber + " and User ID : " + userId);
        }

        if(account.getStatus().equals(AccountStatus.INACTIVE)){
            throw new AccountInactiveException("Account with Account Number: " + accountNumber + " is already inactive.");
        }

        account.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    public void activateAccount(String token, Long accountNumber) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber,UUID.fromString(userId));

        if(account == null){
            throw new AccountNotFoundException("Account not found with Account Number: " + accountNumber + " and User ID : " + userId);
        }

        if(account.getStatus().equals(AccountStatus.ACTIVE)){
            throw new AccountInactiveException("Account with Account Number: " + accountNumber + " is already active.");
        }

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    public KycResponseDto completeKyc(String token, KycRequestDto kycRequestDto) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Account account = accountRepository.findByAccountNumberAndUserId(kycRequestDto.getAccountNumber(), UUID.fromString(userId));

        if(account == null){
            throw new AccountNotFoundException("Account not found with Account Number: " + kycRequestDto.getAccountNumber());
        }

        if(account.getStatus().equals(AccountStatus.INACTIVE)){
            throw new AccountInactiveException("Account with Account Number: " + account.getAccountNumber() + " is inactive.");
        }

        boolean kycDone = kycRepository.existsByAccount(account);

        if(kycDone){
            throw new KycAlreadyCompletedException("Kyc for Account with Account Number : " + account.getAccountNumber() + " already completed");
        }

        Kyc kyc = accountMapper.toKycModel(kycRequestDto);
        kyc.setAccount(account);

        kycRepository.save(kyc);

        return accountMapper.toKycResponseDto(kyc);

    }

    public KycResponseDto getKyc(String token, Long accountNumber) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber,UUID.fromString(userId));

        if(account  == null){
            throw new AccountNotFoundException("Account with Account Number: " + accountNumber + " does not exist.");
        }

        if(account.getStatus().equals(AccountStatus.INACTIVE)){
            throw new AccountInactiveException("Account with Account Number: " + account.getAccountNumber() + " is inactive.");
        }

        Kyc kyc = kycRepository.findByAccount(account);

        if(kyc == null){
            throw new KycNotCompleteException("Kyc for Account with Account Number: " + accountNumber + " is incomplete.");
        }

        return accountMapper.toKycResponseDto(kyc);
    }

    public AccountBalanceResponseDto deposit(String token, AccountTransactionAmountRequestDto accountTransactionAmountRequestDto) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Long accountNumber = accountTransactionAmountRequestDto.getAccountNumber();

        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber,UUID.fromString(userId));

        if(account  == null){
            throw new AccountNotFoundException("Account with Account Number: " + accountNumber + " does not exist.");
        }

        Kyc kyc = kycRepository.findByAccount(account);

        if(kyc == null){
            throw new KycNotCompleteException("Kyc for Account with Account Number: " + accountNumber + " is incomplete. Complete KYC to deposit amount");
        }

        Double currentBalance = account.getBalance();
        Double newBalance = currentBalance + accountTransactionAmountRequestDto.getAmount();

        account.setBalance(newBalance);

        accountRepository.save(account);
        return accountMapper.toAccountBalanceResponseDto(account);
    }

    public AccountBalanceResponseDto withdraw(String token, AccountTransactionAmountRequestDto accountTransactionAmountRequestDto) {
        String userId = authServiceGrpcClient.extractUserId(token);

        Long accountNumber = accountTransactionAmountRequestDto.getAccountNumber();

        Account account = accountRepository.findByAccountNumberAndUserId(accountNumber,UUID.fromString(userId));

        if(account  == null){
            throw new AccountNotFoundException("Account with Account Number: " + accountNumber + " does not exist.");
        }

        Kyc kyc = kycRepository.findByAccount(account);

        if(kyc == null){
            throw new KycNotCompleteException("Kyc for Account with Account Number: " + accountNumber + " is incomplete. Complete KYC to deposit amount");
        }

        Double currentBalance = account.getBalance();
        Double withdrawAmount = accountTransactionAmountRequestDto.getAmount();
        if(currentBalance < withdrawAmount){
            throw new InsufficientFundsException("Insufficient funds to withdraw amount : " + withdrawAmount + ", Current Balance : " + currentBalance);
        }
        Double newBalance = currentBalance - withdrawAmount;

        account.setBalance(newBalance);

        accountRepository.save(account);
        return accountMapper.toAccountBalanceResponseDto(account);
    }
}
