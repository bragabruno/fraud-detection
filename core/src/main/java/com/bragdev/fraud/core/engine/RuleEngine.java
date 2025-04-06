package com.bragdev.fraud.core.engine;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.RiskScore;
import com.bragdev.fraud.core.rule.Rule;

import java.util.List;

/**
 * Interface for the rule-based fraud detection engine.
 * Evaluates transactions against a set of fraud detection rules.
 */
public interface RuleEngine {
    
    /**
     * Adds a rule to the engine
     * @param rule The rule to add
     */
    void addRule(Rule rule);
    
    /**
     * Adds multiple rules to the engine
     * @param rules The rules to add
     */
    void addRules(List<Rule> rules);
    
    /**
     * Removes a rule from the engine
     * @param ruleId The ID of the rule to remove
     */
    void removeRule(String ruleId);
    
    /**
     * Gets all rules registered with the engine
     * @return A list of all rules
     */
    List<Rule> getRules();
    
    /**
     * Evaluates a transaction against all registered rules
     * @param transaction The transaction to evaluate
     * @return A RiskScore containing the evaluation results
     */
    RiskScore evaluate(Transaction transaction);
    
    /**
     * Returns the name of this rule engine implementation
     * @return The engine name
     */
    String getEngineName();
}