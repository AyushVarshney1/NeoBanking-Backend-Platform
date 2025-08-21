package com.neobank.authservice.dto;

import com.neobank.authservice.dto.validator.SignUpValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be more than 6 characters")
    private String password;

    @NotBlank(groups = SignUpValidationGroup.class, message = "Role is required")
    private String role;
}
