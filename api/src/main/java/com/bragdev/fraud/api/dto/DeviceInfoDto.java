package com.bragdev.fraud.api.dto;

import lombok.Data;

@Data
public class DeviceInfoDto {
    private String browser;
    private String operatingSystem;
    private String ipAddress;
}