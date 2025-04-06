package com.bragdev.fraud.detection.rule;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.rule.Rule;

/**
 * Rule that detects transactions occurring at suspicious times (like late night).
 * Transactions at unusual hours often indicate fraud.
 */
public class TimeBasedPatternRule implements Rule {
    
    private final String id = "TIME_BASED_PATTERN";
    private final String name = "Time-Based Pattern";
    private final String description = "Detects transactions occurring at suspicious times";
    private final String category = "TEMPORAL";
    private final double severity = 60.0; // Severity set to 60 out of 100
    
    private final LocalTime startSuspiciousHour;
    private final LocalTime endSuspiciousHour;
    private final Set<DayOfWeek> suspiciousDays;
    private final ZoneId timeZone;
    
    /**
     * Constructor for the time-based pattern rule
     * 
     * @param startSuspiciousHour Start of suspicious time window
     * @param endSuspiciousHour End of suspicious time window
     * @param suspiciousDays Set of days considered suspicious
     * @param timeZone Time zone to evaluate in
     */
    public TimeBasedPatternRule(
            LocalTime startSuspiciousHour,
            LocalTime endSuspiciousHour,
            Set<DayOfWeek> suspiciousDays,
            ZoneId timeZone) {
        
        if (startSuspiciousHour == null || endSuspiciousHour == null) {
            throw new IllegalArgumentException("Time window boundaries cannot be null");
        }
        
        this.startSuspiciousHour = startSuspiciousHour;
        this.endSuspiciousHour = endSuspiciousHour;
        this.suspiciousDays = suspiciousDays != null ? suspiciousDays : Set.of();
        this.timeZone = timeZone != null ? timeZone : ZoneId.systemDefault();
    }
    
    /**
     * Convenience constructor with defaults for suspicious time (midnight to 5am)
     */
    public TimeBasedPatternRule() {
        this(
            LocalTime.of(0, 0),   // Midnight
            LocalTime.of(5, 0),   // 5 AM
            Set.of(),             // No specific days
            ZoneId.systemDefault()
        );
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
     * Evaluates if the transaction occurred at a suspicious time
     */
    @Override
    public boolean evaluate(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        try {
            // Use the Lombok-generated getter for timestamp field
            Instant timestamp = transaction.getTimestamp();
            if (timestamp == null) {
                return false;
            }
            
            // Convert the timestamp to a ZonedDateTime for easier time manipulation
            ZonedDateTime zonedTime = timestamp.atZone(timeZone);
            LocalTime transactionTime = zonedTime.toLocalTime();
            DayOfWeek transactionDay = zonedTime.getDayOfWeek();
            
            // Check if the transaction occurred during the suspicious time window
            boolean isInSuspiciousTimeWindow;
            if (startSuspiciousHour.isBefore(endSuspiciousHour)) {
                // Normal time window (e.g., 22:00 to 05:00)
                isInSuspiciousTimeWindow = !transactionTime.isBefore(startSuspiciousHour) && 
                                           transactionTime.isBefore(endSuspiciousHour);
            } else {
                // Wrapped time window (e.g., 22:00 to 05:00)
                isInSuspiciousTimeWindow = !transactionTime.isBefore(startSuspiciousHour) || 
                                           transactionTime.isBefore(endSuspiciousHour);
            }
            
            // Check if the transaction occurred on a suspicious day
            boolean isOnSuspiciousDay = suspiciousDays.isEmpty() || suspiciousDays.contains(transactionDay);
            
            return isInSuspiciousTimeWindow && isOnSuspiciousDay;
        } catch (Exception e) {
            // If any error occurs (e.g., field access issues), don't trigger the rule
            return false;
        }
    }
    
    @Override
    public String generateTriggerReason(Transaction transaction) {
        if (transaction == null) {
            return "Invalid transaction data";
        }
        
        try {
            Instant timestamp = transaction.getTimestamp();
            if (timestamp == null) {
                return "Transaction has no timestamp";
            }
            
            ZonedDateTime zonedTime = timestamp.atZone(timeZone);
            
            return String.format(
                "Transaction occurred at %s, which falls within the suspicious time window (%s to %s)",
                zonedTime.toLocalTime(),
                startSuspiciousHour,
                endSuspiciousHour
            );
        } catch (Exception e) {
            return "Unable to analyze transaction time pattern";
        }
    }
}