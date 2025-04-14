package com.frauddetection.analytics.service;

import com.frauddetection.analytics.model.TransactionAnalytics;
import com.frauddetection.analytics.repository.TransactionAnalyticsRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionAnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionAnalyticsService.class);

    private final TransactionAnalyticsRepository repository;
    private final MeterRegistry meterRegistry;
    private final Timer analyticsProcessingTimer;

    public TransactionAnalyticsService(TransactionAnalyticsRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;
        this.analyticsProcessingTimer = Timer.builder("analytics.processing.time")
                .description("Time taken to process analytics requests")
                .register(meterRegistry);
    }

    public TransactionAnalytics saveAnalytics(TransactionAnalytics analytics) {
        logger.info("Saving transaction analytics for merchant category: {}", analytics.getMerchantCategory());
        meterRegistry.counter("analytics.transactions.total").increment();
        if (analytics.getIsFraudulent()) {
            meterRegistry.counter("analytics.transactions.fraudulent").increment();
        }
        return repository.save(analytics);
    }

    public Map<String, Object> getFraudStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return analyticsProcessingTimer.record(() -> {
            logger.info("Generating fraud statistics for period: {} to {}", startDate, endDate);
            Map<String, Object> statistics = new HashMap<>();

            List<TransactionAnalytics> transactions = repository.findByDateRange(startDate, endDate);
            statistics.put("totalTransactions", transactions.size());

            List<Object[]> merchantStats = repository.getFraudStatsByMerchantCategory();
            statistics.put("merchantCategoryStats", merchantStats);

            List<Object[]> locationStats = repository.getFraudStatsByLocation();
            statistics.put("locationStats", locationStats);

            List<Object[]> detectionStats = repository.getFraudDetectionMethodStats();
            statistics.put("detectionMethodStats", detectionStats);

            return statistics;
        });
    }

    public List<TransactionAnalytics> getFraudulentTransactions() {
        logger.info("Retrieving fraudulent transactions");
        return repository.findByIsFraudulent(true);
    }

    public Map<String, Object> getMerchantCategoryRiskScores() {
        logger.info("Calculating merchant category risk scores");
        List<Object[]> stats = repository.getFraudStatsByMerchantCategory();
        Map<String, Object> riskScores = new HashMap<>();

        for (Object[] stat : stats) {
            String category = (String) stat[0];
            Long total = (Long) stat[1];
            Long fraudulent = (Long) stat[2];
            double riskScore = total > 0 ? (fraudulent.doubleValue() / total.doubleValue()) : 0.0;
            riskScores.put(category, riskScore);

            // Record merchant category metrics
            meterRegistry.gauge("analytics.merchant.risk.score." + category, riskScore);
        }

        return riskScores;
    }
}