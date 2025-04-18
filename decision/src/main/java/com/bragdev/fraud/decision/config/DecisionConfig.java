package com.bragdev.fraud.decision.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "fraud.decision")
public class DecisionConfig {
    private Map<String, Double> methodWeights = new HashMap<>();
    private StatisticalAnalysisConfig statisticalAnalysis = new StatisticalAnalysisConfig();
    private MLModelConfig mlModel = new MLModelConfig();
    private RiskThresholds riskThresholds = new RiskThresholds();

    public static class StatisticalAnalysisConfig {
        private int windowSize = 1000;
        private double zScoreThreshold = 2.5;
        private int minSampleSize = 100;

        public int getWindowSize() { return windowSize; }
        public void setWindowSize(int windowSize) { this.windowSize = windowSize; }
        public double getZScoreThreshold() { return zScoreThreshold; }
        public void setZScoreThreshold(double zScoreThreshold) { this.zScoreThreshold = zScoreThreshold; }
        public int getMinSampleSize() { return minSampleSize; }
        public void setMinSampleSize(int minSampleSize) { this.minSampleSize = minSampleSize; }
    }

    public static class MLModelConfig {
        private String modelEndpoint;
        private int batchSize = 100;
        private double confidenceThreshold = 0.8;
        private int timeoutSeconds = 5;

        public String getModelEndpoint() { return modelEndpoint; }
        public void setModelEndpoint(String modelEndpoint) { this.modelEndpoint = modelEndpoint; }
        public int getBatchSize() { return batchSize; }
        public void setBatchSize(int batchSize) { this.batchSize = batchSize; }
        public double getConfidenceThreshold() { return confidenceThreshold; }
        public void setConfidenceThreshold(double confidenceThreshold) { this.confidenceThreshold = confidenceThreshold; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    }

    public static class RiskThresholds {
        private double lowRisk = 20.0;
        private double mediumRisk = 50.0;
        private double highRisk = 80.0;

        public double getLowRisk() { return lowRisk; }
        public void setLowRisk(double lowRisk) { this.lowRisk = lowRisk; }
        public double getMediumRisk() { return mediumRisk; }
        public void setMediumRisk(double mediumRisk) { this.mediumRisk = mediumRisk; }
        public double getHighRisk() { return highRisk; }
        public void setHighRisk(double highRisk) { this.highRisk = highRisk; }
    }

    // Getters and setters
    public Map<String, Double> getMethodWeights() { return methodWeights; }
    public void setMethodWeights(Map<String, Double> methodWeights) { this.methodWeights = methodWeights; }
    public StatisticalAnalysisConfig getStatisticalAnalysis() { return statisticalAnalysis; }
    public void setStatisticalAnalysis(StatisticalAnalysisConfig statisticalAnalysis) { this.statisticalAnalysis = statisticalAnalysis; }
    public MLModelConfig getMlModel() { return mlModel; }
    public void setMlModel(MLModelConfig mlModel) { this.mlModel = mlModel; }
    public RiskThresholds getRiskThresholds() { return riskThresholds; }
    public void setRiskThresholds(RiskThresholds riskThresholds) { this.riskThresholds = riskThresholds; }
}