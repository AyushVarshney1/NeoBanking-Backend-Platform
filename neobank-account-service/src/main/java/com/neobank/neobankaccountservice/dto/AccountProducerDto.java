package com.neobank.neobankaccountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountProducerDto {

    String email;
    Long accountNumber;

    // BELOW PROPERTIES REQUIRED ONLY FOR ACCOUNT STATUS UPDATE EVENT
    String status;
    Double balance;
}
