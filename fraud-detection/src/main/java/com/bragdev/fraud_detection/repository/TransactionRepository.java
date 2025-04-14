package com.bragdev.fraud_detection.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bragdev.fraud_detection.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByCardNumberAndTimestampBetween(String cardNumber, LocalDateTime start, LocalDateTime end);
    
    List<Transaction> findByIsFraudulent(Boolean isFraudulent);
    
    List<Transaction> findByCardNumber(String cardNumber);
}