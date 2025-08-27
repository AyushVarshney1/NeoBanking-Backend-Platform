package com.neobank.neobankauditservice.model;

import com.neobank.neobankauditservice.model.enums.TransactionStatus;
import com.neobank.neobankauditservice.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String transactionId;
    @Column(nullable = false, updatable = false)
    private Long accountNumber;
    @Column(nullable = false, updatable = false)
    private String userId;

    @Column(updatable = false)
    private Long beneficiaryAccountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionType type;

    @Column(nullable = false, updatable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TransactionStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdDate;
}