package com.flashdeal.model;

import com.flashdeal.util.CurrencyUtil;
import com.flashdeal.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Represents an Order created during flash sale transactions.
 */
public class Order {
    private String orderId;
    private String customerId;
    private String customerName;
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private LocalDateTime timestamp;
    private OrderStatus status;
    private String failureReason;

    public Order() {
        this.originalAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
        this.timestamp = LocalDateTime.now();
        this.status = OrderStatus.FAILED;
    }

    public Order(String orderId, String customerId, String customerName,
                 String productId, String productName, int quantity,
                 BigDecimal originalAmount, BigDecimal discountAmount, BigDecimal finalAmount,
                 LocalDateTime timestamp, OrderStatus status, String failureReason) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.originalAmount = (originalAmount != null) ? originalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.discountAmount = (discountAmount != null) ? discountAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.finalAmount = (finalAmount != null) ? finalAmount.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        this.timestamp = (timestamp != null) ? timestamp : LocalDateTime.now();
        this.status = (status != null) ? status : OrderStatus.FAILED;
        this.failureReason = (failureReason != null) ? failureReason : "";
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getFormattedFinalAmount() {
        return CurrencyUtil.formatRupee(finalAmount);
    }

    public String getFormattedDiscountAmount() {
        return CurrencyUtil.formatRupee(discountAmount);
    }

    public String getFormattedTimestamp() {
        return DateUtil.formatDisplay(timestamp);
    }

    @Override
    public String toString() {
        return String.format("Order[%s | %s | %s x%d | %s | %s]",
                orderId, customerName, productName, quantity, CurrencyUtil.formatRupee(finalAmount), status);
    }
}
