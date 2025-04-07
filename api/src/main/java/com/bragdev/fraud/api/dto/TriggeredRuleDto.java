package com.bragdev.fraud.api.dto;

import lombok.Data;

@Data
public class TriggeredRuleDto {
    private String ruleId;
    private String ruleName;
    private String category;
    private double severity;
    private String triggerReason;
}