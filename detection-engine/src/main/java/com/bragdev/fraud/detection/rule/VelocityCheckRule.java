package com.bragdev.fraud.detection.rule;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.core.rule.BaseRule;

/**
 * Rule that detects rapid succession of transactions within a short time window.
 * Multiple transactions in quick succession may indicate an attempt to use a compromised card
 * before it gets blocked.
 */
public class VelocityCheckRule extends BaseRule {
    
    private final Duration timeWindow;
    private final int maxTransactionsInWindow;
    
    // Inner class to hold account state information
    private static class AccountState {
        private volatile Instant lastTransactionTime;
        private volatile int transactionCount;
        
        public AccountState() {
            this.transactionCount = 0;
            this.lastTransactionTime = Instant.MIN;
        }
        
        public synchronized void incrementCount() {
            this.transactionCount++;
        }
        
        public synchronized void resetCount() {
            this.transactionCount = 1;
        }
        
        public synchronized void updateLastTransactionTime(Instant time) {
            if (time != null && time.isAfter(this.lastTransactionTime)) {
                this.lastTransactionTime = time;
            }
        }
    }
    
    // Thread-safe map to store account state
    private final ConcurrentHashMap<String, AccountState> accountStates = new ConcurrentHashMap<>();
    
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
            80.0 // Severity
        );
        
        if (timeWindow == null || timeWindow.isNegative() || timeWindow.isZero()) {
            throw new IllegalArgumentException("Time window must be positive");
        }
        
        if (maxTransactionsInWindow <= 0) {
            throw new IllegalArgumentException("Max transactions must be positive");
        }
        
        this.timeWindow = timeWindow;
        this.maxTransactionsInWindow = maxTransactionsInWindow;
    }
    
    /**
     * Evaluates if the transaction exceeds the velocity threshold
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        // Use proper getters instead of reflection
        UUID id = transaction.getId();
        String accountId = transaction.getAccountId();
        Instant transactionTime = transaction.getTimestamp();
        
        if (id == null || accountId == null || transactionTime == null) {
            return false;
        }
        
        // Get or create account state
        AccountState state = accountStates.computeIfAbsent(accountId, k -> new AccountState());
        
        // Calculate if the transaction is within the time window
        Duration timeSinceLastTransaction = Duration.between(state.lastTransactionTime, transactionTime);
        
        // If outside the window, reset the counter
        if (timeSinceLastTransaction.compareTo(timeWindow) > 0) {
            state.resetCount();
            state.updateLastTransactionTime(transactionTime);
            return false;
        }
        
        // Increment the counter and update the last transaction time
        state.incrementCount();
        state.updateLastTransactionTime(transactionTime);
        
        // Trigger the rule if the transaction count exceeds the threshold
        return state.transactionCount > maxTransactionsInWindow;
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null) {
            return "Invalid transaction data";
        }
        
        String accountId = transaction.getAccountId();
        if (accountId == null) {
            return "Unknown account";
        }
        
        AccountState state = accountStates.get(accountId);
        if (state == null) {
            return "No velocity data for account";
        }
        
        return String.format(
            "Account %s has performed %d transactions within %d seconds, exceeding the threshold of %d",
            accountId,
            state.transactionCount,
            timeWindow.getSeconds(),
            maxTransactionsInWindow
        );
    }
    
    /**
     * Cleanup method to prevent memory leaks
     * This should be called when the rule is no longer needed
     */
    public void cleanup() {
        accountStates.clear();
    }
}