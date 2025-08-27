package com.neobank.neobankauditservice.service;

import com.neobank.neobankauditservice.dto.AuditLogResponseDto;
import com.neobank.neobankauditservice.dto.AuditStatsResponseDto;
import com.neobank.neobankauditservice.dto.AuthGrpcResponseDto;
import com.neobank.neobankauditservice.exception.AuditLogNotFoundException;
import com.neobank.neobankauditservice.exception.UnauthorizedException;
import com.neobank.neobankauditservice.grpc.AuthServiceGrpcClient;
import com.neobank.neobankauditservice.mapper.AuditMapper;
import com.neobank.neobankauditservice.model.AuditLog;
import com.neobank.neobankauditservice.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;
    private final AuthServiceGrpcClient authServiceGrpcClient;
    private final AuditMapper auditMapper;

    public void saveAuditLog(AuditLog auditLog){
        auditRepository.save(auditLog);
    }

    public List<AuditLogResponseDto> getAllAuditsByUserId(String token) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndRole(token);

        String userId = authGrpcResponseDto.getUserId();

        List<AuditLog> auditLogs = auditRepository.findAllByUserId(userId);

        List<AuditLogResponseDto> auditLogResponseDtoList = auditLogs.stream().map(auditMapper :: toAuditLogResponseDto).toList();
        return auditLogResponseDtoList;
    }

    public List<AuditLogResponseDto> getAllAuditsByAccountNumber(String token, Long accountNumber) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndRole(token);

        String userId = authGrpcResponseDto.getUserId();

        List<AuditLog> auditLogs = auditRepository.findAllByUserIdAndAccountNumber(userId, accountNumber);

        List<AuditLogResponseDto> auditLogResponseDtoList = auditLogs.stream().map(auditMapper :: toAuditLogResponseDto).toList();
        return auditLogResponseDtoList;
    }

    public List<AuditLogResponseDto> getAllAudits(String token) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndRole(token);

        String userRole =  authGrpcResponseDto.getRole();

        if(!userRole.equals("ADMIN")){
            throw new UnauthorizedException("Only Admins are allowed to get all audit logs");
        }

        List<AuditLog> auditLogs = auditRepository.findAll();
        List<AuditLogResponseDto> auditLogResponseDtoList = auditLogs.stream().map(auditMapper :: toAuditLogResponseDto).toList();
        return auditLogResponseDtoList;
    }

    public AuditLogResponseDto getByAuditId(String token, Long auditId) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndRole(token);

        String userRole =  authGrpcResponseDto.getRole();

        if(!userRole.equals("ADMIN")){
            throw new UnauthorizedException("Only Admins are allowed to get audit log by audit id");
        }

        AuditLog auditLog = auditRepository.findById(auditId).orElseThrow(() -> new AuditLogNotFoundException("AuditLog with id: " + auditId + " not found"));

        return auditMapper.toAuditLogResponseDto(auditLog);
    }

    public List<AuditLogResponseDto> getAllAuditsByUserIdForAdmin(String token, String userId) {
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndRole(token);

        String userRole =  authGrpcResponseDto.getRole();

        if(!userRole.equals("ADMIN")){
            throw new UnauthorizedException("Only Admins are allowed to get audit log by user id");
        }

        List<AuditLog> auditLogs = auditRepository.findAllByUserId(userId);

        List<AuditLogResponseDto> auditLogResponseDtoList = auditLogs.stream().map(auditMapper :: toAuditLogResponseDto).toList();
        return auditLogResponseDtoList;
    }

    public AuditLogResponseDto getAuditByTransactionIdForAdmin(String token, String transactionId){
        AuthGrpcResponseDto authGrpcResponseDto = authServiceGrpcClient.extractUserIdAndRole(token);

        String userRole =  authGrpcResponseDto.getRole();

        if(!userRole.equals("ADMIN")){
            throw new UnauthorizedException("Only Admins are allowed to get audit log by transaction id");
        }

        AuditLog auditLog = auditRepository.findByTransactionId(transactionId).orElseThrow(() -> new AuditLogNotFoundException("AuditLog with transaction id: " + transactionId + " not found"));;

        return auditMapper.toAuditLogResponseDto(auditLog);
    }

    public AuditStatsResponseDto getAuditStatsForAdmin() {
        return new AuditStatsResponseDto(
                auditRepository.getTotalTransactions(),
                auditRepository.getSuccessCount(),
                auditRepository.getFailedCount(),
                auditRepository.getTotalVolume(),
                auditRepository.getLatestTransactionTime()
        );
    }

}
