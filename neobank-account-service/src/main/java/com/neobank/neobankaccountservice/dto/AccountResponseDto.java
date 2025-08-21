package com.neobank.neobankaccountservice.dto;

import com.neobank.neobankaccountservice.model.enums.AccountStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AccountResponseDto {

    private Long accountNumber;
    private Double balance;
    private AccountStatus status;
    private Date createdAt;
    private Date updatedAt;
}
