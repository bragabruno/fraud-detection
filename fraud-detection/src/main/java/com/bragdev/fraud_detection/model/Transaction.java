package com.bragdev.fraud_detection.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String cardNumber;
    
    private BigDecimal amount;
    
    private String merchantName;
    
    private String merchantCategory;
    
    private LocalDateTime timestamp;
    
    private String location;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    private Boolean isFraudulent;
    
    public enum TransactionStatus {
        PENDING, COMPLETED, DECLINED, FLAGGED
    }
}