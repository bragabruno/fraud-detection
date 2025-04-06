package com.bragdev.fraud.detection.rule;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.rule.BaseRule;

import java.math.BigDecimal;

/**
 * Rule that detects transactions with amounts exceeding a configurable threshold.
 * High-value transactions may indicate fraud, especially when they deviate from
 * the customer's normal spending patterns.
 */
public class HighValueTransactionRule extends BaseRule {
    
    private final BigDecimal threshold;
    private final String currency;
    
    /**
     * Constructor for the high-value transaction rule
     * 
     * @param threshold The amount threshold above which transactions trigger this rule
     * @param currency The currency to check (if null, applies to all currencies)
     */
    public HighValueTransactionRule(BigDecimal threshold, String currency) {
        super(
            "HIGH_VALUE_TRANSACTION",
            "High Value Transaction",
            "Detects transactions with amounts exceeding a specified threshold",
            "AMOUNT",
            70.0 // Severity set to 70 out of 100
        );
        
        if (threshold == null || threshold.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Threshold must be a positive number");
        }
        
        this.threshold = threshold;
        this.currency = currency;
    }
    
    /**
     * Evaluates if the transaction exceeds the threshold amount
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null || transaction.getAmount() == null) {
            return false;
        }
        
        // If currency is specified, only evaluate transactions in that currency
        if (currency != null && !currency.equals(transaction.getCurrency())) {
            return false;
        }
        
        // Trigger the rule if the transaction amount exceeds the threshold
        return transaction.getAmount().compareTo(threshold) > 0;
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null || transaction.getAmount() == null) {
            return "Invalid transaction data";
        }
        
        return String.format(
            "Transaction amount %s %s exceeds the threshold of %s %s",
            transaction.getAmount(),
            transaction.getCurrency(),
            threshold,
            currency != null ? currency : transaction.getCurrency()
        );
    }
}