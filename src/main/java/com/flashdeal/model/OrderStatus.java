package com.flashdeal.model;

/**
 * Enumeration representing the possible outcomes of an order in the flash sale.
 */
public enum OrderStatus {
    SUCCESS("Success"),
    FAILED("Failed"),
    OUT_OF_STOCK("Out of Stock");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus fromString(String text) {
        if (text == null) return FAILED;
        for (OrderStatus status : OrderStatus.values()) {
            if (status.name().equalsIgnoreCase(text.trim()) || 
                status.displayName.equalsIgnoreCase(text.trim())) {
                return status;
            }
        }
        return FAILED;
    }
}
