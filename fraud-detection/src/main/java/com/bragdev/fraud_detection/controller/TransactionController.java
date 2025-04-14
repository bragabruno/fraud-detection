package com.bragdev.fraud_detection.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bragdev.fraud_detection.model.Transaction;
import com.bragdev.fraud_detection.service.FraudDetectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final FraudDetectionService fraudDetectionService;
    
    @PostMapping
    public ResponseEntity<Transaction> processTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction processedTransaction = fraudDetectionService.processTransaction(transaction);
        return new ResponseEntity<>(processedTransaction, HttpStatus.CREATED);
    }
    
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = fraudDetectionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/fraudulent")
    public ResponseEntity<List<Transaction>> getFraudulentTransactions() {
        List<Transaction> fraudulentTransactions = fraudDetectionService.getFraudulentTransactions();
        return ResponseEntity.ok(fraudulentTransactions);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        try {
            Transaction transaction = fraudDetectionService.getTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}