package com.neobank.neobankaccountservice.mapper;

import com.neobank.neobankaccountservice.dto.AccountBalanceResponseDto;
import com.neobank.neobankaccountservice.dto.AccountResponseDto;
import com.neobank.neobankaccountservice.dto.KycRequestDto;
import com.neobank.neobankaccountservice.dto.KycResponseDto;
import com.neobank.neobankaccountservice.model.Account;
import com.neobank.neobankaccountservice.model.Kyc;
import org.springframework.stereotype.Service;

@Service
public class AccountMapper {

    public AccountResponseDto toAccountResponseDto(Account account) {
        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setAccountNumber(account.getAccountNumber());
        accountResponseDto.setBalance(account.getBalance());
        accountResponseDto.setStatus(account.getStatus());
        accountResponseDto.setCreatedAt(account.getCreatedDate());
        accountResponseDto.setUpdatedAt(account.getUpdatedDate());

        return accountResponseDto;
    }

    public AccountBalanceResponseDto toAccountBalanceResponseDto(Account account) {
        AccountBalanceResponseDto accountBalanceResponseDto = new AccountBalanceResponseDto();
        accountBalanceResponseDto.setBalance(account.getBalance());

        return accountBalanceResponseDto;
    }

    public Kyc toKycModel(KycRequestDto kycRequestDto) {
        Kyc kyc = new Kyc();
        kyc.setPanNumber(kycRequestDto.getPanNumber());
        kyc.setAddress(kycRequestDto.getAddress());
        kyc.setAadhaarNumber(kycRequestDto.getAadhaarNumber());

        return kyc;
    }

    public KycResponseDto  toKycResponseDto(Kyc kyc) {
        KycResponseDto kycResponseDto = new KycResponseDto();
        kycResponseDto.setPanNumber(kyc.getPanNumber());
        kycResponseDto.setAddress(kyc.getAddress());
        kycResponseDto.setAadhaarNumber(kyc.getAadhaarNumber());
        kycResponseDto.setAccountNumber(String.valueOf(kyc.getAccount().getAccountNumber()));

        return kycResponseDto;
    }
}
