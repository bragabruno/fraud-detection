package com.bragdev.fraud.decision;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bragdev.fraud.core.model.RiskLevel;
import com.bragdev.fraud.core.model.RiskScore;
import com.bragdev.fraud.core.model.Transaction;

/**
 * Decision Manager responsible for combining risk scores from different detection methods
 * and making final fraud/non-fraud decisions.
 */
@Service
public class DecisionManager {

    @Autowired
    private StatisticalAnalysisService statisticalAnalysisService;
    
    @Autowired
    private MLModelIntegrationService mlModelService;
    
    // Sliding window for statistical analysis
    private final ConcurrentLinkedQueue<Double> transactionScores = new ConcurrentLinkedQueue<>();
    private static final int WINDOW_SIZE = 1000;
    
    // Configuration for risk thresholds
    private double lowRiskThreshold = 20.0;
    private double mediumRiskThreshold = 50.0;
    private double highRiskThreshold = 80.0;
    
    // Weights for different detection methods with configurable values
    private final Map<String, Double> methodWeights = new HashMap<>();
    
    /**
     * Creates a decision manager with default weights
     */
    public DecisionManager() {
        // Set default weights for different detection methods
        methodWeights.put("RULE_ENGINE", 0.5);
        methodWeights.put("STATISTICAL", 0.2);
        methodWeights.put("MACHINE_LEARNING", 0.2);
        methodWeights.put("NETWORK_ANALYSIS", 0.1);
    }
    
    /**
     * Updates the weight for a specific detection method
     * 
     * @param methodName The name of the detection method
     * @param weight The weight to assign (0.0 to 1.0)
     */
    public void setMethodWeight(String methodName, double weight) {
        if (weight < 0.0 || weight > 1.0) {
            throw new IllegalArgumentException("Weight must be between 0.0 and 1.0");
        }
        methodWeights.put(methodName, weight);
        normalizeWeights();
    }
    
    /**
     * Normalizes the weights to ensure they sum to 1.0
     */
    private void normalizeWeights() {
        double sum = methodWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum > 0) {
            methodWeights.forEach((key, value) -> methodWeights.put(key, value / sum));
        }
    }
    
    /**
     * Makes a decision by combining risk scores from different detection methods
     * 
     * @param transaction The transaction to evaluate
     * @param scores Risk scores from different detection methods
     * @return A combined risk score with final decision
     */
    public RiskScore makeDecision(Transaction transaction, Map<String, RiskScore> scores) {
        if (transaction == null || scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException("Transaction and scores cannot be null or empty");
        }
        
        // Get ML model prediction
        double mlPrediction = mlModelService.predictRisk(transaction);
        scores.put("MACHINE_LEARNING", createRiskScore(transaction.getId(), mlPrediction));
        
        // Calculate weighted average score
        double overallScore = calculateWeightedScore(scores);
        
        // Add to statistical window
        updateStatisticalWindow(overallScore);
        
        // Get statistical anomaly score
        double statisticalScore = statisticalAnalysisService.calculateAnomalyScore(
            transactionScores.stream().mapToDouble(Double::doubleValue).toArray(),
            overallScore
        );
        scores.put("STATISTICAL", createRiskScore(transaction.getId(), statisticalScore));
        
        // Recalculate final score including statistical analysis
        overallScore = calculateWeightedScore(scores);
        
        // Create the final risk score
        RiskScore finalScore = new RiskScore();
        finalScore.setId(UUID.randomUUID());
        finalScore.setTransactionId(transaction.getId());
        finalScore.setOverallScore(overallScore);
        finalScore.setEvaluatedAt(Instant.now());
        finalScore.setEvaluatedBy("Decision Manager");
        
        // Set component scores
        Map<String, Double> componentScores = new HashMap<>();
        scores.forEach((methodName, score) -> {
            if (score != null) {
                componentScores.put(methodName, score.getOverallScore());
            }
        });
        finalScore.setComponentScores(componentScores);
        
        // Set risk level based on overall score
        RiskLevel riskLevel;
        if (overallScore < lowRiskThreshold) {
            riskLevel = RiskLevel.LOW;
        } else if (overallScore < mediumRiskThreshold) {
            riskLevel = RiskLevel.MEDIUM;
        } else if (overallScore < highRiskThreshold) {
            riskLevel = RiskLevel.HIGH;
        } else {
            riskLevel = RiskLevel.CRITICAL;
        }
        finalScore.setRiskLevel(riskLevel);
        
        return finalScore;
    }
    
    /**
     * Sets the risk thresholds
     * 
     * @param lowRiskThreshold Threshold for low risk (0-100)
     * @param mediumRiskThreshold Threshold for medium risk (0-100)
     * @param highRiskThreshold Threshold for high risk (0-100)
     */
    public void setRiskThresholds(double lowRiskThreshold, double mediumRiskThreshold, double highRiskThreshold) {
        if (lowRiskThreshold >= mediumRiskThreshold || mediumRiskThreshold >= highRiskThreshold) {
            throw new IllegalArgumentException("Thresholds must be in ascending order");
        }
        
        if (lowRiskThreshold < 0 || highRiskThreshold > 100) {
            throw new IllegalArgumentException("Thresholds must be between 0 and 100");
        }
        
        this.lowRiskThreshold = lowRiskThreshold;
        this.mediumRiskThreshold = mediumRiskThreshold;
        this.highRiskThreshold = highRiskThreshold;
    }
    
    private void updateStatisticalWindow(double score) {
        transactionScores.offer(score);
        while (transactionScores.size() > WINDOW_SIZE) {
            transactionScores.poll();
        }
    }
    
    private double calculateWeightedScore(Map<String, RiskScore> scores) {
        double overallScore = 0.0;
        double totalWeight = 0.0;
        
        for (Map.Entry<String, RiskScore> entry : scores.entrySet()) {
            String methodName = entry.getKey();
            RiskScore score = entry.getValue();
            
            if (score != null) {
                double weight = methodWeights.getOrDefault(methodName, 0.1);
                overallScore += score.getOverallScore() * weight;
                totalWeight += weight;
            }
        }
        
        return totalWeight > 0 ? overallScore / totalWeight : 0.0;
    }
    
    private RiskScore createRiskScore(UUID transactionId, double score) {
        RiskScore riskScore = new RiskScore();
        riskScore.setId(UUID.randomUUID());
        riskScore.setTransactionId(transactionId);
        riskScore.setOverallScore(score);
        riskScore.setEvaluatedAt(Instant.now());
        return riskScore;
    }
}