package com.flashdeal.inventory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread-Safe Inventory Manager.
 * Demonstrates thread synchronization using monitors and synchronized methods
 * to prevent race conditions and overselling during concurrent flash sales.
 */
public class Inventory {
    // Map storing productId -> available stock count
    private final Map<String, Integer> stockMap;
    // Dedicated lock objects for fine-grained per-product locking if needed, or monitor lock
    private final Object inventoryLock = new Object();

    public Inventory() {
        this.stockMap = new HashMap<>();
    }

    /**
     * Initializes or resets the stock level for a product.
     */
    public void setStock(String productId, int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        synchronized (inventoryLock) {
            stockMap.put(productId, stock);
        }
    }

    /**
     * Retrieves the current available stock for a product in a thread-safe manner.
     */
    public int getStock(String productId) {
        synchronized (inventoryLock) {
            return stockMap.getOrDefault(productId, 0);
        }
    }

    /**
     * ATOMIC check-and-deduct operation.
     * Prevents race conditions by ensuring that stock verification and deduction
     * occur inside a single synchronized block.
     *
     * @param productId         The product identifier.
     * @param requestedQuantity Number of units requested.
     * @return Remaining stock count after deduction.
     * @throws OutOfStockException      If available stock is less than requestedQuantity.
     * @throws IllegalArgumentException If requestedQuantity <= 0.
     */
    public int deductStock(String productId, int requestedQuantity) throws OutOfStockException {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be positive. Received: " + requestedQuantity);
        }

        synchronized (inventoryLock) {
            int currentStock = stockMap.getOrDefault(productId, 0);

            // Strict check: if not enough units remain, reject purchase immediately
            if (currentStock < requestedQuantity) {
                throw new OutOfStockException(productId, requestedQuantity, currentStock);
            }

            // Deduct stock atomically
            int remainingStock = currentStock - requestedQuantity;
            stockMap.put(productId, remainingStock);

            return remainingStock;
        }
    }

    /**
     * Restores stock in case of cancellation or rollback.
     */
    public int restoreStock(String productId, int quantityToRestore) {
        if (quantityToRestore <= 0) {
            return getStock(productId);
        }
        synchronized (inventoryLock) {
            int currentStock = stockMap.getOrDefault(productId, 0);
            int updated = currentStock + quantityToRestore;
            stockMap.put(productId, updated);
            return updated;
        }
    }

    /**
     * Checks if requested quantity is currently available without mutating state.
     */
    public boolean hasAvailableStock(String productId, int requestedQuantity) {
        if (requestedQuantity <= 0) return false;
        synchronized (inventoryLock) {
            return stockMap.getOrDefault(productId, 0) >= requestedQuantity;
        }
    }

    /**
     * Returns an unmodifiable snapshot of the current inventory state.
     */
    public Map<String, Integer> getStockSnapshot() {
        synchronized (inventoryLock) {
            return Collections.unmodifiableMap(new HashMap<>(stockMap));
        }
    }
}
