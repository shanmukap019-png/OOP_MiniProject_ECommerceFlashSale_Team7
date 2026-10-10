package com.flashdeal.model;

import com.flashdeal.util.CurrencyUtil;

import java.math.BigDecimal;

/**
 * Encapsulates aggregated sales metrics for individual products or global flash sale operations.
 */
public class SaleMetric {
    private final String productId;
    private final String productName;
    private int totalAttempts;
    private int successfulOrders;
    private int failedOrders;
    private int unitsSold;
    private BigDecimal totalRevenue;
    private BigDecimal totalDiscounts;

    public SaleMetric(String productId, String productName) {
        this.productId = productId;
        this.productName = productName;
        this.totalRevenue = BigDecimal.ZERO;
        this.totalDiscounts = BigDecimal.ZERO;
    }

    public void recordAttempt(boolean success, int units, BigDecimal revenue, BigDecimal discount) {
        this.totalAttempts++;
        if (success) {
            this.successfulOrders++;
            this.unitsSold += units;
            this.totalRevenue = this.totalRevenue.add(revenue != null ? revenue : BigDecimal.ZERO);
            this.totalDiscounts = this.totalDiscounts.add(discount != null ? discount : BigDecimal.ZERO);
        } else {
            this.failedOrders++;
        }
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getSuccessfulOrders() {
        return successfulOrders;
    }

    public int getFailedOrders() {
        return failedOrders;
    }

    public int getUnitsSold() {
        return unitsSold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public BigDecimal getTotalDiscounts() {
        return totalDiscounts;
    }

    public double getSuccessRate() {
        if (totalAttempts == 0) return 0.0;
        return ((double) successfulOrders / totalAttempts) * 100.0;
    }

    public String getFormattedRevenue() {
        return CurrencyUtil.formatRupee(totalRevenue);
    }

    public String getFormattedDiscounts() {
        return CurrencyUtil.formatRupee(totalDiscounts);
    }
}
