package com.aibankapp.service;

import com.aibankapp.dto.FraudCheckRequest;
import com.aibankapp.dto.FraudCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class FraudDetectionClient {

    private static final Logger log = LoggerFactory.getLogger(FraudDetectionClient.class);

    private final RestTemplate restTemplate;

    @Value("${app.ai-service.base-url}")
    private String aiServiceBaseUrl;

    public FraudDetectionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Calls the Python AI fraud-detection microservice to score a transaction.
     * Fails "open" (treats as non-fraud, score 0) if the AI service is unreachable,
     * so a monitoring blip in the AI tier never blocks legitimate banking traffic.
     */
    public FraudCheckResponse checkTransaction(FraudCheckRequest request) {
        try {
            FraudCheckResponse response = restTemplate.postForObject(
                    aiServiceBaseUrl + "/api/v1/predict", request, FraudCheckResponse.class);
            return response != null ? response : safeDefault();
        } catch (RestClientException ex) {
            log.warn("AI fraud-detection service unavailable, failing open: {}", ex.getMessage());
            return safeDefault();
        }
    }

    private FraudCheckResponse safeDefault() {
        FraudCheckResponse response = new FraudCheckResponse();
        response.setFraudScore(0.0);
        response.setFraud(false);
        response.setReason("ai-service-unavailable");
        return response;
    }
}
