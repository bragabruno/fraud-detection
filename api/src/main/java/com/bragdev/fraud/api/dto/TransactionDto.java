package com.bragdev.fraud.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
public class TransactionDto {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String merchantId;
    private String merchantName;
    private String merchantCategory;
    private Instant timestamp;
    private GeoLocationDto location;
    private String channel;
    private DeviceInfoDto deviceInfo;
    private String type;
    private Map<String, Object> additionalAttributes;
}