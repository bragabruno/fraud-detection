package com.bragdev.fraud.decision.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class StatisticalAnalysisService {
    
    private static final double ZSCORE_THRESHOLD = 2.5;
    
    /**
     * Calculates the anomaly score based on Z-score analysis
     * @param historicalScores Array of historical transaction scores
     * @param currentScore The current transaction score to evaluate
     * @return Normalized anomaly score between 0 and 100
     */
    public double calculateAnomalyScore(double[] historicalScores, double currentScore) {
        if (historicalScores == null || historicalScores.length < 2) {
            return 50.0; // Default score when insufficient data
        }

        // Calculate mean and standard deviation
        double mean = Arrays.stream(historicalScores).average().orElse(0.0);
        double variance = Arrays.stream(historicalScores)
            .map(x -> Math.pow(x - mean, 2))
            .average()
            .orElse(0.0);
        double stdDev = Math.sqrt(variance);

        if (stdDev == 0.0) {
            return currentScore > mean ? 100.0 : 0.0;
        }

        // Calculate Z-score
        double zScore = Math.abs((currentScore - mean) / stdDev);
        
        // Normalize Z-score to 0-100 scale
        double normalizedScore = Math.min((zScore / ZSCORE_THRESHOLD) * 100, 100.0);
        
        return normalizedScore;
    }
    
    /**
     * Calculates running statistics for a transaction window
     * @param scores Array of transaction scores
     * @return Map containing statistics (mean, stdDev, min, max)
     */
    public Statistics calculateStatistics(double[] scores) {
        if (scores == null || scores.length == 0) {
            return new Statistics(0, 0, 0, 0);
        }
        
        double sum = 0.0;
        double min = scores[0];
        double max = scores[0];
        
        for (double score : scores) {
            sum += score;
            min = Math.min(min, score);
            max = Math.max(max, score);
        }
        
        double mean = sum / scores.length;
        double variance = Arrays.stream(scores)
            .map(x -> Math.pow(x - mean, 2))
            .average()
            .orElse(0.0);
            
        return new Statistics(mean, Math.sqrt(variance), min, max);
    }
    
    public static class Statistics {
        private final double mean;
        private final double standardDeviation;
        private final double minimum;
        private final double maximum;
        
        public Statistics(double mean, double standardDeviation, double minimum, double maximum) {
            this.mean = mean;
            this.standardDeviation = standardDeviation;
            this.minimum = minimum;
            this.maximum = maximum;
        }
        
        public double getMean() { return mean; }
        public double getStandardDeviation() { return standardDeviation; }
        public double getMinimum() { return minimum; }
        public double getMaximum() { return maximum; }
    }
}