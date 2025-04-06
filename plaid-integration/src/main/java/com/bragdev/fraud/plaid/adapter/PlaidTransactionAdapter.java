package com.bragdev.fraud.plaid.adapter;

import com.bragdev.fraud.core.model.DeviceInfo;
import com.bragdev.fraud.core.model.GeoLocation;
import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TransactionType;
import com.bragdev.fraud.plaid.model.PlaidTransaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter for converting Plaid transactions to the core fraud detection system's transaction model.
 * This adapter allows the fraud detection engine to analyze transactions from Plaid.
 */
@Component
public class PlaidTransactionAdapter {

    /**
     * Converts a Plaid transaction to the core fraud detection Transaction model
     *
     * @param plaidTransaction The Plaid transaction to convert
     * @return A Transaction instance used by the fraud detection engine
     */
    public Transaction adaptToCoreTransaction(PlaidTransaction plaidTransaction) {
        if (plaidTransaction == null) {
            return null;
        }

        // Convert Plaid transaction to our core Transaction model
        Transaction transaction = new Transaction();
        
        // Set basic transaction information
        transaction.setId(UUID.randomUUID()); // Generate new ID since Plaid IDs are strings
        transaction.setAccountId(plaidTransaction.getAccountId());
        transaction.setAmount(plaidTransaction.getAmount() != null ? 
                              plaidTransaction.getAmount().abs() : BigDecimal.ZERO); // Ensure positive amount
        transaction.setCurrency(plaidTransaction.getCurrencyCode());
        
        // Set merchant information
        transaction.setMerchantId("PLAID-" + (plaidTransaction.getId() != null ? 
                                 plaidTransaction.getId() : "UNKNOWN"));
        transaction.setMerchantName(plaidTransaction.getMerchantName() != null ? 
                                   plaidTransaction.getMerchantName() : plaidTransaction.getName());
        
        // Set category
        String[] categories = plaidTransaction.getCategory();
        if (categories != null && categories.length > 0) {
            transaction.setMerchantCategory(categories[0]);
        } else {
            transaction.setMerchantCategory("UNKNOWN");
        }
        
        // Set timestamp (convert LocalDate to Instant)
        LocalDate date = plaidTransaction.getDate();
        if (date != null) {
            Instant timestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            transaction.setTimestamp(timestamp);
        } else {
            transaction.setTimestamp(Instant.now());
        }
        
        // Set location if available
        PlaidTransaction.Location plaidLocation = plaidTransaction.getLocation();
        if (plaidLocation != null && plaidLocation.getLatitude() != null && plaidLocation.getLongitude() != null) {
            GeoLocation location = new GeoLocation(
                plaidLocation.getLatitude(),
                plaidLocation.getLongitude()
            );
            transaction.setLocation(location);
        }
        
        // Set device info (defaults since Plaid doesn't provide device info)
        transaction.setDeviceInfo(new DeviceInfo("Unknown", "Unknown", "0.0.0.0"));
        
        // Set channel (always assume online for Plaid transactions)
        transaction.setChannel("ONLINE");
        
        // Set transaction type (assume purchase by default)
        transaction.setType(TransactionType.PURCHASE);
        
        // Set additional attributes
        Map<String, Object> additionalAttributes = new HashMap<>();
        additionalAttributes.put("plaidTransactionId", plaidTransaction.getId());
        additionalAttributes.put("isPending", plaidTransaction.isPending());
        
        // Add payment metadata if available
        PlaidTransaction.PaymentMeta paymentMeta = plaidTransaction.getPaymentMeta();
        if (paymentMeta != null) {
            if (paymentMeta.getPaymentMethod() != null) {
                additionalAttributes.put("paymentMethod", paymentMeta.getPaymentMethod());
            }
            if (paymentMeta.getPaymentProcessor() != null) {
                additionalAttributes.put("paymentProcessor", paymentMeta.getPaymentProcessor());
            }
            if (paymentMeta.getReferenceNumber() != null) {
                additionalAttributes.put("referenceNumber", paymentMeta.getReferenceNumber());
            }
        }
        
        transaction.setAdditionalAttributes(additionalAttributes);
        
        // Set metadata
        transaction.setReceivedAt(Instant.now());
        transaction.setTransactionReference("PLAID-" + plaidTransaction.getId());
        
        return transaction;
    }
}