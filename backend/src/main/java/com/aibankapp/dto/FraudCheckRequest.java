package com.aibankapp.dto;

import java.math.BigDecimal;

public class FraudCheckRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private BigDecimal fromAccountBalance;
    private int transactionsLastHour;

    public FraudCheckRequest() {}

    public FraudCheckRequest(String fromAccountNumber, String toAccountNumber, BigDecimal amount,
                              BigDecimal fromAccountBalance, int transactionsLastHour) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.fromAccountBalance = fromAccountBalance;
        this.transactionsLastHour = transactionsLastHour;
    }

    public String getFromAccountNumber() { return fromAccountNumber; }
    public void setFromAccountNumber(String fromAccountNumber) { this.fromAccountNumber = fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public void setToAccountNumber(String toAccountNumber) { this.toAccountNumber = toAccountNumber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getFromAccountBalance() { return fromAccountBalance; }
    public void setFromAccountBalance(BigDecimal fromAccountBalance) { this.fromAccountBalance = fromAccountBalance; }
    public int getTransactionsLastHour() { return transactionsLastHour; }
    public void setTransactionsLastHour(int transactionsLastHour) { this.transactionsLastHour = transactionsLastHour; }
}
