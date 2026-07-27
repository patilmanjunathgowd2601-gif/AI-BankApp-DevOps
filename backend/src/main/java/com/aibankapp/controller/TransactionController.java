package com.aibankapp.controller;

import com.aibankapp.dto.TransactionResponse;
import com.aibankapp.dto.TransferRequest;
import com.aibankapp.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(Authentication authentication,
                                                          @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transactionService.transfer(authentication.getName(), request));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> history(Authentication authentication,
                                                               @PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getHistory(accountNumber, authentication.getName()));
    }
}
