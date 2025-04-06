package com.bragdev.fraud.plaid.controller;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.plaid.adapter.PlaidTransactionAdapter;
import com.bragdev.fraud.plaid.model.PlaidAccountInfo;
import com.bragdev.fraud.plaid.model.PlaidTransaction;
import com.bragdev.fraud.plaid.service.PlaidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for Plaid API integration.
 * Provides endpoints for creating link tokens, exchanging tokens,
 * and retrieving financial data for fraud analysis.
 */
@RestController
@RequestMapping("/api/plaid")
@RequiredArgsConstructor
@Slf4j
public class PlaidController {

    private final PlaidService plaidService;
    private final PlaidTransactionAdapter transactionAdapter;

    /**
     * Creates a link token to initialize Plaid Link
     *
     * @param userId User identifier
     * @return Link token
     */
    @PostMapping("/create-link-token")
    public ResponseEntity<Map<String, String>> createLinkToken(@RequestParam String userId) {
        String linkToken = plaidService.createLinkToken(userId);
        Map<String, String> response = new HashMap<>();
        response.put("link_token", linkToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Exchanges a public token for an access token
     *
     * @param publicToken The public token from Plaid Link
     * @return Access token
     */
    @PostMapping("/exchange-token")
    public ResponseEntity<Map<String, String>> exchangeToken(@RequestParam String publicToken) {
        String accessToken = plaidService.exchangePublicToken(publicToken);
        Map<String, String> response = new HashMap<>();
        response.put("access_token", accessToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets account information for a user
     *
     * @param accessToken The Plaid access token
     * @return List of accounts
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<PlaidAccountInfo>> getAccounts(@RequestParam String accessToken) {
        List<PlaidAccountInfo> accounts = plaidService.getAccounts(accessToken);
        return ResponseEntity.ok(accounts);
    }

    /**
     * Gets transactions for a date range
     *
     * @param accessToken The Plaid access token
     * @param startDate   Start date for transactions
     * @param endDate     End date for transactions
     * @return List of transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<PlaidTransaction>> getTransactions(
            @RequestParam String accessToken,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PlaidTransaction> transactions = plaidService.getTransactions(accessToken, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Gets transactions for fraud analysis
     * Converts Plaid transactions to the core fraud detection Transaction model
     *
     * @param accessToken The Plaid access token
     * @param startDate   Start date for transactions
     * @param endDate     End date for transactions
     * @return List of fraud detection system transactions
     */
    @GetMapping("/fraud-analysis/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsForFraudAnalysis(
            @RequestParam String accessToken,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Get transactions from Plaid
        List<PlaidTransaction> plaidTransactions = plaidService.getTransactions(accessToken, startDate, endDate);
        
        // Convert to core Transaction model for fraud analysis
        List<Transaction> fraudDetectionTransactions = plaidTransactions.stream()
                .map(transactionAdapter::adaptToCoreTransaction)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(fraudDetectionTransactions);
    }
}