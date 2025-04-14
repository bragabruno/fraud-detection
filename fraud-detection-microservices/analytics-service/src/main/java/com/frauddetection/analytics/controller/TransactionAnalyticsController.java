package com.frauddetection.analytics.controller;

import com.frauddetection.analytics.model.TransactionAnalytics;
import com.frauddetection.analytics.service.TransactionAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Transaction Analytics", description = "API endpoints for transaction analytics and fraud statistics")
public class TransactionAnalyticsController {

    private final TransactionAnalyticsService analyticsService;

    public TransactionAnalyticsController(TransactionAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @PostMapping("/transactions")
    @Operation(summary = "Save transaction analytics data")
    public ResponseEntity<TransactionAnalytics> saveAnalytics(@RequestBody TransactionAnalytics analytics) {
        return ResponseEntity.ok(analyticsService.saveAnalytics(analytics));
    }

    @GetMapping("/fraud/statistics")
    @Operation(summary = "Get fraud statistics for a specific date range")
    public ResponseEntity<Map<String, Object>> getFraudStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(analyticsService.getFraudStatistics(startDate, endDate));
    }

    @GetMapping("/fraud/transactions")
    @Operation(summary = "Get list of fraudulent transactions")
    public ResponseEntity<List<TransactionAnalytics>> getFraudulentTransactions() {
        return ResponseEntity.ok(analyticsService.getFraudulentTransactions());
    }

    @GetMapping("/merchant/risk-scores")
    @Operation(summary = "Get risk scores by merchant category")
    public ResponseEntity<Map<String, Object>> getMerchantCategoryRiskScores() {
        return ResponseEntity.ok(analyticsService.getMerchantCategoryRiskScores());
    }

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}