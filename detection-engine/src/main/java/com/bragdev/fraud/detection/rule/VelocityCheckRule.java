package com.bragdev.fraud.detection.rule;

import java.time.Duration;
import java.time.Instant;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.rule.BaseRule;

/**
 * Rule that detects rapid succession of transactions within a short time window.
 * Multiple transactions in quick succession may indicate an attempt to use a compromised card
 * before it gets blocked.
 */
public class VelocityCheckRule extends BaseRule {
    
    private final Duration timeWindow;
    private final int maxTransactionsInWindow;
    
    // Store the last transaction time for each account
    private Instant lastTransactionTime;
    private int transactionCount;
    private String currentAccountId;
    
    /**
     * Constructor for the velocity check rule
     * 
     * @param timeWindow The time window in which to check for transaction velocity
     * @param maxTransactionsInWindow The maximum number of allowed transactions in the window
     */
    public VelocityCheckRule(Duration timeWindow, int maxTransactionsInWindow) {
        super(
            "VELOCITY_CHECK",
            "Transaction Velocity Check",
            "Detects rapid succession of transactions within a short time window",
            "VELOCITY",
            80.0 // Severity set to 80 out of 100
        );
        
        if (timeWindow == null || timeWindow.isNegative() || timeWindow.isZero()) {
            throw new IllegalArgumentException("Time window must be positive");
        }
        
        if (maxTransactionsInWindow <= 0) {
            throw new IllegalArgumentException("Max transactions must be positive");
        }
        
        this.timeWindow = timeWindow;
        this.maxTransactionsInWindow = maxTransactionsInWindow;
        
        this.transactionCount = 0;
        this.lastTransactionTime = Instant.MIN;
        this.currentAccountId = "";
    }
    
    /**
     * Evaluates if the transaction exceeds the velocity threshold
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null || transaction.getId() == null || 
            transaction.getAccountId() == null || transaction.getTimestamp() == null) {
            return false;
        }
        
        Instant transactionTime = transaction.getTimestamp();
        String accountId = transaction.getAccountId();
        
        // If this is a new account, reset the counter
        if (!accountId.equals(currentAccountId)) {
            currentAccountId = accountId;
            transactionCount = 1;
            lastTransactionTime = transactionTime;
            return false;
        }
        
        // Calculate if the transaction is within the time window
        Duration timeSinceLastTransaction = Duration.between(lastTransactionTime, transactionTime);
        
        // If outside the window, reset the counter
        if (timeSinceLastTransaction.compareTo(timeWindow) > 0) {
            transactionCount = 1;
            lastTransactionTime = transactionTime;
            return false;
        }
        
        // Increment the counter and update the last transaction time
        transactionCount++;
        
        // Update the last transaction time if this transaction is more recent
        if (transactionTime.isAfter(lastTransactionTime)) {
            lastTransactionTime = transactionTime;
        }
        
        // Trigger the rule if the transaction count exceeds the threshold
        return transactionCount > maxTransactionsInWindow;
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null) {
            return "Invalid transaction data";
        }
        
        return String.format(
            "Account %s has performed %d transactions within %d seconds, exceeding the threshold of %d",
            transaction.getAccountId(),
            transactionCount,
            timeWindow.getSeconds(),
            maxTransactionsInWindow
        );
    }
}