package com.bragdev.fraud.api.controller;

import com.bragdev.fraud.api.dto.FraudDetectionRequest;
import com.bragdev.fraud.api.dto.FraudDetectionResponse;
import com.bragdev.fraud.api.dto.TransactionDto;
import com.bragdev.fraud.api.service.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * REST controller that exposes fraud detection capabilities as an API.
 * Allows external systems to submit transactions for fraud evaluation.
 */
@RestController
@RequestMapping("/api/v1/fraud-detection")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @Autowired
    public FraudDetectionController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    /**
     * Endpoint for evaluating a single transaction for fraud
     * 
     * @param request The fraud detection request containing transaction details
     * @return A fraud detection response with risk assessment
     */
    @PostMapping("/evaluate")
    public ResponseEntity<FraudDetectionResponse> evaluateTransaction(
            @Valid @RequestBody FraudDetectionRequest request) {
        
        TransactionDto transaction = request.getTransaction();
        
        // Call the service to perform fraud detection
        FraudDetectionResponse response = fraudDetectionService.evaluateTransaction(transaction);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for evaluating a batch of transactions for fraud
     * 
     * @param request The fraud detection request containing multiple transactions
     * @return A fraud detection response with risk assessments for each transaction
     */
    @PostMapping("/evaluate-batch")
    public ResponseEntity<FraudDetectionResponse> evaluateBatch(
            @Valid @RequestBody FraudDetectionRequest request) {
        
        // Call the service to perform batch fraud detection
        FraudDetectionResponse response = fraudDetectionService.evaluateBatch(request.getTransactions());
        
        return ResponseEntity.ok(response);
    }
}