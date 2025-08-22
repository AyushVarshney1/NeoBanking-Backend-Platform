package com.neobank.neobanktransactionservice.dto;

import com.neobank.neobanktransactionservice.dto.validator.TransferValidationGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequestDto {

    @NotNull(message = "Account Number is required")
    @Positive(message = "Account Number should be positive")
    private Long accountNumber;

    @NotNull(groups = TransferValidationGroup.class, message = "Beneficiary Account Number is required")
    @Positive(message = "Beneficiary Account Number should be positive")
    private Long beneficiaryAccountNumber;

    @NotNull(message = "Amount is required")
    private Double amount;

}