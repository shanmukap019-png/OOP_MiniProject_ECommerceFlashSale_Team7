package com.flashdeal.discount;

import java.math.BigDecimal;

/**
 * Interface defining the contract for various flash sale discount algorithms.
 * Demonstrates polymorphism in object-oriented design.
 */
public interface DiscountStrategy {

    /**
     * Calculates the monetary discount to apply on the total price.
     *
     * @param totalAmount  The base amount before coupon discount.
     * @param quantity     The quantity of units being purchased.
     * @return The discount amount in BigDecimal (must be >= 0 and <= totalAmount).
     */
    BigDecimal calculateDiscount(BigDecimal totalAmount, int quantity);

    /**
     * Validates whether the given total amount and quantity meet the coupon's eligibility criteria.
     */
    boolean isEligible(BigDecimal totalAmount, int quantity);

    /**
     * Returns the unique coupon code.
     */
    String getCouponCode();

    /**
     * Returns a human-friendly description of the discount rules.
     */
    String getDescription();

    /**
     * Returns the minimum order amount required to qualify for this discount.
     */
    BigDecimal getMinOrderAmount();

    /**
     * Returns the maximum discount cap, or null if uncapped.
     */
    BigDecimal getMaxDiscountAmount();
}
