package com.aibankapp.repository;

import com.aibankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerUsername(String username);
    Optional<Account> findByAccountNumber(String accountNumber);
}
