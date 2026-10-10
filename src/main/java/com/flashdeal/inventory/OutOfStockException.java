package com.flashdeal.inventory;

/**
 * Custom Exception thrown when an order cannot be fulfilled due to insufficient inventory.
 */
public class OutOfStockException extends Exception {
    private final String productId;
    private final int requestedQuantity;
    private final int availableQuantity;

    public OutOfStockException(String message) {
        super(message);
        this.productId = "UNKNOWN";
        this.requestedQuantity = 0;
        this.availableQuantity = 0;
    }

    public OutOfStockException(String productId, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient inventory for Product [%s]. Requested: %d, Available: %d",
                productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public OutOfStockException(String productId, int requestedQuantity, int availableQuantity, String details) {
        super(String.format("Insufficient inventory for Product [%s]. Requested: %d, Available: %d (%s)",
                productId, requestedQuantity, availableQuantity, details));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
