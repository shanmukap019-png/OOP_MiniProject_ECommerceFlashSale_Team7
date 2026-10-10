package com.flashdeal.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete strategy representing Instant Bank Credit/Debit card discount.
 * Includes bank-specific conditions, minimum transaction value, and maximum savings cap.
 */
public class BankOfferDiscount extends DiscountStrategyBase {
    private final String partnerBank;
    private final double bankDiscountPercentage;

    public BankOfferDiscount(String couponCode, String partnerBank, String description,
                             double bankDiscountPercentage, BigDecimal minOrderAmount,
                             BigDecimal maxDiscountAmount) {
        super(couponCode, description, minOrderAmount, maxDiscountAmount);
        if (bankDiscountPercentage <= 0 || bankDiscountPercentage > 100) {
            throw new IllegalArgumentException("Bank discount percentage must be between 0 and 100");
        }
        this.partnerBank = partnerBank;
        this.bankDiscountPercentage = bankDiscountPercentage;
    }

    @Override
    protected BigDecimal computeRawDiscount(BigDecimal totalAmount, int quantity) {
        BigDecimal rate = BigDecimal.valueOf(bankDiscountPercentage)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return totalAmount.multiply(rate);
    }

    public String getPartnerBank() {
        return partnerBank;
    }

    public double getBankDiscountPercentage() {
        return bankDiscountPercentage;
    }
}
