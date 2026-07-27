package com.aibankapp.service;

import com.aibankapp.dto.FraudCheckRequest;
import com.aibankapp.dto.FraudCheckResponse;
import com.aibankapp.dto.TransactionResponse;
import com.aibankapp.dto.TransferRequest;
import com.aibankapp.model.Account;
import com.aibankapp.model.Transaction;
import com.aibankapp.repository.AccountRepository;
import com.aibankapp.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final FraudDetectionClient fraudDetectionClient;

    public TransactionService(TransactionRepository transactionRepository,
                               AccountRepository accountRepository,
                               FraudDetectionClient fraudDetectionClient) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.fraudDetectionClient = fraudDetectionClient;
    }

    @Transactional
    public TransactionResponse transfer(String username, TransferRequest request) {
        Account from = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        Account to = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (!from.getOwner().getUsername().equals(username)) {
            throw new IllegalStateException("You do not own the source account");
        }
        if (from.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }

        int recentTxCount = countRecentTransactions(from.getId());

        FraudCheckRequest fraudCheckRequest = new FraudCheckRequest(
                from.getAccountNumber(), to.getAccountNumber(), request.getAmount(),
                from.getBalance(), recentTxCount);
        FraudCheckResponse fraudResult = fraudDetectionClient.checkTransaction(fraudCheckRequest);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setAmount(request.getAmount());
        transaction.setType("TRANSFER");
        transaction.setFraudScore(fraudResult.getFraudScore());

        if (fraudResult.isFraud()) {
            transaction.setStatus("FLAGGED");
            transaction.setFraudReason(fraudResult.getReason());
            // Funds are NOT moved for flagged transactions; held for manual review.
        } else {
            from.setBalance(from.getBalance().subtract(request.getAmount()));
            to.setBalance(to.getBalance().add(request.getAmount()));
            accountRepository.save(from);
            accountRepository.save(to);
            transaction.setStatus("COMPLETED");
        }

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getHistory(String accountNumber, String username) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!account.getOwner().getUsername().equals(username)) {
            throw new IllegalStateException("You do not own this account");
        }
        return transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(account.getId(), account.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null,
                t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null,
                t.getAmount(),
                t.getType(),
                t.getStatus(),
                t.getFraudScore(),
                t.getFraudReason(),
                t.getCreatedAt());
    }

    private int countRecentTransactions(Long accountId) {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        return (int) transactionRepository
                .findByFromAccountIdOrToAccountIdOrderByCreatedAtDesc(accountId, accountId)
                .stream()
                .filter(t -> t.getCreatedAt().isAfter(oneHourAgo))
                .count();
    }
}
