package com.bragdev.fraud.plaid.service;

import com.bragdev.fraud.plaid.model.PlaidAccountInfo;
import com.bragdev.fraud.plaid.model.PlaidTransaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Plaid API integration.
 * Defines methods for interacting with Plaid's financial data services.
 */
public interface PlaidService {

    /**
     * Creates a link token for initializing Plaid Link
     * 
     * @param userId The user identifier in our system
     * @return A Plaid link token
     */
    String createLinkToken(String userId);
    
    /**
     * Exchanges a public token for an access token
     * 
     * @param publicToken The public token received from Plaid Link
     * @return The access token for subsequent API calls
     */
    String exchangePublicToken(String publicToken);
    
    /**
     * Retrieves account information for a user
     * 
     * @param accessToken The Plaid access token
     * @return List of account information
     */
    List<PlaidAccountInfo> getAccounts(String accessToken);
    
    /**
     * Retrieves transactions for a specific time period
     * 
     * @param accessToken The Plaid access token
     * @param startDate The start date for transactions
     * @param endDate The end date for transactions
     * @return List of transactions
     */
    List<PlaidTransaction> getTransactions(String accessToken, LocalDate startDate, LocalDate endDate);
    
    /**
     * Retrieves transactions for a specific account and time period
     * 
     * @param accessToken The Plaid access token
     * @param accountId The account ID
     * @param startDate The start date for transactions
     * @param endDate The end date for transactions
     * @return List of transactions for the specified account
     */
    List<PlaidTransaction> getTransactions(String accessToken, String accountId, LocalDate startDate, LocalDate endDate);
}