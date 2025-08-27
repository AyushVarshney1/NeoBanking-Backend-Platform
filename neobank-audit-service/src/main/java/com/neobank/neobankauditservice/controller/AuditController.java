package com.neobank.neobankauditservice.controller;

import com.neobank.neobankauditservice.dto.AuditLogResponseDto;
import com.neobank.neobankauditservice.exception.InvalidTokenException;
import com.neobank.neobankauditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    // RETURNS ALL AUDIT BELONGING TO A USER
    @GetMapping("/users")
    public ResponseEntity<List<AuditLogResponseDto>> getUserAuditLogs(@RequestHeader("Authorization") String header) {
        if(header==null || !header.startsWith("Bearer ")){
            throw new InvalidTokenException("Authorization header is incorrect");
        }

        String token = header.substring(7);

        List<AuditLogResponseDto> auditLogResponseDtos = auditService.findAllAuditsByUserId(token);

        return ResponseEntity.ok(auditLogResponseDtos);
    }

    // RETURNS ALL AUDIT BELONGING TO AN ACCOUNT
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<AuditLogResponseDto>> getAccountAuditLogs(@RequestHeader("Authorization") String header, @PathVariable Long accountNumber) {
        if(header==null || !header.startsWith("Bearer ")){
            throw new InvalidTokenException("Authorization header is incorrect");
        }

        String token = header.substring(7);

        List<AuditLogResponseDto> auditLogResponseDtos = auditService.findAllAuditsByAccountNumber(token,accountNumber);

        return ResponseEntity.ok(auditLogResponseDtos);
    }

    // RETURNS ALL AUDITS (CAN ONLY BE ACCESSED BY ADMIN)
    @GetMapping
    public ResponseEntity<List<AuditLogResponseDto>> getAllAuditLogsForAdmin(@RequestHeader("Authorization") String header) {
        if(header==null || !header.startsWith("Bearer ")){
            throw new InvalidTokenException("Authorization header is incorrect");
        }

        String token = header.substring(7);

        List<AuditLogResponseDto> auditLogResponseDtos = auditService.findAllAudits(token);

        return ResponseEntity.ok(auditLogResponseDtos);
    }

    // RETURNS AUDIT BY AUDIT ID (CAN ONLY BE ACCESSED BY ADMIN)
    @GetMapping("/{auditId}")
    public ResponseEntity<AuditLogResponseDto> getAuditLogByIdForAdmin(@RequestHeader("Authorization") String header, @PathVariable Long auditId) {
        if(header==null || !header.startsWith("Bearer ")){
            throw new InvalidTokenException("Authorization header is incorrect");
        }

        String token = header.substring(7);

        AuditLogResponseDto auditLogResponseDto = auditService.findByAuditId(token,auditId);

        return ResponseEntity.ok(auditLogResponseDto);
    }

    // RETURNS ALL AUDIT BELONGING TO A USER BY USER ID GIVEN BY ADMIN (ENDPOINT FOR ADMIN TO FETCH ALL AUDITS OF A USER)
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AuditLogResponseDto>> getUserAuditLogsForAdmin(@RequestHeader("Authorization") String header, @PathVariable String userId) {
        if(header==null || !header.startsWith("Bearer ")){
            throw new InvalidTokenException("Authorization header is incorrect");
        }

        String token = header.substring(7);

        List<AuditLogResponseDto> auditLogResponseDtos = auditService.findAllAuditsByUserIdForAdmin(token,userId);

        return ResponseEntity.ok(auditLogResponseDtos);
    }

    // RETURNS THE AUDIT RELATED TO TRANSACTION ID BY ADMIN
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<AuditLogResponseDto> getAuditLogByTransactionIdForAdmin(@RequestHeader("Authorization") String header, @PathVariable String transactionId) {
        if(header==null || !header.startsWith("Bearer ")){
            throw new InvalidTokenException("Authorization header is incorrect");
        }

        String token = header.substring(7);

        AuditLogResponseDto auditLogResponseDto = auditService.findAuditByTransactionIdForAdmin(token,transactionId);

        return ResponseEntity.ok(auditLogResponseDto);
    }
}
