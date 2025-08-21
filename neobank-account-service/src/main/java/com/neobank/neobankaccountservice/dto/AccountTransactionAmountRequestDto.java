package com.neobank.neobankaccountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class AccountTransactionAmountRequestDto {

    @NotNull(message = "Account Number is required")
    @Positive(message = "Account Number should be positive")
    private Long accountNumber;

    @NotNull(message = "Amount required")
    @Positive(message = "Amount should be a positive value")
    private Double amount;

}
