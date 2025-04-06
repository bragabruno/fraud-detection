package com.bragdev.fraud.plaid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents account information retrieved from Plaid.
 * This maps to essential account details returned from Plaid's API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaidAccountInfo {
    private String id;
    private String mask;
    private String name;
    private String officialName;
    private AccountType type;
    private AccountSubType subtype;
    private BalanceInfo balances;
    
    /**
     * Account type enumeration matching Plaid's categorization
     */
    public enum AccountType {
        CREDIT, DEPOSITORY, INVESTMENT, LOAN, OTHER
    }
    
    /**
     * Account subtype enumeration matching Plaid's categorization
     */
    public enum AccountSubType {
        CHECKING, SAVINGS, CREDIT_CARD, PAYPAL, MONEY_MARKET, 
        HOME, CAR, STUDENT, PERSONAL, MORTGAGE, OVERDRAFT, OTHER
    }
    
    /**
     * Account balance information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceInfo {
        private BigDecimal available;
        private BigDecimal current;
        private BigDecimal limit;
        private String isoCurrencyCode;
        private String unofficialCurrencyCode;
        private Long lastUpdatedDatetime;
    }
}