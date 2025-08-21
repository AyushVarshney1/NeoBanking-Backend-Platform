package com.neobank.neobankaccountservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "kyc")
public class Kyc {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String panNumber;

    @Column(nullable = false, unique = true, updatable = false)
    private String aadhaarNumber;

    @Column(nullable = false)
    private String address;

    @OneToOne
    @JoinColumn(name = "account_number", referencedColumnName = "accountNumber",
            nullable = false, unique = true, updatable = false)
    private Account account;
}
