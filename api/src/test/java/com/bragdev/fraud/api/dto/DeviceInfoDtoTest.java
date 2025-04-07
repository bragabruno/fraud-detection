package com.bragdev.fraud.api.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeviceInfoDtoTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String browser = "Chrome";
        String operatingSystem = "Windows";
        String ipAddress = "192.168.1.1";
        
        // Act
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setBrowser(browser);
        deviceInfo.setOperatingSystem(operatingSystem);
        deviceInfo.setIpAddress(ipAddress);
        
        // Assert
        assertEquals(browser, deviceInfo.getBrowser());
        assertEquals(operatingSystem, deviceInfo.getOperatingSystem());
        assertEquals(ipAddress, deviceInfo.getIpAddress());
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        DeviceInfoDto deviceInfo1 = new DeviceInfoDto();
        deviceInfo1.setBrowser("Chrome");
        deviceInfo1.setOperatingSystem("Windows");
        deviceInfo1.setIpAddress("192.168.1.1");
        
        DeviceInfoDto deviceInfo2 = new DeviceInfoDto();
        deviceInfo2.setBrowser("Chrome");
        deviceInfo2.setOperatingSystem("Windows");
        deviceInfo2.setIpAddress("192.168.1.1");
        
        DeviceInfoDto deviceInfo3 = new DeviceInfoDto();
        deviceInfo3.setBrowser("Firefox");
        deviceInfo3.setOperatingSystem("Linux");
        deviceInfo3.setIpAddress("10.0.0.1");
        
        // Assert
        assertEquals(deviceInfo1, deviceInfo2);
        assertEquals(deviceInfo1.hashCode(), deviceInfo2.hashCode());
        
        assertNotEquals(deviceInfo1, deviceInfo3);
        assertNotEquals(deviceInfo1.hashCode(), deviceInfo3.hashCode());
    }
    
    @Test
    public void testToString() {
        // Arrange
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setBrowser("Chrome");
        deviceInfo.setOperatingSystem("Windows");
        deviceInfo.setIpAddress("192.168.1.1");
        
        // Act
        String toString = deviceInfo.toString();
        
        // Assert
        assertTrue(toString.contains("Chrome"));
        assertTrue(toString.contains("Windows"));
        assertTrue(toString.contains("192.168.1.1"));
    }
    
    @Test
    public void testSetters() {
        // Arrange
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        
        // Act
        deviceInfo.setBrowser("Chrome");
        deviceInfo.setOperatingSystem("Windows");
        deviceInfo.setIpAddress("192.168.1.1");
        
        // Change values
        deviceInfo.setBrowser("Firefox");
        deviceInfo.setOperatingSystem("Linux");
        deviceInfo.setIpAddress("10.0.0.1");
        
        // Assert
        assertEquals("Firefox", deviceInfo.getBrowser());
        assertEquals("Linux", deviceInfo.getOperatingSystem());
        assertEquals("10.0.0.1", deviceInfo.getIpAddress());
    }
}