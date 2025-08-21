package com.neobank.neobankaccountservice.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycRequestDto {

    @NotNull(message = "Account Number is required")
    @Positive(message = "Account Number should be positive")
    private Long accountNumber;

    @NotNull(message = "Pan Number is required")
    @Size(min = 10, max = 10)
    private String panNumber;

    @NotBlank(message = "Aadhaar Number is required")
    @Size(min = 12, max = 12)
    private String aadhaarNumber;

    @NotBlank(message = "Address is required")
    private String address;
}
