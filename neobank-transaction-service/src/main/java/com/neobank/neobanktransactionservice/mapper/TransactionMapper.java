package com.neobank.neobanktransactionservice.mapper;

import com.neobank.neobanktransactionservice.dto.TransactionResponseDto;
import com.neobank.neobanktransactionservice.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public class TransactionMapper {

    public TransactionResponseDto toTransactionResponseDto(Transaction transaction) {
        TransactionResponseDto transactionResponseDto = new TransactionResponseDto();
        transactionResponseDto.setBalance(transaction.getClosingBalance());

        return transactionResponseDto;
    }

}
