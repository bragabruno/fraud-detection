package com.bragdev.fraud;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Financial Transaction Fraud Detection System.
 * This application demonstrates a rule-based approach to fraud detection.
 */
@SpringBootApplication
public class FraudDetectionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FraudDetectionApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner demonstrateFraudDetection() {
        return args -> {
            System.out.println("\n===== Fraud Detection System Demonstration =====\n");
            
            // Create a list of fraud detection rules
            List<Rule> rules = new ArrayList<>();
            rules.add(new HighValueTransactionRule());
            rules.add(new UnusualLocationRule());
            
            System.out.println("Rules registered: " + rules.size());
            
            // Create sample transactions
            Transaction normalTransaction = createNormalTransaction();
            Transaction suspiciousTransaction = createSuspiciousTransaction();
            Transaction fraudulentTransaction = createFraudulentTransaction();
            
            // Evaluate transactions
            System.out.println("\n--- Evaluating Normal Transaction ---");
            evaluateTransaction(rules, normalTransaction);
            
            System.out.println("\n--- Evaluating Suspicious Transaction ---");
            evaluateTransaction(rules, suspiciousTransaction);
            
            System.out.println("\n--- Evaluating Fraudulent Transaction ---");
            evaluateTransaction(rules, fraudulentTransaction);
            
            System.out.println("\n===== Demonstration Complete =====");
        };
    }
    
    /**
     * Evaluates a transaction against all rules and displays the results
     */
    private void evaluateTransaction(List<Rule> rules, Transaction transaction) {
        System.out.println("Transaction: " + transaction.getAmount() + " " +
                transaction.getCurrency() + " at " + transaction.getMerchantName());
        
        List<TriggeredRule> triggeredRules = new ArrayList<>();
        
        // Apply each rule and collect triggered ones
        for (Rule rule : rules) {
            TriggeredRule triggered = rule.createTriggeredRule(transaction);
            if (triggered != null) {
                triggeredRules.add(triggered);
                System.out.println("  Rule triggered: " + rule.getName() + " - " +
                        rule.generateTriggerReason(transaction));
            }
        }
        
        // Calculate an overall risk score (simple average of severities)
        double overallScore = 0;
        if (!triggeredRules.isEmpty()) {
            double totalSeverity = 0;
            for (TriggeredRule rule : triggeredRules) {
                totalSeverity += rule.getSeverity();
            }
            overallScore = totalSeverity / triggeredRules.size();
        }
        
        String riskLevel = determineRiskLevel(overallScore);
        
        System.out.println("Risk assessment: " + riskLevel + " (" + overallScore + ")");
        System.out.println("Triggered rules: " + triggeredRules.size());
    }
    
    /**
     * Determines risk level based on score
     */
    private String determineRiskLevel(double score) {
        if (score < 20) return "LOW";
        if (score < 50) return "MEDIUM";
        if (score < 80) return "HIGH";
        return "CRITICAL";
    }
    
    /**
     * Creates a normal transaction with no suspicious characteristics
     */
    private Transaction createNormalTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAccountId("ACC1234");
        transaction.setAmount(new BigDecimal("50.00"));
        transaction.setCurrency("USD");
        transaction.setMerchantId("MCH001");
        transaction.setMerchantName("Coffee Shop");
        transaction.setMerchantCategory("FOOD");
        transaction.setTimestamp(Instant.now());
        transaction.setLocation(new GeoLocation(40.7128, -74.0060));
        transaction.setChannel("ONLINE");
        transaction.setDeviceInfo(new DeviceInfo("Chrome", "Windows", "192.168.1.1"));
        transaction.setType(TransactionType.PURCHASE);
        transaction.setReceivedAt(Instant.now());
        transaction.setTransactionReference("TX" + System.currentTimeMillis());
        return transaction;
    }
    
    /**
     * Creates a transaction with some suspicious characteristics
     */
    private Transaction createSuspiciousTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAccountId("ACC1234");
        transaction.setAmount(new BigDecimal("1500.00")); // High value
        transaction.setCurrency("USD");
        transaction.setMerchantId("MCH002");
        transaction.setMerchantName("Electronics Store");
        transaction.setMerchantCategory("RETAIL");
        transaction.setTimestamp(Instant.now());
        transaction.setLocation(new GeoLocation(40.7128, -74.0060));
        transaction.setChannel("ONLINE");
        transaction.setDeviceInfo(new DeviceInfo("Chrome", "Windows", "192.168.1.1"));
        transaction.setType(TransactionType.PURCHASE);
        transaction.setReceivedAt(Instant.now());
        transaction.setTransactionReference("TX" + System.currentTimeMillis());
        return transaction;
    }
    
    /**
     * Creates a transaction with multiple fraud indicators
     */
    private Transaction createFraudulentTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setAccountId("ACC1234");
        transaction.setAmount(new BigDecimal("3500.00")); // Very high value
        transaction.setCurrency("USD");
        transaction.setMerchantId("MCH003");
        transaction.setMerchantName("Rare Collectibles");
        transaction.setMerchantCategory("LUXURY");
        transaction.setTimestamp(Instant.now());
        transaction.setLocation(new GeoLocation(35.6762, 139.6503)); // Tokyo, unusual location
        transaction.setChannel("ONLINE");
        transaction.setDeviceInfo(new DeviceInfo("Unknown", "Android", "87.65.43.21")); // Unusual device
        transaction.setType(TransactionType.PURCHASE);
        transaction.setReceivedAt(Instant.now());
        transaction.setTransactionReference("TX" + System.currentTimeMillis());
        return transaction;
    }
    
    /**
     * Rule that detects high-value transactions
     */
    private class HighValueTransactionRule extends BaseRule {
        private final BigDecimal threshold = new BigDecimal("1000.00");
        
        public HighValueTransactionRule() {
            super(
                "HIGH_VALUE_TRANSACTION",
                "High Value Transaction",
                "Detects transactions with amounts exceeding a specified threshold",
                "AMOUNT",
                70.0 // Severity out of 100
            );
        }
        
        @Override
        public boolean evaluate(Transaction transaction) {
            if (transaction == null || transaction.getAmount() == null) {
                return false;
            }
            return transaction.getAmount().compareTo(threshold) > 0;
        }
        
        @Override
        public String generateTriggerReason(Transaction transaction) {
            return String.format(
                "Transaction amount %s %s exceeds threshold of %s USD",
                transaction.getAmount(),
                transaction.getCurrency(),
                threshold
            );
        }
    }
    
    /**
     * Rule that detects transactions from unusual locations
     */
    private class UnusualLocationRule extends BaseRule {
        private final GeoLocation homeLocation = new GeoLocation(40.7128, -74.0060); // NYC
        private final double maxDistanceKm = 1000.0; // Max distance in kilometers
        
        public UnusualLocationRule() {
            super(
                "UNUSUAL_LOCATION",
                "Unusual Location",
                "Detects transactions occurring far from the customer's normal location",
                "LOCATION",
                80.0 // Severity out of 100
            );
        }
        
        @Override
        public boolean evaluate(Transaction transaction) {
            if (transaction == null || transaction.getLocation() == null) {
                return false;
            }
            double distance = homeLocation.distanceFrom(transaction.getLocation());
            return distance > maxDistanceKm;
        }
        
        @Override
        public String generateTriggerReason(Transaction transaction) {
            double distance = homeLocation.distanceFrom(transaction.getLocation());
            return String.format(
                "Transaction location is %.2f km from customer's normal location",
                distance
            );
        }
    }
}

