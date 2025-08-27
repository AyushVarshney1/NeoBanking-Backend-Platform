package com.neobank.neobankauditservice.dto;

import com.neobank.neobankauditservice.model.enums.TransactionStatus;
import com.neobank.neobankauditservice.model.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogResponseDto {

    private String transactionId;
    private Long accountNumber;
    private String userId;

    private Long beneficiaryAccountNumber;

    private TransactionType type;
    private Double amount;
    private TransactionStatus status;
}
