package com.bragdev.fraud.api.dto;

import lombok.Data;

@Data
public class TransactionRiskDto {
    private String transactionId;
    private double riskScore;
    private String riskLevel;
}