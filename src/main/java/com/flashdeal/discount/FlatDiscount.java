package com.flashdeal.discount;

import java.math.BigDecimal;

/**
 * Concrete strategy applying a fixed flat rupee discount if minimum purchase condition is met.
 * Demonstrates inheritance and method overriding.
 */
public class FlatDiscount extends DiscountStrategyBase {
    private final BigDecimal flatAmount;

    public FlatDiscount(String couponCode, String description, BigDecimal flatAmount,
                        BigDecimal minOrderAmount) {
        super(couponCode, description, minOrderAmount, flatAmount);
        if (flatAmount == null || flatAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Flat discount amount must be greater than zero");
        }
        this.flatAmount = flatAmount;
    }

    @Override
    protected BigDecimal computeRawDiscount(BigDecimal totalAmount, int quantity) {
        return flatAmount;
    }

    public BigDecimal getFlatAmount() {
        return flatAmount;
    }
}
