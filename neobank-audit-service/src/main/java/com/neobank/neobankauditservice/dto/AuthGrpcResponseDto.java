package com.neobank.neobankauditservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthGrpcResponseDto {

    String userId;
    String role;
}
