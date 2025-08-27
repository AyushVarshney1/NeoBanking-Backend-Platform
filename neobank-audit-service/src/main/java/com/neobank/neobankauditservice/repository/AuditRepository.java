package com.neobank.neobankauditservice.repository;

import com.neobank.neobankauditservice.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AuditRepository extends JpaRepository<AuditLog,Long> {
    List<AuditLog> findAllByUserId(String userId);

    List<AuditLog> findAllByUserIdAndAccountNumber(String userId, Long accountNumber);

    Optional<AuditLog> findByTransactionId(String transactionId);


    @Query("SELECT COUNT(a) FROM AuditLog a")
    long getTotalTransactions();

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.status = com.neobank.neobankauditservice.model.enums.TransactionStatus.SUCCESS")
    long getSuccessCount();

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.status = com.neobank.neobankauditservice.model.enums.TransactionStatus.FAILED")
    long getFailedCount();

    @Query("SELECT COALESCE(SUM(a.amount),0) FROM AuditLog a")
    double getTotalVolume();

    @Query("SELECT MAX(a.createdDate) FROM AuditLog a")
    Date getLatestTransactionTime();
}
