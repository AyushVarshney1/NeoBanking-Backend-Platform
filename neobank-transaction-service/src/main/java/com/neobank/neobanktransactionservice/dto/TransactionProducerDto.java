package com.neobank.neobanktransactionservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionProducerDto {

    String userId;
    String transactionId;
    Long accountNumber;
    // ONLY REQUIRED IN TRANSFER EVENT
    Long beneficiaryNumber;
    String email;
    Double amount;
    Double balance;
    // ONLY REQUIRED IN BALANCE UPDATE EVENT
    String typeOfTransaction;
}
