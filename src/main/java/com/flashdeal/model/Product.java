package com.flashdeal.model;

import com.flashdeal.util.CurrencyUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents an e-commerce product available in the Flash Sale.
 */
public class Product {
    private String productId;
    private String name;
    private String description;
    private String category;
    private BigDecimal originalPrice;
    private BigDecimal flashSalePrice;
    private int initialStock;
    private int availableStock;
    private double discountPercentage;
    private String status; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    public Product() {
        this.originalPrice = BigDecimal.ZERO;
        this.flashSalePrice = BigDecimal.ZERO;
        this.status = "OUT_OF_STOCK";
    }

    public Product(String productId, String name, String description, String category,
                   BigDecimal originalPrice, BigDecimal flashSalePrice, int initialStock) {
        if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Original price cannot be negative");
        }
        if (flashSalePrice == null || flashSalePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Flash sale price cannot be negative");
        }
        if (initialStock < 0) {
            throw new IllegalArgumentException("Initial stock cannot be negative");
        }

        this.productId = productId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.originalPrice = originalPrice.setScale(2, RoundingMode.HALF_UP);
        this.flashSalePrice = flashSalePrice.setScale(2, RoundingMode.HALF_UP);
        this.initialStock = initialStock;
        this.availableStock = initialStock;
        calculateDiscountPercentage();
        updateStatus();
    }

    public void calculateDiscountPercentage() {
        if (originalPrice.compareTo(BigDecimal.ZERO) > 0 && originalPrice.compareTo(flashSalePrice) > 0) {
            BigDecimal diff = originalPrice.subtract(flashSalePrice);
            this.discountPercentage = diff.divide(originalPrice, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        } else {
            this.discountPercentage = 0.0;
        }
    }

    public void updateStatus() {
        if (availableStock <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (availableStock <= 5) {
            this.status = "LOW_STOCK";
        } else {
            this.status = "IN_STOCK";
        }
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) >= 0) {
            this.originalPrice = originalPrice.setScale(2, RoundingMode.HALF_UP);
            calculateDiscountPercentage();
        }
    }

    public BigDecimal getFlashSalePrice() {
        return flashSalePrice;
    }

    public void setFlashSalePrice(BigDecimal flashSalePrice) {
        if (flashSalePrice != null && flashSalePrice.compareTo(BigDecimal.ZERO) >= 0) {
            this.flashSalePrice = flashSalePrice.setScale(2, RoundingMode.HALF_UP);
            calculateDiscountPercentage();
        }
    }

    public int getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(int initialStock) {
        if (initialStock >= 0) {
            this.initialStock = initialStock;
        }
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        if (availableStock >= 0) {
            this.availableStock = availableStock;
            updateStatus();
        }
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public String getStatus() {
        return status;
    }

    public String getFormattedOriginalPrice() {
        return CurrencyUtil.formatRupee(originalPrice);
    }

    public String getFormattedFlashSalePrice() {
        return CurrencyUtil.formatRupee(flashSalePrice);
    }

    public String getDiscountBadge() {
        return String.format("%.0f%% OFF", discountPercentage);
    }

    public boolean isAvailable(int requestedQty) {
        return requestedQty > 0 && this.availableStock >= requestedQty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", productId, name, CurrencyUtil.formatRupee(flashSalePrice));
    }
}
