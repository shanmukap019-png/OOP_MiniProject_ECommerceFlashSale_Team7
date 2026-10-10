package com.flashdeal.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Abstract base class providing common properties and template method logic for discounts.
 * Demonstrates inheritance and method overriding.
 */
public abstract class DiscountStrategyBase implements DiscountStrategy {
    protected final String couponCode;
    protected final String description;
    protected final BigDecimal minOrderAmount;
    protected final BigDecimal maxDiscountAmount;

    public DiscountStrategyBase(String couponCode, String description,
                                BigDecimal minOrderAmount, BigDecimal maxDiscountAmount) {
        this.couponCode = couponCode;
        this.description = description;
        this.minOrderAmount = (minOrderAmount != null) ? minOrderAmount : BigDecimal.ZERO;
        this.maxDiscountAmount = maxDiscountAmount;
    }

    @Override
    public boolean isEligible(BigDecimal totalAmount, int quantity) {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0 || quantity <= 0) {
            return false;
        }
        return totalAmount.compareTo(minOrderAmount) >= 0;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal totalAmount, int quantity) {
        if (!isEligible(totalAmount, quantity)) {
            return BigDecimal.ZERO;
        }

        BigDecimal rawDiscount = computeRawDiscount(totalAmount, quantity);
        if (rawDiscount == null || rawDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Apply maximum discount ceiling if specified
        if (maxDiscountAmount != null && maxDiscountAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (rawDiscount.compareTo(maxDiscountAmount) > 0) {
                rawDiscount = maxDiscountAmount;
            }
        }

        // Guarantee final discount cannot exceed the total amount to prevent negative payable amounts
        if (rawDiscount.compareTo(totalAmount) > 0) {
            rawDiscount = totalAmount;
        }

        return rawDiscount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Subclasses must override this to compute their specific discount formula.
     */
    protected abstract BigDecimal computeRawDiscount(BigDecimal totalAmount, int quantity);

    @Override
    public String getCouponCode() {
        return couponCode;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    @Override
    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    @Override
    public String toString() {
        return couponCode + " - " + description;
    }
}
