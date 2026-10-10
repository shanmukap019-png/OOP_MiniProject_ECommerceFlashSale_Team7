package com.flashdeal.util;

import java.math.BigDecimal;

/**
 * Validation utilities for application inputs.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        // Prevent instantiation
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidPositiveInt(int value) {
        return value > 0;
    }

    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static boolean isValidCouponCode(String code) {
        if (code == null) return false;
        String trimmed = code.trim();
        return trimmed.matches("^[A-Z0-9_-]{3,15}$");
    }
}
