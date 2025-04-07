package com.bragdev.fraud.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TransactionDtoTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String id = "123e4567-e89b-12d3-a456-426614174000";
        String accountId = "ACC123";
        BigDecimal amount = new BigDecimal("100.50");
        String currency = "USD";
        String merchantId = "MERCH456";
        String merchantName = "Test Merchant";
        String merchantCategory = "Retail";
        Instant timestamp = Instant.now();
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        String channel = "ONLINE";
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setBrowser("Chrome");
        deviceInfo.setOperatingSystem("Windows");
        deviceInfo.setIpAddress("192.168.1.1");
        String type = "PURCHASE";
        Map<String, Object> additionalAttributes = new HashMap<>();
        additionalAttributes.put("key1", "value1");
        
        // Act
        TransactionDto transaction = new TransactionDto();
        transaction.setId(id);
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setMerchantId(merchantId);
        transaction.setMerchantName(merchantName);
        transaction.setMerchantCategory(merchantCategory);
        transaction.setTimestamp(timestamp);
        transaction.setLocation(location);
        transaction.setChannel(channel);
        transaction.setDeviceInfo(deviceInfo);
        transaction.setType(type);
        transaction.setAdditionalAttributes(additionalAttributes);
        
        // Assert
        assertEquals(id, transaction.getId());
        assertEquals(accountId, transaction.getAccountId());
        assertEquals(amount, transaction.getAmount());
        assertEquals(currency, transaction.getCurrency());
        assertEquals(merchantId, transaction.getMerchantId());
        assertEquals(merchantName, transaction.getMerchantName());
        assertEquals(merchantCategory, transaction.getMerchantCategory());
        assertEquals(timestamp, transaction.getTimestamp());
        assertEquals(location, transaction.getLocation());
        assertEquals(channel, transaction.getChannel());
        assertEquals(deviceInfo, transaction.getDeviceInfo());
        assertEquals(type, transaction.getType());
        assertEquals(additionalAttributes, transaction.getAdditionalAttributes());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        TransactionDto transaction1 = new TransactionDto();
        transaction1.setId("123e4567-e89b-12d3-a456-426614174000");
        transaction1.setAccountId("ACC123");
        transaction1.setAmount(new BigDecimal("100.50"));
        
        TransactionDto transaction2 = new TransactionDto();
        transaction2.setId("123e4567-e89b-12d3-a456-426614174000");
        transaction2.setAccountId("ACC123");
        transaction2.setAmount(new BigDecimal("100.50"));
        
        TransactionDto transaction3 = new TransactionDto();
        transaction3.setId("223e4567-e89b-12d3-a456-426614174000");
        transaction3.setAccountId("ACC456");
        transaction3.setAmount(new BigDecimal("200.75"));
        
        // Assert
        assertEquals(transaction1, transaction2);
        assertEquals(transaction1.hashCode(), transaction2.hashCode());
        
        assertNotEquals(transaction1, transaction3);
        assertNotEquals(transaction1.hashCode(), transaction3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        TransactionDto transaction = new TransactionDto();
        transaction.setId("123e4567-e89b-12d3-a456-426614174000");
        transaction.setAccountId("ACC123");
        transaction.setAmount(new BigDecimal("100.50"));
        
        // Act
        String toString = transaction.toString();
        
        // Assert
        assertTrue(toString.contains("123e4567-e89b-12d3-a456-426614174000"));
        assertTrue(toString.contains("ACC123"));
        assertTrue(toString.contains("100.50"));
    }
    
    @Test
    public void testNestedObjects() {
        // Arrange
        TransactionDto transaction = new TransactionDto();
        
        GeoLocationDto location = new GeoLocationDto();
        location.setLatitude(37.7749);
        location.setLongitude(-122.4194);
        
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setBrowser("Chrome");
        deviceInfo.setOperatingSystem("Windows");
        deviceInfo.setIpAddress("192.168.1.1");
        
        // Act
        transaction.setLocation(location);
        transaction.setDeviceInfo(deviceInfo);
        
        // Assert
        assertEquals(location, transaction.getLocation());
        assertEquals(deviceInfo, transaction.getDeviceInfo());
        
        // Verify nested properties
        assertEquals(37.7749, transaction.getLocation().getLatitude(), 0.0001);
        assertEquals(-122.4194, transaction.getLocation().getLongitude(), 0.0001);
        assertEquals("Chrome", transaction.getDeviceInfo().getBrowser());
        assertEquals("Windows", transaction.getDeviceInfo().getOperatingSystem());
        assertEquals("192.168.1.1", transaction.getDeviceInfo().getIpAddress());
    }
}