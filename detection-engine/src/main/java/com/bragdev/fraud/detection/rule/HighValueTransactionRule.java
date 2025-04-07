package com.bragdev.fraud.detection.rule;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.core.rule.Rule;

import java.math.BigDecimal;

/**
 * Rule that detects transactions with amounts exceeding a configurable threshold.
 * High-value transactions may indicate fraud, especially when they deviate from
 * the customer's normal spending patterns.
 */
public class HighValueTransactionRule implements Rule {
    
    private final String id = "HIGH_VALUE_TRANSACTION";
    private final String name = "High Value Transaction";
    private final String description = "Detects transactions with amounts exceeding a specified threshold";
    private final String category = "AMOUNT";
    private final double severity = 70.0; // Severity set to 70 out of 100
    
    private final BigDecimal threshold;
    private final String currency;
    
    /**
     * Constructor for the high-value transaction rule
     * 
     * @param threshold The amount threshold above which transactions trigger this rule
     * @param currency The currency to check (if null, applies to all currencies)
     */
    public HighValueTransactionRule(BigDecimal threshold, String currency) {
        if (threshold == null || threshold.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Threshold must be a positive number");
        }
        
        this.threshold = threshold;
        this.currency = currency;
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getCategory() {
        return category;
    }
    
    @Override
    public double getSeverity() {
        return severity;
    }
    
    /**
     * Evaluates if the transaction exceeds the threshold amount
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        // Use reflection to safely access fields
        BigDecimal amount = null;
        String transactionCurrency = null;
        
        try {
            java.lang.reflect.Field amountField = Transaction.class.getDeclaredField("amount");
            amountField.setAccessible(true);
            amount = (BigDecimal) amountField.get(transaction);
            
            java.lang.reflect.Field currencyField = Transaction.class.getDeclaredField("currency");
            currencyField.setAccessible(true);
            transactionCurrency = (String) currencyField.get(transaction);
        } catch (Exception e) {
            return false;
        }
        
        if (amount == null) {
            return false;
        }
        
        // If currency is specified, only evaluate transactions in that currency
        if (currency != null && !currency.equals(transactionCurrency)) {
            return false;
        }
        
        // Trigger the rule if the transaction amount exceeds the threshold
        return amount.compareTo(threshold) > 0;
    }
    
    @Override
    public TriggeredRule createTriggeredRule(Transaction transaction) {
        if (evaluate(transaction)) {
            return TriggeredRule.create(getId(), getName(), getSeverity());
        }
        return null;
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null) {
            return "Invalid transaction data";
        }
        
        // Use reflection to safely access fields
        BigDecimal amount = null;
        String transactionCurrency = null;
        
        try {
            java.lang.reflect.Field amountField = Transaction.class.getDeclaredField("amount");
            amountField.setAccessible(true);
            amount = (BigDecimal) amountField.get(transaction);
            
            java.lang.reflect.Field currencyField = Transaction.class.getDeclaredField("currency");
            currencyField.setAccessible(true);
            transactionCurrency = (String) currencyField.get(transaction);
        } catch (Exception e) {
            return "Unable to access transaction data";
        }
        
        if (amount == null) {
            return "Transaction has no amount";
        }
        
        return String.format(
            "Transaction amount %s %s exceeds the threshold of %s %s",
            amount,
            transactionCurrency,
            threshold,
            currency != null ? currency : transactionCurrency
        );
    }
}