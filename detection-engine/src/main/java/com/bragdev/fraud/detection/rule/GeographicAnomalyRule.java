package com.bragdev.fraud.detection.rule;

import com.bragdev.fraud.core.model.GeoLocation;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.core.rule.BaseRule;

/**
 * Rule that detects transactions occurring far from the customer's typical location.
 * Transactions occurring in unusual locations may indicate card theft or account takeover.
 */
public class GeographicAnomalyRule extends BaseRule {
    
    private final GeoLocation referenceLocation;
    private final double distanceThresholdKm;
    
    /**
     * Constructor for the geographic anomaly rule
     * 
     * @param referenceLocation The reference location (e.g., home location of the customer)
     * @param distanceThresholdKm The distance threshold in kilometers
     */
    public GeographicAnomalyRule(GeoLocation referenceLocation, double distanceThresholdKm) {
        super(
            "GEOGRAPHIC_ANOMALY",
            "Geographic Anomaly",
            "Detects transactions occurring unusually far from the reference location",
            "LOCATION",
            75.0 // Severity
        );
        
        if (referenceLocation == null) {
            throw new IllegalArgumentException("Reference location cannot be null");
        }
        
        if (distanceThresholdKm <= 0) {
            throw new IllegalArgumentException("Distance threshold must be positive");
        }
        
        this.referenceLocation = referenceLocation;
        this.distanceThresholdKm = distanceThresholdKm;
    }
    
    /**
     * Evaluates if the transaction location is unusually far from the reference location
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        // Use proper getter instead of direct field access or reflection
        GeoLocation transactionLocation = transaction.getLocation();
        
        if (transactionLocation == null) {
            return false;
        }
        
        // Calculate the distance between the transaction location and reference location
        double distance = referenceLocation.distanceFrom(transactionLocation);
        
        // Trigger the rule if the distance exceeds the threshold
        return distance > distanceThresholdKm;
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null) {
            return "Invalid transaction data";
        }
        
        // Use proper getter instead of direct field access or reflection
        GeoLocation transactionLocation = transaction.getLocation();
        
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