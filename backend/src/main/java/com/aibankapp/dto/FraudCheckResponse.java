package com.aibankapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FraudCheckResponse {
    private double fraudScore;
    private boolean isFraud;
    private String reason;

    public double getFraudScore() { return fraudScore; }
    public void setFraudScore(double fraudScore) { this.fraudScore = fraudScore; }

    @JsonProperty("isFraud")
    public boolean isFraud() { return isFraud; }

    @JsonProperty("isFraud")
    public void setFraud(boolean fraud) { isFraud = fraud; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
