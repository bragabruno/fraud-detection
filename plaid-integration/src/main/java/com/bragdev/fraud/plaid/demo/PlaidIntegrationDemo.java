package com.bragdev.fraud.plaid.demo;

import com.bragdev.fraud.core.model.RiskScore;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.core.rule.Rule;
import com.bragdev.fraud.plaid.adapter.PlaidTransactionAdapter;
import com.bragdev.fraud.plaid.model.PlaidTransaction;
import com.bragdev.fraud.plaid.service.PlaidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstration of the Plaid integration with the fraud detection system.
 * This class shows how to fetch transactions from Plaid and analyze them for fraud.
 * 
 * Only active when the "plaid-demo" profile is enabled.
 */
@Configuration
@Profile("plaid-demo")
@RequiredArgsConstructor
@Slf4j
public class PlaidIntegrationDemo {

    private final PlaidService plaidService;
    private final PlaidTransactionAdapter transactionAdapter;
    
    @Value("${demo.plaid.access-token:access-sandbox-12345}")
    private String demoAccessToken;
    
    /**
     * CommandLineRunner that demonstrates the Plaid integration functionality
     */
    @Bean
    public CommandLineRunner demonstratePlaidIntegration(List<Rule> fraudRules) {
        return args -> {
            System.out.println("\n===== Plaid Integration Demonstration =====\n");
            
            // Step 1: Define the date range for transactions
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30); // Look at the last 30 days
            
            System.out.println("Fetching transactions from Plaid from " + startDate + " to " + endDate);
            System.out.println("Using demo access token: " + demoAccessToken);
            
            try {
                // Step 2: Fetch transactions from Plaid
                List<PlaidTransaction> plaidTransactions = plaidService.getTransactions(
                    demoAccessToken, startDate, endDate);
                
                System.out.println("\nRetrieved " + plaidTransactions.size() + " transactions from Plaid");
                
                // Step 3: Convert Plaid transactions to fraud detection system format
                List<Transaction> fraudDetectionTransactions = new ArrayList<>();
                for (PlaidTransaction plaidTx : plaidTransactions) {
                    Transaction coreTx = transactionAdapter.adaptToCoreTransaction(plaidTx);
                    fraudDetectionTransactions.add(coreTx);
                    
                    System.out.println("  - " + coreTx.getAmount() + " " + coreTx.getCurrency() + 
                        " at " + coreTx.getMerchantName());
                }
                
                System.out.println("\nAnalyzing transactions for fraud patterns...");
                
                // Step 4: Run fraud detection rules on each transaction
                for (Transaction transaction : fraudDetectionTransactions) {
                    System.out.println("\nAnalyzing Transaction: " + transaction.getAmount() + " " +
                        transaction.getCurrency() + " at " + transaction.getMerchantName());
                    
                    List<TriggeredRule> triggeredRules = new ArrayList<>();
                    
                    // Apply each rule and collect triggered ones
                    for (Rule rule : fraudRules) {
                        TriggeredRule triggered = rule.createTriggeredRule(transaction);
                        if (triggered != null) {
                            triggeredRules.add(triggered);
                            System.out.println("  Rule triggered: " + rule.getName() + " - " +
                                rule.generateTriggerReason(transaction));
                        }
                    }
                    
                    // Calculate risk score
                    RiskScore riskScore = calculateRiskScore(triggeredRules);
                    
                    System.out.println("  Risk assessment: " + riskScore.getLevel() + 
                        " (" + riskScore.getScore() + ")");
                    System.out.println("  Triggered rules: " + triggeredRules.size());
                }
                
            } catch (Exception e) {
                System.err.println("\nError in Plaid integration demo: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("\n===== Demonstration Complete =====\n");
        };
    }
    
    /**
     * Calculates an overall risk score based on triggered rules
     */
    private RiskScore calculateRiskScore(List<TriggeredRule> triggeredRules) {
        double score = 0;
        
        if (!triggeredRules.isEmpty()) {
            double totalSeverity = 0;
            for (TriggeredRule rule : triggeredRules) {
                totalSeverity += rule.getSeverity();
            }
            score = totalSeverity / triggeredRules.size();
        }
        
        String level;
        if (score < 20) level = "LOW";
        else if (score < 50) level = "MEDIUM";
        else if (score < 80) level = "HIGH";
        else level = "CRITICAL";
        
        return new RiskScore(score, level);
    }
}