package com.flashdeal.service;

import com.flashdeal.inventory.InventoryService;
import com.flashdeal.model.Order;
import com.flashdeal.model.OrderStatus;
import com.flashdeal.model.Product;
import com.flashdeal.model.SaleMetric;
import com.flashdeal.persistence.FileStorageService;
import com.flashdeal.persistence.OrderRepository;
import com.flashdeal.persistence.ProductRepository;
import com.flashdeal.util.CurrencyUtil;
import com.flashdeal.util.DateUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service calculating analytics, product breakdown, revenue summaries,
 * and generating CSV sales reports.
 */
public class SalesReportService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public SalesReportService(OrderRepository orderRepository,
                              ProductRepository productRepository,
                              InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    public int getTotalAttempts() {
        return orderRepository.getOrderCount();
    }

    public int getSuccessfulPurchasesCount() {
        int count = 0;
        for (Order o : orderRepository.getAllOrders()) {
            if (o.getStatus() == OrderStatus.SUCCESS) count++;
        }
        return count;
    }

    public int getFailedPurchasesCount() {
        int count = 0;
        for (Order o : orderRepository.getAllOrders()) {
            if (o.getStatus() != OrderStatus.SUCCESS) count++;
        }
        return count;
    }

    public int getTotalUnitsSold() {
        int units = 0;
        for (Order o : orderRepository.getAllOrders()) {
            if (o.getStatus() == OrderStatus.SUCCESS) {
                units += o.getQuantity();
            }
        }
        return units;
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.calculateSuccessfulRevenue();
    }

    public BigDecimal getTotalDiscountsGranted() {
        BigDecimal total = BigDecimal.ZERO;
        for (Order o : orderRepository.getAllOrders()) {
            if (o.getStatus() == OrderStatus.SUCCESS) {
                total = total.add(o.getDiscountAmount());
            }
        }
        return total;
    }

    public double getOverallSuccessRate() {
        int total = getTotalAttempts();
        if (total == 0) return 0.0;
        return ((double) getSuccessfulPurchasesCount() / total) * 100.0;
    }

    /**
     * Aggregates sales metrics for all products.
     */
    public Map<String, SaleMetric> getProductMetrics() {
        Map<String, SaleMetric> metrics = new LinkedHashMap<>();

        // Initialize entries for all products
        for (Product p : productRepository.getAllProducts()) {
            metrics.put(p.getProductId(), new SaleMetric(p.getProductId(), p.getName()));
        }

        // Aggregate orders using an explicit Iterator traversal
        List<Order> orders = orderRepository.getAllOrders();
        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            SaleMetric metric = metrics.get(order.getProductId());
            if (metric == null) {
                metric = new SaleMetric(order.getProductId(), order.getProductName());
                metrics.put(order.getProductId(), metric);
            }
            boolean isSuccess = (order.getStatus() == OrderStatus.SUCCESS);
            metric.recordAttempt(isSuccess, order.getQuantity(), order.getFinalAmount(), order.getDiscountAmount());
        }

        return metrics;
    }

    public SaleMetric getProductSalesSummary(String productId) {
        return getProductMetrics().get(productId);
    }

    public Product getMostPopularProduct() {
        Map<String, SaleMetric> metrics = getProductMetrics();
        SaleMetric topMetric = null;
        for (SaleMetric m : metrics.values()) {
            if (topMetric == null || m.getUnitsSold() > topMetric.getUnitsSold()) {
                topMetric = m;
            }
        }
        if (topMetric != null && topMetric.getUnitsSold() > 0) {
            return productRepository.findById(topMetric.getProductId());
        }
        return null;
    }

    /**
     * Exports the comprehensive sales analytics and product performance to a CSV file.
     */
    public File exportReportToCsv(File targetFile) throws IOException {
        if (targetFile == null) {
            targetFile = new File("data/sales_history_" + System.currentTimeMillis() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile, StandardCharsets.UTF_8))) {
            writer.write("FLASHDEAL PRO - BIG SALE DAY ANALYTICS REPORT");
            writer.newLine();
            writer.write("Generated At," + DateUtil.formatDisplay(LocalDateTime.now()));
            writer.newLine();
            writer.newLine();

            // Overall Summary
            writer.write("METRIC,VALUE");
            writer.newLine();
            writer.write("Total Purchase Attempts," + getTotalAttempts());
            writer.newLine();
            writer.write("Successful Orders," + getSuccessfulPurchasesCount());
            writer.newLine();
            writer.write("Failed / Out of Stock Orders," + getFailedPurchasesCount());
            writer.newLine();
            writer.write("Total Units Sold," + getTotalUnitsSold());
            writer.newLine();
            writer.write(String.format("Total Revenue (INR),%.2f", getTotalRevenue()));
            writer.newLine();
            writer.write(String.format("Total Discounts Granted (INR),%.2f", getTotalDiscountsGranted()));
            writer.newLine();
            writer.write(String.format("Success Rate,%.2f%%", getOverallSuccessRate()));
            writer.newLine();
            Product popular = getMostPopularProduct();
            writer.write("Most Popular Product," + (popular != null ? FileStorageService.escapeCsvField(popular.getName()) : "N/A"));
            writer.newLine();
            writer.newLine();

            // Product-wise table
            writer.write("PRODUCT PERFORMANCE BREAKDOWN");
            writer.newLine();
            writer.write("ProductId,ProductName,TotalAttempts,SuccessfulOrders,FailedOrders,UnitsSold,RemainingStock,RevenueINR,DiscountsINR,SuccessRate");
            writer.newLine();

            for (SaleMetric m : getProductMetrics().values()) {
                int liveStock = inventoryService.getAvailableStock(m.getProductId());
                writer.write(String.format("%s,%s,%d,%d,%d,%d,%d,%.2f,%.2f,%.2f%%",
                        FileStorageService.escapeCsvField(m.getProductId()),
                        FileStorageService.escapeCsvField(m.getProductName()),
                        m.getTotalAttempts(),
                        m.getSuccessfulOrders(),
                        m.getFailedOrders(),
                        m.getUnitsSold(),
                        liveStock,
                        m.getTotalRevenue(),
                        m.getTotalDiscounts(),
                        m.getSuccessRate()
                ));
                writer.newLine();
            }
        }
        return targetFile;
    }
}
