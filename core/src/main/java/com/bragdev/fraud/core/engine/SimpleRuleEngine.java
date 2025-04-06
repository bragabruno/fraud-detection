package com.bragdev.fraud.core.engine;

import com.bragdev.fraud.core.model.RiskLevel;
import com.bragdev.fraud.core.model.RiskScore;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import com.bragdev.fraud.core.rule.Rule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * A simple implementation of the RuleEngine interface.
 * Evaluates transactions against a collection of rules and generates a risk score.
 */
public class SimpleRuleEngine implements RuleEngine {
    
    private final List<Rule> rules = new CopyOnWriteArrayList<>();
    
    @Override
    public void addRule(Rule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }
    
    @Override
    public void addRules(List<Rule> rulesToAdd) {
        if (rulesToAdd != null) {
            rules.addAll(rulesToAdd);
        }
    }
    
    @Override
    public void removeRule(String ruleId) {
        rules.removeIf(rule -> rule.getId().equals(ruleId));
    }
    
    @Override
    public List<Rule> getRules() {
        return new ArrayList<>(rules);
    }
    
    @Override
    public RiskScore evaluate(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        // Collect triggered rules
        List<TriggeredRule> triggeredRules = rules.stream()
                .map(rule -> rule.createTriggeredRule(transaction))
                .filter(rule -> rule != null)
                .collect(Collectors.toList());
        
        // Calculate component scores based on rule categories
        Map<String, Double> componentScores = calculateComponentScores(triggeredRules);
        
        // Calculate overall score based on triggered rules
        double overallScore = calculateOverallScore(triggeredRules);
        
        // Build and return the risk score
        RiskScore riskScore = new RiskScore();
        riskScore.setId(UUID.randomUUID());
        riskScore.setTransactionId(transaction.getId());
        riskScore.setOverallScore(overallScore);
        riskScore.setComponentScores(componentScores);
        riskScore.setTriggeredRules(triggeredRules);
        riskScore.setConfidenceLevel(calculateConfidence(triggeredRules));
        riskScore.setEvaluatedAt(Instant.now());
        riskScore.setEvaluatedBy(getEngineName());
        
        // Calculate and set risk level
        riskScore.updateRiskLevel();
        
        return riskScore;
    }
    
    @Override
    public String getEngineName() {
        return "Simple Rule Engine";
    }
    
    /**
     * Calculates component scores based on triggered rules
     */
    private Map<String, Double> calculateComponentScores(List<TriggeredRule> triggeredRules) {
        Map<String, List<TriggeredRule>> rulesByCategory = triggeredRules.stream()
                .filter(rule -> rule.getCategory() != null)
                .collect(Collectors.groupingBy(TriggeredRule::getCategory));
        
        Map<String, Double> componentScores = new HashMap<>();
        
        for (Map.Entry<String, List<TriggeredRule>> entry : rulesByCategory.entrySet()) {
            // Calculate average severity for each category
            double categoryScore = entry.getValue().stream()
                    .mapToDouble(TriggeredRule::getSeverity)
                    .average()
                    .orElse(0.0);
            
            componentScores.put(entry.getKey(), categoryScore);
        }
        
        return componentScores;
    }
    
    /**
     * Calculates overall score based on triggered rules
     */
    private double calculateOverallScore(List<TriggeredRule> triggeredRules) {
        if (triggeredRules.isEmpty()) {
            return 0.0;
        }
        
        // Calculate weighted average of rule severities
        double totalSeverity = triggeredRules.stream()
                .mapToDouble(rule -> rule.getSeverity() * rule.getConfidence())
                .sum();
        
        double totalWeight = triggeredRules.stream()
                .mapToDouble(TriggeredRule::getConfidence)
                .sum();
        
        return totalWeight > 0 ? totalSeverity / totalWeight : 0.0;
    }
    
    /**
     * Calculates confidence level for the risk assessment
     */
    private double calculateConfidence(List<TriggeredRule> triggeredRules) {
        if (triggeredRules.isEmpty()) {
            return 1.0; // High confidence in a "no risk" assessment
        }
        
        // Average confidence across all triggered rules
        return triggeredRules.stream()
                .mapToDouble(TriggeredRule::getConfidence)
                .average()
                .orElse(0.5);
    }
}