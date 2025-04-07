package com.bragdev.fraud.detection.rule;

import com.bragdev.fraud.core.model.Transaction;
import com.bragdev.fraud.core.model.TriggeredRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class VelocityCheckRuleConcurrencyTest {

    private VelocityCheckRule velocityRule;
    private ExecutorService executorService;

    @BeforeEach
    public void setup() {
        // Create a velocity rule with a 5-second window and max 3 transactions
        velocityRule = new VelocityCheckRule(Duration.ofSeconds(5), 3);
        executorService = Executors.newFixedThreadPool(10);
    }

    @AfterEach
    public void cleanup() {
        // Clean up the thread local to prevent memory leaks
        velocityRule.cleanup();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testConcurrentTransactionsForSameAccount() throws Exception {
        // Arrange
        final String accountId = "ACC123";
        final int numTransactions = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch finishLatch = new CountDownLatch(numTransactions);
        final AtomicInteger triggeredCount = new AtomicInteger(0);
        final List<Future<Boolean>> results = new ArrayList<>();

        // Act - Submit concurrent transactions for the same account
        for (int i = 0; i < numTransactions; i++) {
            results.add(executorService.submit(() -> {
                // Wait for all threads to be ready
                startLatch.await();
                
                // Create and evaluate a transaction
                Transaction transaction = createTransaction(accountId);
                boolean triggered = velocityRule.evaluate(transaction);
                
                if (triggered) {
                    triggeredCount.incrementAndGet();
                }
                
                finishLatch.countDown();
                return triggered;
            }));
        }

        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for all transactions to complete
        finishLatch.await(10, TimeUnit.SECONDS);

        // Assert - Verify that some transactions triggered the rule
        int actualTriggeredCount = 0;
        for (Future<Boolean> result : results) {
            if (result.get()) {
                actualTriggeredCount++;
            }
        }

        // We expect some transactions to trigger the rule (after the first 3)
        assertTrue(actualTriggeredCount > 0, "Some transactions should have triggered the rule");
        assertEquals(triggeredCount.get(), actualTriggeredCount, "Triggered count should match actual results");
        
        // We expect numTransactions - 3 to trigger (first 3 are allowed)
        assertEquals(numTransactions - 3, actualTriggeredCount, 
                "Expected number of triggered transactions doesn't match");
    }

    @Test
    public void testConcurrentTransactionsForDifferentAccounts() throws Exception {
        // Arrange
        final int numAccounts = 5;
        final int numTransactionsPerAccount = 3; // Just at the threshold, shouldn't trigger
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch finishLatch = new CountDownLatch(numAccounts * numTransactionsPerAccount);
        final AtomicInteger triggeredCount = new AtomicInteger(0);
        final List<Future<Boolean>> results = new ArrayList<>();

        // Act - Submit concurrent transactions for different accounts
        for (int account = 0; account < numAccounts; account++) {
            final String accountId = "ACC" + account;
            
            for (int i = 0; i < numTransactionsPerAccount; i++) {
                results.add(executorService.submit(() -> {
                    // Wait for all threads to be ready
                    startLatch.await();
                    
                    // Create and evaluate a transaction
                    Transaction transaction = createTransaction(accountId);
                    boolean triggered = velocityRule.evaluate(transaction);
                    
                    if (triggered) {
                        triggeredCount.incrementAndGet();
                    }
                    
                    finishLatch.countDown();
                    return triggered;
                }));
            }
        }

        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for all transactions to complete
        finishLatch.await(10, TimeUnit.SECONDS);

        // Assert - Verify that no transactions triggered the rule
        int actualTriggeredCount = 0;
        for (Future<Boolean> result : results) {
            if (result.get()) {
                actualTriggeredCount++;
            }
        }

        // We expect no transactions to trigger the rule since each account has exactly the threshold number
        assertEquals(0, actualTriggeredCount, "No transactions should have triggered the rule");
        assertEquals(0, triggeredCount.get(), "Triggered count should be zero");
    }

    @Test
    public void testHighConcurrencyLoad() throws Exception {
        // Arrange
        final int numThreads = 100;
        final int numAccounts = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch finishLatch = new CountDownLatch(numThreads);
        final ConcurrentHashMap<String, AtomicInteger> triggeredByAccount = new ConcurrentHashMap<>();

        // Initialize counters for each account
        for (int i = 0; i < numAccounts; i++) {
            triggeredByAccount.put("ACC" + i, new AtomicInteger(0));
        }

        // Act - Submit a high number of concurrent transactions
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executorService.submit(() -> {
                try {
                    // Wait for all threads to be ready
                    startLatch.await();
                    
                    // Select an account based on the thread number to distribute load
                    String accountId = "ACC" + (threadNum % numAccounts);
                    
                    // Create and evaluate a transaction
                    Transaction transaction = createTransaction(accountId);
                    boolean triggered = velocityRule.evaluate(transaction);
                    
                    if (triggered) {
                        triggeredByAccount.get(accountId).incrementAndGet();
                    }
                    
                    return triggered;
                } catch (Exception e) {
                    fail("Exception in concurrent execution: " + e.getMessage());
                    return false;
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for all transactions to complete
        assertTrue(finishLatch.await(20, TimeUnit.SECONDS), "Timed out waiting for threads to complete");

        // Assert - Verify that the rule handled high concurrency correctly
        for (int i = 0; i < numAccounts; i++) {
            String accountId = "ACC" + i;
            int triggeredForAccount = triggeredByAccount.get(accountId).get();
            
            // Each account should have (numThreads/numAccounts - 3) triggered transactions
            // First 3 transactions per account are allowed, the rest should trigger
            int expectedTriggered = (numThreads / numAccounts) - 3;
            expectedTriggered = Math.max(0, expectedTriggered); // Ensure non-negative
            
            assertEquals(expectedTriggered, triggeredForAccount,
                    "Incorrect number of triggered transactions for account " + accountId);
        }
    }

    @Test
    public void testTimeWindowRespected() throws Exception {
        // Arrange
        final String accountId = "ACC123";
        final VelocityCheckRule shortWindowRule = new VelocityCheckRule(Duration.ofMillis(500), 2);
        
        try {
            // Act - First send 2 transactions (should not trigger)
            Transaction tx1 = createTransaction(accountId);
            Transaction tx2 = createTransaction(accountId);
            
            boolean result1 = shortWindowRule.evaluate(tx1);
            boolean result2 = shortWindowRule.evaluate(tx2);
            
            // Assert - First 2 should not trigger
            assertFalse(result1, "First transaction should not trigger");
            assertFalse(result2, "Second transaction should not trigger");
            
            // Act - Send a 3rd transaction immediately (should trigger)
            Transaction tx3 = createTransaction(accountId);
            boolean result3 = shortWindowRule.evaluate(tx3);
            
            // Assert - 3rd should trigger
            assertTrue(result3, "Third transaction should trigger");
            
            // Act - Wait for time window to expire, then send another transaction
            Thread.sleep(600); // Wait longer than the time window
            Transaction tx4 = createTransaction(accountId);
            boolean result4 = shortWindowRule.evaluate(tx4);
            
            // Assert - After window expiry, should not trigger
            assertFalse(result4, "Transaction after time window should not trigger");
        } finally {
            shortWindowRule.cleanup();
        }
    }

    @Test
    public void testTriggeredRuleCreation() throws Exception {
        // Arrange
        final String accountId = "ACC123";
        final int numTransactions = 5; // More than our threshold of 3
        
        // Act - Send multiple transactions for the same account
        TriggeredRule triggeredRule = null;
        Transaction lastTransaction = null;
        
        for (int i = 0; i < numTransactions; i++) {
            lastTransaction = createTransaction(accountId);
            velocityRule.evaluate(lastTransaction);
            
            // Try to create a triggered rule
            TriggeredRule rule = velocityRule.createTriggeredRule(lastTransaction);
            
            // Store the first non-null rule we get
            if (rule != null && triggeredRule == null) {
                triggeredRule = rule;
            }
        }
        
        // Assert
        assertNotNull(triggeredRule, "A triggered rule should have been created");
        assertEquals(velocityRule.getId(), triggeredRule.getRuleId());
        assertEquals(velocityRule.getName(), triggeredRule.getRuleName());
        assertEquals(velocityRule.getSeverity(), triggeredRule.getSeverity());
        
        // Verify trigger reason
        String reason = velocityRule.generateTriggerReason(lastTransaction);
        assertNotNull(reason);
        assertTrue(reason.contains(accountId), "Trigger reason should contain the account ID");
    }

    // Helper method to create a transaction
    private Transaction createTransaction(String accountId) {
        UUID id = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100.00");
        String currency = "USD";
        String merchantId = "MERCH456";
        String merchantName = "Test Merchant";
        String merchantCategory = "Retail";
        Instant timestamp = Instant.now();
        
        // Use reflection to create a Transaction instance
        try {
            // Get the Transaction.Builder class
            Class<?> builderClass = Class.forName("com.bragdev.fraud.core.model.Transaction$Builder");
            
            // Get the builder instance
            Object builder = Transaction.class.getMethod("builder").invoke(null);
            
            // Set the properties
            builderClass.getMethod("id", UUID.class).invoke(builder, id);
            builderClass.getMethod("accountId", String.class).invoke(builder, accountId);
            builderClass.getMethod("amount", BigDecimal.class).invoke(builder, amount);
            builderClass.getMethod("currency", String.class).invoke(builder, currency);
            builderClass.getMethod("merchantId", String.class).invoke(builder, merchantId);
            builderClass.getMethod("merchantName", String.class).invoke(builder, merchantName);
            builderClass.getMethod("merchantCategory", String.class).invoke(builder, merchantCategory);
            builderClass.getMethod("timestamp", Instant.class).invoke(builder, timestamp);
            
            // Build the transaction
            return (Transaction) builderClass.getMethod("build").invoke(builder);
        } catch (Exception e) {
            fail("Failed to create Transaction: " + e.getMessage());
            return null;
        }
    }
}