package com.neobank.neobankaccountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class AccountTransactionAmountRequestDto {

    @NotBlank(message = "Account Number is required")
    private Long accountNumber;

    @NotBlank(message = "Amount required")
    private Double amount;

}
