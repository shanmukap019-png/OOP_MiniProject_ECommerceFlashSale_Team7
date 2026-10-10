package com.flashdeal.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility for uniform Indian Rupee (₹) currency formatting and operations.
 */
public final class CurrencyUtil {

    private static final Locale INDIA_LOCALE = Locale.forLanguageTag("en-IN");
    public static final String RUPEE_SYMBOL = "\u20B9";

    private CurrencyUtil() {
        // Prevent instantiation
    }

    /**
     * Formats a BigDecimal amount as Indian Rupee (₹) string.
     */
    public static String formatRupee(BigDecimal amount) {
        if (amount == null) {
            return RUPEE_SYMBOL + "0.00";
        }
        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);
        NumberFormat formatter = NumberFormat.getNumberInstance(INDIA_LOCALE);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return RUPEE_SYMBOL + " " + formatter.format(scaled);
    }

    /**
     * Formats a double amount as Indian Rupee (₹) string.
     */
    public static String formatRupee(double amount) {
        return formatRupee(BigDecimal.valueOf(amount));
    }

    /**
     * Parses a string into a clean BigDecimal value.
     */
    public static BigDecimal parseAmount(String input) {
        if (input == null || input.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        String clean = input.replace(RUPEE_SYMBOL, "").replace(",", "").trim();
        try {
            return new BigDecimal(clean).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
