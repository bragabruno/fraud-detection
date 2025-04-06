package com.bragdev.fraud.detection.rule;

import java.util.HashSet;
import java.util.Set;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.rule.BaseRule;

/**
 * Rule that detects transactions in merchant categories that are unusual for the account.
 * Transactions in categories that the customer doesn't typically use may indicate fraud.
 */
public class UnusualMerchantCategoryRule extends BaseRule {
    
    private final Set<String> unusualCategories;
    
    /**
     * Constructor for the unusual merchant category rule
     * 
     * @param unusualCategories Set of merchant categories considered unusual or high-risk
     */
    public UnusualMerchantCategoryRule(Set<String> unusualCategories) {
        super(
            "UNUSUAL_MERCHANT_CATEGORY",
            "Unusual Merchant Category",
            "Detects transactions in merchant categories that are unusual for the account",
            "MERCHANT",
            65.0 // Severity set to 65 out of 100
        );
        
        if (unusualCategories == null || unusualCategories.isEmpty()) {
            throw new IllegalArgumentException("Unusual categories set cannot be null or empty");
        }
        
        this.unusualCategories = new HashSet<>(unusualCategories);
    }
    
    /**
     * Convenience constructor with varargs for easier initialization
     */
    public UnusualMerchantCategoryRule(String... categories) {
        this(Set.of(categories));
    }
    
    /**
     * Evaluates if the transaction is in an unusual merchant category
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        String merchantCategory = transaction.getMerchantCategory();
        if (merchantCategory == null || merchantCategory.isEmpty()) {
            return false;
        }
        
        // Trigger if the merchant category is in our unusual categories set
        return unusualCategories.contains(merchantCategory.toUpperCase());
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null) {
            return "Invalid transaction data";
        }
        
        String merchantCategory = transaction.getMerchantCategory();
        if (merchantCategory == null || merchantCategory.isEmpty()) {
            return "Transaction has no merchant category";
        }
        
        return String.format(
            "Transaction merchant category '%s' is on the unusual categories list",
            merchantCategory
        );
    }
}