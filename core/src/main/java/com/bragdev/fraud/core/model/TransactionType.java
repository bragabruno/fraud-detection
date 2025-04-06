package com.bragdev.fraud.core.model;

/**
 * Enumeration of possible transaction types that can be processed by the system.
 */
public enum TransactionType {
    PURCHASE,
    CASH_WITHDRAWAL,
    TRANSFER,
    PAYMENT,
    REFUND,
    CREDIT,
    DEBIT,
    DEPOSIT,
    FEE,
    ADJUSTMENT,
    DISPUTE,
    REVERSAL,
    OTHER
}