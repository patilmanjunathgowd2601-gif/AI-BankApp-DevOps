package com.aibankapp.controller;

import com.aibankapp.dto.AccountResponse;
import com.aibankapp.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> myAccounts(Authentication authentication) {
        return ResponseEntity.ok(accountService.getAccountsForUser(authentication.getName()));
    }
}
