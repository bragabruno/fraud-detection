package com.bragdev.fraud.detection.rule;

import java.util.HashSet;
import java.util.Set;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.core.rule.Rule;

/**
 * Rule that detects transactions in merchant categories that are unusual for the account.
 * Transactions in categories that the customer doesn't typically use may indicate fraud.
 */
public class UnusualMerchantCategoryRule implements Rule {
    
    private final String id = "UNUSUAL_MERCHANT_CATEGORY";
    private final String name = "Unusual Merchant Category";
    private final String description = "Detects transactions in merchant categories that are unusual for the account";
    private final String category = "MERCHANT";
    private final double severity = 65.0; // Severity set to 65 out of 100
    
    private final Set<String> unusualCategories;
    
    /**
     * Constructor for the unusual merchant category rule
     * 
     * @param unusualCategories Set of merchant categories considered unusual or high-risk
     */
    public UnusualMerchantCategoryRule(Set<String> unusualCategories) {
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
     * Evaluates if the transaction is in an unusual merchant category
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        // Use reflection to safely access the merchantCategory field
        String merchantCategory = null;
        try {
            java.lang.reflect.Field merchantCategoryField = Transaction.class.getDeclaredField("merchantCategory");
            merchantCategoryField.setAccessible(true);
            merchantCategory = (String) merchantCategoryField.get(transaction);
            
            if (merchantCategory == null || merchantCategory.isEmpty()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        
        // Trigger if the merchant category is in our unusual categories set
        return unusualCategories.contains(merchantCategory.toUpperCase());
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
        
        // Use reflection to safely access the merchantCategory field
        String merchantCategory = null;
        try {
            java.lang.reflect.Field merchantCategoryField = Transaction.class.getDeclaredField("merchantCategory");
            merchantCategoryField.setAccessible(true);
            merchantCategory = (String) merchantCategoryField.get(transaction);
            
            if (merchantCategory == null || merchantCategory.isEmpty()) {
                return "Transaction has no merchant category";
            }
        } catch (Exception e) {
            return "Unable to access merchant category";
        }
        
        return String.format(
            "Transaction merchant category '%s' is on the unusual categories list",
            merchantCategory
        );
    }
}