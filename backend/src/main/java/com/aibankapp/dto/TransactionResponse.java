package com.aibankapp.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionResponse {
    private Long id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String type;
    private String status;
    private Double fraudScore;
    private String fraudReason;
    private Instant createdAt;

    public TransactionResponse(Long id, String fromAccountNumber, String toAccountNumber,
                                BigDecimal amount, String type, String status,
                                Double fraudScore, String fraudReason, Instant createdAt) {
        this.id = id;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.fraudScore = fraudScore;
        this.fraudReason = fraudReason;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public Double getFraudScore() { return fraudScore; }
    public String getFraudReason() { return fraudReason; }
    public Instant getCreatedAt() { return createdAt; }
}
