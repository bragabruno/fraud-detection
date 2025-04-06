package com.bragdev.fraud.plaid.service;

import com.bragdev.fraud.plaid.model.PlaidAccountInfo;
import com.bragdev.fraud.plaid.model.PlaidTransaction;
import com.plaid.client.request.PlaidApi;
import com.plaid.client.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the Plaid service interface.
 * Provides methods to interact with Plaid's APIs for financial data access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlaidServiceImpl implements PlaidService {

    private final PlaidApi plaidApi;
    
    @Value("${plaid.client-id}")
    private String clientId;
    
    @Value("${plaid.redirect-uri:}")
    private String redirectUri;
    
    @Value("${plaid.client-name}")
    private String clientName;
    
    @Value("${plaid.country-codes:US}")
    private String countryCodes;
    
    @Value("${plaid.language:en}")
    private String language;

    @Override
    public String createLinkToken(String userId) {
        try {
            LinkTokenCreateRequest request = new LinkTokenCreateRequest()
                .clientId(clientId)
                .secret(System.getenv("PLAID_SECRET"))
                .clientName(clientName)
                .user(new LinkTokenCreateRequestUser().clientUserId(userId))
                .products(Arrays.asList(Products.TRANSACTIONS, Products.AUTH))
                .countryCodes(Arrays.asList(countryCodes.split(",")))
                .language(language);

            if (redirectUri != null && !redirectUri.isEmpty()) {
                request.redirectUri(redirectUri);
            }

            Response<LinkTokenCreateResponse> response = plaidApi.linkTokenCreate(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getLinkToken();
            } else {
                throw new PlaidIntegrationException("Failed to create link token: " + 
                    (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
            }
        } catch (IOException e) {
            log.error("Error creating link token", e);
            throw new PlaidIntegrationException("Error creating link token", e);
        }
    }

    @Override
    public String exchangePublicToken(String publicToken) {
        try {
            ItemPublicTokenExchangeRequest request = new ItemPublicTokenExchangeRequest()
                .clientId(clientId)
                .secret(System.getenv("PLAID_SECRET"))
                .publicToken(publicToken);

            Response<ItemPublicTokenExchangeResponse> response = 
                plaidApi.itemPublicTokenExchange(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                return response.body().getAccessToken();
            } else {
                throw new PlaidIntegrationException("Failed to exchange public token: " + 
                    (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
            }
        } catch (IOException e) {
            log.error("Error exchanging public token", e);
            throw new PlaidIntegrationException("Error exchanging public token", e);
        }
    }

    @Override
    public List<PlaidAccountInfo> getAccounts(String accessToken) {
        try {
            AccountsGetRequest request = new AccountsGetRequest()
                .clientId(clientId)
                .secret(System.getenv("PLAID_SECRET"))
                .accessToken(accessToken);

            Response<AccountsGetResponse> response = plaidApi.accountsGet(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                return mapAccountsFromResponse(response.body());
            } else {
                throw new PlaidIntegrationException("Failed to get accounts: " + 
                    (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
            }
        } catch (IOException e) {
            log.error("Error getting accounts", e);
            throw new PlaidIntegrationException("Error getting accounts", e);
        }
    }

    @Override
    public List<PlaidTransaction> getTransactions(String accessToken, LocalDate startDate, LocalDate endDate) {
        try {
            TransactionsGetRequest request = new TransactionsGetRequest()
                .clientId(clientId)
                .secret(System.getenv("PLAID_SECRET"))
                .accessToken(accessToken)
                .startDate(startDate)
                .endDate(endDate);

            Response<TransactionsGetResponse> response = plaidApi.transactionsGet(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                return mapTransactionsFromResponse(response.body());
            } else {
                throw new PlaidIntegrationException("Failed to get transactions: " + 
                    (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
            }
        } catch (IOException e) {
            log.error("Error getting transactions", e);
            throw new PlaidIntegrationException("Error getting transactions", e);
        }
    }

    @Override
    public List<PlaidTransaction> getTransactions(String accessToken, String accountId, 
                                                 LocalDate startDate, LocalDate endDate) {
        try {
            TransactionsGetRequest request = new TransactionsGetRequest()
                .clientId(clientId)
                .secret(System.getenv("PLAID_SECRET"))
                .accessToken(accessToken)
                .startDate(startDate)
                .endDate(endDate)
                .accountIds(Collections.singletonList(accountId));

            Response<TransactionsGetResponse> response = plaidApi.transactionsGet(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                return mapTransactionsFromResponse(response.body());
            } else {
                throw new PlaidIntegrationException("Failed to get transactions: " + 
                    (response.errorBody() != null ? response.errorBody().string() : "Unknown error"));
            }
        } catch (IOException e) {
            log.error("Error getting transactions", e);
            throw new PlaidIntegrationException("Error getting transactions", e);
        }
    }

    /**
     * Maps Plaid API account response to our domain model
     */
    private List<PlaidAccountInfo> mapAccountsFromResponse(AccountsGetResponse response) {
        return response.getAccounts().stream()
            .map(this::mapAccount)
            .collect(Collectors.toList());
    }

    /**
     * Maps a single Plaid account to our domain model
     */
    private PlaidAccountInfo mapAccount(Account account) {
        return PlaidAccountInfo.builder()
            .id(account.getAccountId())
            .mask(account.getMask())
            .name(account.getName())
            .officialName(account.getOfficialName())
            .type(mapAccountType(account.getType()))
            .subtype(mapAccountSubtype(account.getSubtype()))
            .balances(mapBalances(account.getBalances()))
            .build();
    }

    /**
     * Maps Plaid account type to our enum
     */
    private PlaidAccountInfo.AccountType mapAccountType(AccountType type) {
        if (type == null) return PlaidAccountInfo.AccountType.OTHER;
        
        switch (type) {
            case CREDIT:
                return PlaidAccountInfo.AccountType.CREDIT;
            case DEPOSITORY:
                return PlaidAccountInfo.AccountType.DEPOSITORY;
            case INVESTMENT:
                return PlaidAccountInfo.AccountType.INVESTMENT;
            case LOAN:
                return PlaidAccountInfo.AccountType.LOAN;
            case OTHER:
            default:
                return PlaidAccountInfo.AccountType.OTHER;
        }
    }

    /**
     * Maps Plaid account subtype to our enum
     */
    private PlaidAccountInfo.AccountSubType mapAccountSubtype(AccountSubtype subtype) {
        if (subtype == null) return PlaidAccountInfo.AccountSubType.OTHER;
        
        // Map the most common subtypes, default to OTHER for uncommon ones
        String subtypeStr = subtype.toString();
        if (subtypeStr.contains("CHECKING")) return PlaidAccountInfo.AccountSubType.CHECKING;
        if (subtypeStr.contains("SAVINGS")) return PlaidAccountInfo.AccountSubType.SAVINGS;
        if (subtypeStr.contains("CREDIT_CARD")) return PlaidAccountInfo.AccountSubType.CREDIT_CARD;
        if (subtypeStr.contains("PAYPAL")) return PlaidAccountInfo.AccountSubType.PAYPAL;
        if (subtypeStr.contains("MONEY_MARKET")) return PlaidAccountInfo.AccountSubType.MONEY_MARKET;
        if (subtypeStr.contains("MORTGAGE")) return PlaidAccountInfo.AccountSubType.MORTGAGE;
        if (subtypeStr.contains("STUDENT")) return PlaidAccountInfo.AccountSubType.STUDENT;
        if (subtypeStr.contains("AUTO")) return PlaidAccountInfo.AccountSubType.CAR;
        
        return PlaidAccountInfo.AccountSubType.OTHER;
    }

    /**
     * Maps Plaid balances to our domain model
     */
    private PlaidAccountInfo.BalanceInfo mapBalances(AccountBalance balance) {
        return PlaidAccountInfo.BalanceInfo.builder()
            .available(balance.getAvailable() != null ? 
                      new BigDecimal(balance.getAvailable().toString()) : null)
            .current(balance.getCurrent() != null ? 
                    new BigDecimal(balance.getCurrent().toString()) : null)
            .limit(balance.getLimit() != null ? 
                  new BigDecimal(balance.getLimit().toString()) : null)
            .isoCurrencyCode(balance.getIsoCurrencyCode())
            .unofficialCurrencyCode(balance.getUnofficialCurrencyCode())
            .lastUpdatedDatetime(balance.getLastUpdatedDatetime() != null ? 
                               balance.getLastUpdatedDatetime().toEpochMilli() : null)
            .build();
    }

    /**
     * Maps Plaid API transaction response to our domain model
     */
    private List<PlaidTransaction> mapTransactionsFromResponse(TransactionsGetResponse response) {
        return response.getTransactions().stream()
            .map(this::mapTransaction)
            .collect(Collectors.toList());
    }

    /**
     * Maps a single Plaid transaction to our domain model
     */
    private PlaidTransaction mapTransaction(Transaction transaction) {
        return PlaidTransaction.builder()
            .id(transaction.getTransactionId())
            .accountId(transaction.getAccountId())
            .pendingTransactionId(transaction.getPendingTransactionId())
            .categoryId(transaction.getCategoryId())
            .category(transaction.getCategory() != null ? 
                     transaction.getCategory().toArray(new String[0]) : null)
            .merchantName(transaction.getMerchantName())
            .name(transaction.getName())
            .amount(transaction.getAmount() != null ? 
                   new BigDecimal(transaction.getAmount().toString()) : null)
            .date(transaction.getDate())
            .pending(transaction.getPending())
            .currencyCode(transaction.getIsoCurrencyCode())
            .location(mapLocation(transaction.getLocation()))
            .paymentMeta(mapPaymentMeta(transaction.getPaymentMeta()))
            .build();
    }

    /**
     * Maps Plaid location to our domain model
     */
    private PlaidTransaction.Location mapLocation(TransactionLocation location) {
        if (location == null) return null;
        
        return PlaidTransaction.Location.builder()
            .address(location.getAddress())
            .city(location.getCity())
            .region(location.getRegion())
            .postalCode(location.getPostalCode())
            .country(location.getCountry())
            .latitude(location.getLat())
            .longitude(location.getLon())
            .storeNumber(location.getStoreNumber())
            .build();
    }

    /**
     * Maps Plaid payment metadata to our domain model
     */
    private PlaidTransaction.PaymentMeta mapPaymentMeta(TransactionPaymentMeta meta) {
        if (meta == null) return null;
        
        return PlaidTransaction.PaymentMeta.builder()
            .byOrderOf(meta.getByOrderOf())
            .payee(meta.getPayee())
            .payer(meta.getPayer())
            .paymentMethod(meta.getPaymentMethod())
            .paymentProcessor(meta.getPaymentProcessor())
            .ppdId(meta.getPpdId())
            .reason(meta.getReason())
            .referenceNumber(meta.getReferenceNumber())
            .build();
    }

    /**
     * Exception thrown for Plaid integration errors
     */
    public static class PlaidIntegrationException extends RuntimeException {
        public PlaidIntegrationException(String message) {
            super(message);
        }
        
        public PlaidIntegrationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}