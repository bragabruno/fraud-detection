package com.bragdev.fraud.detection.rule;

import com.bragdev.fraud.core.model.GeoLocation;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.core.rule.Rule;

/**
 * Rule that detects transactions occurring far from the customer's typical location.
 * Transactions occurring in unusual locations may indicate card theft or account takeover.
 */
public class GeographicAnomalyRule implements Rule {
    
    private final String id = "GEOGRAPHIC_ANOMALY";
    private final String name = "Geographic Anomaly";
    private final String description = "Detects transactions occurring unusually far from the reference location";
    private final String category = "LOCATION";
    private final double severity = 75.0; // Severity set to 75 out of 100
    
    private final GeoLocation referenceLocation;
    private final double distanceThresholdKm;
    
    /**
     * Constructor for the geographic anomaly rule
     * 
     * @param referenceLocation The reference location (e.g., home location of the customer)
     * @param distanceThresholdKm The distance threshold in kilometers
     */
    public GeographicAnomalyRule(GeoLocation referenceLocation, double distanceThresholdKm) {
        if (referenceLocation == null) {
            throw new IllegalArgumentException("Reference location cannot be null");
        }
        
        if (distanceThresholdKm <= 0) {
            throw new IllegalArgumentException("Distance threshold must be positive");
        }
        
        this.referenceLocation = referenceLocation;
        this.distanceThresholdKm = distanceThresholdKm;
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
     * Evaluates if the transaction location is unusually far from the reference location
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        // Access the location field directly instead of using getter
        GeoLocation transactionLocation = null;
        try {
            // Fields may be accessed via reflection in a real implementation
            // For now, just check if the transaction has location data in a generic way
            transactionLocation = transaction.location;
        } catch (Exception e) {
            return false;
        }
        
        if (transactionLocation == null) {
            return false;
        }
        
        // Calculate the distance between the transaction location and reference location
        double distance = referenceLocation.distanceFrom(transactionLocation);
        
        // Trigger the rule if the distance exceeds the threshold
        return distance > distanceThresholdKm;
    }
    
    @Override
    public TriggeredRule createTriggeredRule(Transaction transaction) {
        if (evaluate(transaction)) {
            // Use static create method instead of builder
            return TriggeredRule.create(getId(), getName(), getSeverity());
        }
        return null;
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null) {
            return "Invalid transaction data";
        }
        
        // Access the location field directly
        GeoLocation transactionLocation = null;
        try {
            transactionLocation = transaction.location;
        } catch (Exception e) {
            return "Unable to access transaction location";
        }
        
        if (transactionLocation == null) {
            return "Transaction has no location data";
        }
        
        double distance = referenceLocation.distanceFrom(transactionLocation);
        
        return String.format(
            "Transaction location is %.2f km from the reference location, exceeding the threshold of %.2f km",
            distance,
            distanceThresholdKm
        );
    }
}