package com.neobank.neobankauditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AuditStatsResponseDto {
    private long totalTransactions;
    private long successCount;
    private long failedCount;
    private double totalVolume;
    private Date latestTransaction;
}
