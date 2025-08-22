package com.neobank.neobankaccountservice.repository;

import com.neobank.neobankaccountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(Long accountNumber);

    Account findByAccountNumber(Long accountNumber);

    Account findByAccountNumberAndUserId(Long accountNumber, UUID userId);

    List<Account> findAllByUserId(UUID userId);
}
