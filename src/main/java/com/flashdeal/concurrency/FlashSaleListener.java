package com.flashdeal.concurrency;

import com.flashdeal.model.Customer;
import com.flashdeal.model.Order;
import com.flashdeal.model.Product;

import java.math.BigDecimal;

/**
 * Listener interface for observing flash sale simulation events in real time.
 */
public interface FlashSaleListener {
    void onPurchaseAttempt(Customer customer, Product product, int quantity);
    void onPurchaseSuccess(Order order, int remainingStock);
    void onPurchaseFailed(Customer customer, Product product, int quantity, String reason);
    void onSimulationProgress(int completedAttempts, int totalAttempts, int remainingStock);
    void onSimulationComplete(int totalAttempts, int successCount, int failureCount, BigDecimal totalRevenue);
}
