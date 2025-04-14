package com.frauddetection.analytics.repository;

import com.frauddetection.analytics.model.TransactionAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionAnalyticsRepository extends JpaRepository<TransactionAnalytics, Long> {

    List<TransactionAnalytics> findByIsFraudulent(Boolean isFraudulent);

    @Query("SELECT ta FROM TransactionAnalytics ta WHERE ta.timestamp BETWEEN :startDate AND :endDate")
    List<TransactionAnalytics> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT ta.merchantCategory, COUNT(ta) as count, SUM(CASE WHEN ta.isFraudulent = true THEN 1 ELSE 0 END) as fraudCount " +
           "FROM TransactionAnalytics ta " +
           "GROUP BY ta.merchantCategory")
    List<Object[]> getFraudStatsByMerchantCategory();

    @Query("SELECT ta.location, COUNT(ta) as count, SUM(CASE WHEN ta.isFraudulent = true THEN 1 ELSE 0 END) as fraudCount " +
           "FROM TransactionAnalytics ta " +
           "GROUP BY ta.location")
    List<Object[]> getFraudStatsByLocation();

    @Query("SELECT ta.detectionMethod, COUNT(ta) as count " +
           "FROM TransactionAnalytics ta " +
           "WHERE ta.isFraudulent = true " +
           "GROUP BY ta.detectionMethod")
    List<Object[]> getFraudDetectionMethodStats();
}