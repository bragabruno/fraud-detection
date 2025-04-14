package com.bragdev.fraud_detection.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bragdev.fraud_detection.model.Transaction;
import com.bragdev.fraud_detection.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final TransactionRepository transactionRepository;
    
    // Threshold for high amount transactions (in dollars)
    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = new BigDecimal("1000.00");
    
    // Time window for multiple transactions (in hours)
    private static final int SUSPICIOUS_TIME_WINDOW_HOURS = 1;
    
    // Threshold for number of transactions in the time window
    private static final int TRANSACTION_COUNT_THRESHOLD = 3;
    
    public boolean isFraudulent(Transaction transaction) {
        // Rule 1: Check for high amount transactions
        if (isHighAmountTransaction(transaction)) {
            return true;
        }
        
        // Rule 2: Check for multiple transactions in a short time period
        if (hasMultipleRecentTransactions(transaction)) {
            return true;
        }
        
        // Add more sophisticated fraud detection rules here
        
        return false;
    }
    
    private boolean isHighAmountTransaction(Transaction transaction) {
        return transaction.getAmount().compareTo(HIGH_AMOUNT_THRESHOLD) >= 0;
    }
    
    private boolean hasMultipleRecentTransactions(Transaction transaction) {
        LocalDateTime endTime = transaction.getTimestamp();
        LocalDateTime startTime = endTime.minusHours(SUSPICIOUS_TIME_WINDOW_HOURS);
        
        List<Transaction> recentTransactions = transactionRepository
                .findByCardNumberAndTimestampBetween(
                        transaction.getCardNumber(), 
                        startTime, 
                        endTime);
        
        return recentTransactions.size() >= TRANSACTION_COUNT_THRESHOLD;
    }
    
    public Transaction processTransaction(Transaction transaction) {
        // Default values if not set
        if (transaction.getTimestamp() == null) {
            transaction.setTimestamp(LocalDateTime.now());
        }
        if (transaction.getIsFraudulent() == null) {
            transaction.setIsFraudulent(false);
        }
        
        // Detect fraud
        boolean fraudDetected = isFraudulent(transaction);
        transaction.setIsFraudulent(fraudDetected);
        
        // Set status based on fraud detection
        if (fraudDetected) {
            transaction.setStatus(Transaction.TransactionStatus.FLAGGED);
        } else {
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        }
        
        // Save to database
        return transactionRepository.save(transaction);
    }
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<Transaction> getFraudulentTransactions() {
        return transactionRepository.findByIsFraudulent(true);
    }
    
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }
    
    public List<Transaction> getTransactionsByCardNumber(String cardNumber) {
        return transactionRepository.findByCardNumber(cardNumber);
    }
}