// Add support classes

class DeviceInfo {
    private String browser;
    private String os;
    private String ipAddress;
    
    public DeviceInfo(String browser, String os, String ipAddress) {
        this.browser = browser;
        this.os = os;
        this.ipAddress = ipAddress;
    }
    
    public String getBrowser() { return browser; }
    public String getOs() { return os; }
    public String getIpAddress() { return ipAddress; }
}

class GeoLocation {
    private double latitude;
    private double longitude;
    
    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    
    public double distanceFrom(GeoLocation other) {
        return Math.sqrt(
            Math.pow(this.latitude - other.latitude, 2) + 
            Math.pow(this.longitude - other.longitude, 2)
        ) * 111.0; // Rough conversion to kilometers
    }
}

class Transaction {
    private UUID id;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String merchantId;
    private String merchantName;
    private String merchantCategory;
    private Instant timestamp;
    private GeoLocation location;
    private String channel;
    private DeviceInfo deviceInfo;
    private TransactionType type;
    private Instant receivedAt;
    private String transactionReference;
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    
    public String getMerchantCategory() { return merchantCategory; }
    public void setMerchantCategory(String merchantCategory) { this.merchantCategory = merchantCategory; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public GeoLocation getLocation() { return location; }
    public void setLocation(GeoLocation location) { this.location = location; }
    
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    
    public DeviceInfo getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(DeviceInfo deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    
    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }
    
    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String ref) { this.transactionReference = ref; }
}

enum TransactionType {
    PURCHASE, WITHDRAWAL, DEPOSIT, TRANSFER
}

class TriggeredRule {
    private String ruleId;
    private double severity;
    private String reason;
    
    public TriggeredRule(String ruleId, double severity, String reason) {
        this.ruleId = ruleId;
        this.severity = severity;
        this.reason = reason;
    }
    
    public double getSeverity() {
        return severity;
    }
    
    public String getRuleId() { return ruleId; }
    public String getReason() { return reason; }
}

interface Rule {
    String getId();
    String getName();
    boolean evaluate(Transaction transaction);
    String generateTriggerReason(Transaction transaction);
    TriggeredRule createTriggeredRule(Transaction transaction);
}

abstract class BaseRule implements Rule {
    private final String id;
    private final String name;
    private final String description;
    private final String category;
    private final double severity;
    
    public BaseRule(String id, String name, String description, String category, double severity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.severity = severity;
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
    public TriggeredRule createTriggeredRule(Transaction transaction) {
        if (evaluate(transaction)) {
            return new TriggeredRule(
                id,
                severity,
                generateTriggerReason(transaction)
            );
        }
        return null;
    }
}
