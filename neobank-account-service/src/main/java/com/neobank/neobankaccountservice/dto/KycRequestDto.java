package com.neobank.neobankaccountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KycRequestDto {

    @NotBlank(message = "Account Number is required")
    private Long accountNumber;

    @NotBlank(message = "Pan Number is required")
    @Size(min = 10, max = 10)
    private String panNumber;

    @NotBlank(message = "Aadhaar Number is required")
    @Size(min = 12, max = 12)
    private String aadhaarNumber;

    @NotBlank(message = "Address is required")
    private String address;
}
