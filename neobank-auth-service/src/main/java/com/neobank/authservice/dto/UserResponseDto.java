package com.neobank.authservice.dto;

import com.neobank.authservice.model.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponseDto {
    private UUID id;
    private String email;
    private Role role;
}
