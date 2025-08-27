package com.neobank.neobankauditservice.mapper;

import com.neobank.neobankauditservice.dto.AuditLogResponseDto;
import com.neobank.neobankauditservice.model.AuditLog;
import com.neobank.neobankauditservice.model.enums.TransactionType;
import org.springframework.stereotype.Service;

@Service
public class AuditMapper {

    public AuditLogResponseDto toAuditLogResponseDto(AuditLog auditLog) {
        AuditLogResponseDto auditLogResponseDto = new AuditLogResponseDto();
        auditLogResponseDto.setUserId(auditLog.getUserId());
        auditLogResponseDto.setTransactionId(auditLog.getTransactionId());
        auditLogResponseDto.setAccountNumber(auditLog.getAccountNumber());
        auditLogResponseDto.setType(auditLog.getType());
        auditLogResponseDto.setStatus(auditLog.getStatus());
        auditLogResponseDto.setAmount(auditLog.getAmount());
        if(auditLog.getType().equals(TransactionType.TRANSFER)){
            auditLogResponseDto.setBeneficiaryAccountNumber(auditLog.getBeneficiaryAccountNumber());
        }

        return auditLogResponseDto;
    }
}
