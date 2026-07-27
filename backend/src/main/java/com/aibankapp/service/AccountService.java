package com.aibankapp.service;

import com.aibankapp.dto.AccountResponse;
import com.aibankapp.model.Account;
import com.aibankapp.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<AccountResponse> getAccountsForUser(String username) {
        return accountRepository.findByOwnerUsername(username).stream()
                .map(a -> new AccountResponse(a.getAccountNumber(), a.getAccountType(), a.getBalance()))
                .toList();
    }

    public Account getByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }
}
