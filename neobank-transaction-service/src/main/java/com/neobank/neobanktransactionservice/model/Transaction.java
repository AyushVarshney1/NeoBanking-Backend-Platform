package com.neobank.neobanktransactionservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID transactionId = UUID.randomUUID();

    @Column(nullable = false, updatable = false)
    private Long accountNumber;

    @Column(updatable = false)
    private Long targetAccountNumber;

    @Column(nullable = false, updatable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column
    private Double closingBalance;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}
