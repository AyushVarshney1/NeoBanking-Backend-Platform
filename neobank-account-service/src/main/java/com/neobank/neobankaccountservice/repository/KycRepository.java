package com.neobank.neobankaccountservice.repository;

import com.neobank.neobankaccountservice.model.Account;
import com.neobank.neobankaccountservice.model.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycRepository extends JpaRepository<Kyc,Long> {

    boolean existsByAccount(Account account);

    Kyc findByAccount(Account account);
}
