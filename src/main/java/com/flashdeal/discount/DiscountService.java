package com.flashdeal.discount;

import com.flashdeal.util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service managing registered coupon strategies and orchestrating discount calculations.
 * Uses HashMap for O(1) coupon code lookups.
 */
public class DiscountService {
    private final Map<String, DiscountStrategy> couponRegistry;

    public DiscountService() {
        this.couponRegistry = new HashMap<>();
        registerDefaultCoupons();
    }

    private void registerDefaultCoupons() {
        // SAVE10: 10% off on orders above ₹500, max discount ₹300
        registerStrategy(new PercentageDiscount(
                "SAVE10",
                "10% Instant Discount on orders >= \u20B9500 (Max \u20B9300)",
                10.0,
                new BigDecimal("500.00"),
                new BigDecimal("300.00")
        ));

        // FLAT100: Flat ₹100 off on orders above ₹999
        registerStrategy(new FlatDiscount(
                "FLAT100",
                "Flat \u20B9100 OFF on orders >= \u20B9999",
                new BigDecimal("100.00"),
                new BigDecimal("999.00")
        ));

        // BANK15: 15% instant HDFC/SBI bank offer on orders above ₹1,500, max discount ₹750
        registerStrategy(new BankOfferDiscount(
                "BANK15",
                "HDFC / SBI Cards",
                "15% Instant Bank Card Discount on orders >= \u20B91500 (Max \u20B9750)",
                15.0,
                new BigDecimal("1500.00"),
                new BigDecimal("750.00")
        ));
    }

    public void registerStrategy(DiscountStrategy strategy) {
        if (strategy != null && strategy.getCouponCode() != null) {
            couponRegistry.put(strategy.getCouponCode().toUpperCase().trim(), strategy);
        }
    }

    public DiscountStrategy getStrategy(String couponCode) {
        if (couponCode == null) return null;
        return couponRegistry.get(couponCode.toUpperCase().trim());
    }

    public boolean isValidCoupon(String couponCode) {
        if (couponCode == null) return false;
        return couponRegistry.containsKey(couponCode.toUpperCase().trim());
    }

    public BigDecimal computeDiscount(String couponCode, BigDecimal totalAmount, int quantity) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        DiscountStrategy strategy = getStrategy(couponCode);
        if (strategy == null) {
            return BigDecimal.ZERO;
        }
        return strategy.calculateDiscount(totalAmount, quantity);
    }

    public List<DiscountStrategy> getAllCoupons() {
        return new ArrayList<>(couponRegistry.values());
    }

    public List<String> getAvailableCouponCodes() {
        List<String> codes = new ArrayList<>(couponRegistry.keySet());
        Collections.sort(codes);
        return codes;
    }
}
