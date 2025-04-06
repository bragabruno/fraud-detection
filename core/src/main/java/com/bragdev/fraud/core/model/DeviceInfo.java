package com.bragdev.fraud.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains information about the device used for a transaction.
 * Used for device fingerprinting and identifying potential fraudulent access.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceInfo {
    private String userAgent;
    private String operatingSystem;
    private String ipAddress;
    private String deviceId;
    private String browserFingerprint;
    private String screenResolution;
    private String timeZone;
    private String language;
    private boolean isMobile;
    private boolean isEmulator;
    private boolean isRooted;
    
    /**
     * Simplified constructor with essential fields
     */
    public DeviceInfo(String userAgent, String operatingSystem, String ipAddress) {
        this.userAgent = userAgent;
        this.operatingSystem = operatingSystem;
        this.ipAddress = ipAddress;
    }
}