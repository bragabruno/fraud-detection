package com.bragdev.fraud.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class FraudDetectionRequest {
    private TransactionDto transaction;
    private List<TransactionDto> transactions;
}