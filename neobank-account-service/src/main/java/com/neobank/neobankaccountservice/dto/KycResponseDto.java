package com.neobank.neobankaccountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycResponseDto {

    private String accountNumber;
    private String panNumber;
    private String aadhaarNumber;
    private String address;

}
