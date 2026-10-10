package com.flashdeal.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service orchestrating inventory queries, mutations, and thread-safe stock listeners.
 */
public class InventoryService {
    public interface StockChangeListener {
        void onStockChanged(String productId, int remainingStock);
    }

    private final Inventory inventory;
    private final List<StockChangeListener> listeners;

    public InventoryService(Inventory inventory) {
        this.inventory = (inventory != null) ? inventory : new Inventory();
        this.listeners = new ArrayList<>();
    }

    public synchronized void addStockListener(StockChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public synchronized void removeStockListener(StockChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyStockChanged(String productId, int remainingStock) {
        List<StockChangeListener> copy;
        synchronized (this) {
            copy = new ArrayList<>(listeners);
        }
        for (StockChangeListener listener : copy) {
            try {
                listener.onStockChanged(productId, remainingStock);
            } catch (Exception e) {
                // Prevent listener errors from breaking inventory flow
            }
        }
    }

    public int getAvailableStock(String productId) {
        return inventory.getStock(productId);
    }

    public int deductStockAtomically(String productId, int quantity) throws OutOfStockException {
        int remaining = inventory.deductStock(productId, quantity);
        notifyStockChanged(productId, remaining);
        return remaining;
    }

    public void setStock(String productId, int stock) {
        inventory.setStock(productId, stock);
        notifyStockChanged(productId, stock);
    }

    public boolean hasAvailableStock(String productId, int quantity) {
        return inventory.hasAvailableStock(productId, quantity);
    }

    public Map<String, Integer> getStockSnapshot() {
        return inventory.getStockSnapshot();
    }
}
