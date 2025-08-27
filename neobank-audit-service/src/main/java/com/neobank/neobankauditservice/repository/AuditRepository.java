package com.neobank.neobankauditservice.repository;

import com.neobank.neobankauditservice.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuditRepository extends JpaRepository<AuditLog,Long> {
    List<AuditLog> findAllByUserId(String userId);

    List<AuditLog> findAllByUserIdAndAccountNumber(String userId, Long accountNumber);

    Optional<AuditLog> findByTransactionId(String transactionId);
}
