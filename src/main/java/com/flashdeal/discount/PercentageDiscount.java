package com.flashdeal.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete strategy applying a percentage-based discount up to a defined ceiling.
 * Extends DiscountStrategyBase to demonstrate OOP inheritance.
 */
public class PercentageDiscount extends DiscountStrategyBase {
    private final double percentage;

    public PercentageDiscount(String couponCode, String description, double percentage,
                              BigDecimal minOrderAmount, BigDecimal maxDiscountAmount) {
        super(couponCode, description, minOrderAmount, maxDiscountAmount);
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.percentage = percentage;
    }

    @Override
    protected BigDecimal computeRawDiscount(BigDecimal totalAmount, int quantity) {
        BigDecimal factor = BigDecimal.valueOf(percentage).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return totalAmount.multiply(factor);
    }

    public double getPercentage() {
        return percentage;
    }
}
