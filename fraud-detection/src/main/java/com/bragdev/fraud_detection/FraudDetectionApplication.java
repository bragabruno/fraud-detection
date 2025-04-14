package com.bragdev.fraud_detection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.bragdev.fraud_detection.model.Transaction;
import com.bragdev.fraud_detection.repository.TransactionRepository;

@SpringBootApplication
public class FraudDetectionApplication {

 public static void main(String[] args) {
  SpringApplication.run(FraudDetectionApplication.class, args);
 }

 @Bean
 CommandLineRunner initDatabase(TransactionRepository repository) {
  return args -> {
   // Create some sample transactions
   repository.save(Transaction.builder()
     .cardNumber("4111111111111111")
     .amount(new BigDecimal("50.00"))
     .merchantName("Amazon")
     .merchantCategory("Retail")
     .timestamp(LocalDateTime.now().minusDays(1))
     .location("Online")
     .status(Transaction.TransactionStatus.COMPLETED)
     .isFraudulent(false)
     .build());

   repository.save(Transaction.builder()
     .cardNumber("4111111111111111")
     .amount(new BigDecimal("1500.00"))
     .merchantName("Best Buy")
     .merchantCategory("Electronics")
     .timestamp(LocalDateTime.now().minusHours(3))
     .location("New York, NY")
     .status(Transaction.TransactionStatus.FLAGGED)
     .isFraudulent(true)
     .build());

   repository.save(Transaction.builder()
     .cardNumber("5555555555554444")
     .amount(new BigDecimal("25.75"))
     .merchantName("Starbucks")
     .merchantCategory("Food")
     .timestamp(LocalDateTime.now().minusHours(2))
     .location("San Francisco, CA")
     .status(Transaction.TransactionStatus.COMPLETED)
     .isFraudulent(false)
     .build());
  };
 }
}
