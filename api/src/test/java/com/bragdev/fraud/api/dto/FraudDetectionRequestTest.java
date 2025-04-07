package com.bragdev.fraud.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FraudDetectionRequestTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        TransactionDto transaction = createSampleTransaction();
        List<TransactionDto> batchTransactions = new ArrayList<>();
        batchTransactions.add(createSampleTransaction());
        batchTransactions.add(createSampleTransaction());
        
        // Act
        FraudDetectionRequest request = new FraudDetectionRequest();
        request.setTransaction(transaction);
        request.setBatchTransactions(batchTransactions);
        
        // Assert
        assertEquals(transaction, request.getTransaction());
        assertEquals(batchTransactions, request.getBatchTransactions());
        assertEquals(2, request.getBatchTransactions().size());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        FraudDetectionRequest request1 = new FraudDetectionRequest();
        request1.setTransaction(createSampleTransaction());
        
        FraudDetectionRequest request2 = new FraudDetectionRequest();
        request2.setTransaction(createSampleTransaction());
        
        FraudDetectionRequest request3 = new FraudDetectionRequest();
        TransactionDto differentTransaction = createSampleTransaction();
        differentTransaction.setAmount(new BigDecimal("999.99"));
        request3.setTransaction(differentTransaction);
        
        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        FraudDetectionRequest request = new FraudDetectionRequest();
        request.setTransaction(createSampleTransaction());
        
        // Act
        String toString = request.toString();
        
        // Assert
        assertTrue(toString.contains("transaction"));
        assertTrue(toString.contains("ACC123"));
    }
    
    @Test
    public void testBatchTransactionsHandling() {
        // Arrange
        FraudDetectionRequest request = new FraudDetectionRequest();
        
        // Act - Test with null batch
        request.setBatchTransactions(null);
        assertNull(request.getBatchTransactions());
        
        // Act - Test with empty batch
        List<TransactionDto> emptyBatch = new ArrayList<>();
        request.setBatchTransactions(emptyBatch);
        assertEquals(emptyBatch, request.getBatchTransactions());
        assertEquals(0, request.getBatchTransactions().size());
        
        // Act - Test with populated batch
        List<TransactionDto> populatedBatch = new ArrayList<>();
        populatedBatch.add(createSampleTransaction());
        populatedBatch.add(createSampleTransaction());
        populatedBatch.add(createSampleTransaction());
        request.setBatchTransactions(populatedBatch);
        
        // Assert
        assertEquals(populatedBatch, request.getBatchTransactions());
        assertEquals(3, request.getBatchTransactions().size());
    }
    
    // Helper method to create a sample transaction
    private TransactionDto createSampleTransaction() {
        TransactionDto transaction = new TransactionDto();
        transaction.setId("123e4567-e89b-12d3-a456-426614174000");
        transaction.setAccountId("ACC123");
        transaction.setAmount(new BigDecimal("100.50"));
        transaction.setCurrency("USD");
        transaction.setMerchantId("MERCH456");
        transaction.setMerchantName("Test Merchant");
        transaction.setMerchantCategory("Retail");
        transaction.setTimestamp(Instant.now());
        
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        transaction.setLocation(location);
        
        transaction.setChannel("ONLINE");
        
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setBrowser("Chrome");
        deviceInfo.setOperatingSystem("Windows");
        deviceInfo.setIpAddress("192.168.1.1");
        transaction.setDeviceInfo(deviceInfo);
        
        transaction.setType("PURCHASE");
        
        Map<String, Object> additionalAttributes = new HashMap<>();
        additionalAttributes.put("key1", "value1");
        transaction.setAdditionalAttributes(additionalAttributes);
        
        return transaction;
    }
}