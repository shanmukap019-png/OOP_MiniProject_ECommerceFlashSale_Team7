package com.flashdeal.persistence;

import com.flashdeal.model.Order;
import com.flashdeal.model.OrderStatus;
import com.flashdeal.util.DateUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Repository for managing flash sale orders and transactions.
 * Persists orders to data/orders.csv and demonstrates Java Iterator traversal.
 */
public class OrderRepository {
    private static final String FILE_NAME = "orders.csv";
    private static final String CSV_HEADER = "orderId,customerId,customerName,productId,productName,quantity,originalAmount,discountAmount,finalAmount,timestamp,status,failureReason";

    private final FileStorageService storageService;
    private final List<Order> orderList;
    private final Map<String, Order> orderMap;
    private final Object orderLock = new Object();

    public OrderRepository(FileStorageService storageService) {
        this.storageService = storageService;
        this.orderList = new ArrayList<>();
        this.orderMap = new LinkedHashMap<>();
        loadOrders();
    }

    private void loadOrders() {
        synchronized (orderLock) {
            orderList.clear();
            orderMap.clear();

            try {
                List<String> lines = storageService.readLines(FILE_NAME);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (i == 0 && line.toLowerCase().startsWith("orderid")) {
                        continue; // skip header
                    }
                    Order order = parseOrderFromCsv(line);
                    if (order != null) {
                        orderList.add(order);
                        orderMap.put(order.getOrderId(), order);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading orders.csv: " + e.getMessage());
            }
        }
    }

    private Order parseOrderFromCsv(String line) {
        try {
            List<String> tokens = FileStorageService.parseCsvLine(line);
            if (tokens.size() < 11) return null;

            String orderId = tokens.get(0);
            String customerId = tokens.get(1);
            String customerName = tokens.get(2);
            String productId = tokens.get(3);
            String productName = tokens.get(4);
            int qty = Integer.parseInt(tokens.get(5));
            BigDecimal origAmt = new BigDecimal(tokens.get(6));
            BigDecimal discAmt = new BigDecimal(tokens.get(7));
            BigDecimal finalAmt = new BigDecimal(tokens.get(8));
            LocalDateTime timestamp = DateUtil.parseIso(tokens.get(9));
            OrderStatus status = OrderStatus.fromString(tokens.get(10));
            String reason = tokens.size() > 11 ? tokens.get(11) : "";

            return new Order(orderId, customerId, customerName, productId, productName,
                    qty, origAmt, discAmt, finalAmt, timestamp, status, reason);
        } catch (Exception e) {
            System.err.println("Skipping malformed order record: " + line);
            return null;
        }
    }

    /**
     * Thread-safe saving of an order. Appends to orders.csv and updates in-memory collection.
     */
    public void save(Order order) {
        if (order == null) return;
        synchronized (orderLock) {
            orderList.add(0, order); // Add to beginning for recent-first order display
            orderMap.put(order.getOrderId(), order);

            String csvLine = String.format("%s,%s,%s,%s,%s,%d,%.2f,%.2f,%.2f,%s,%s,%s",
                    FileStorageService.escapeCsvField(order.getOrderId()),
                    FileStorageService.escapeCsvField(order.getCustomerId()),
                    FileStorageService.escapeCsvField(order.getCustomerName()),
                    FileStorageService.escapeCsvField(order.getProductId()),
                    FileStorageService.escapeCsvField(order.getProductName()),
                    order.getQuantity(),
                    order.getOriginalAmount(),
                    order.getDiscountAmount(),
                    order.getFinalAmount(),
                    DateUtil.formatIso(order.getTimestamp()),
                    order.getStatus().name(),
                    FileStorageService.escapeCsvField(order.getFailureReason())
            );

            try {
                storageService.appendLine(FILE_NAME, CSV_HEADER, csvLine);
            } catch (IOException e) {
                System.err.println("Failed to append order to CSV: " + e.getMessage());
            }
        }
    }

    public List<Order> getAllOrders() {
        synchronized (orderLock) {
            return new ArrayList<>(orderList);
        }
    }

    /**
     * Demonstrates explicit use of Java Iterator to traverse orders and filter by product and status.
     * Satisfies University requirement: "Use an Iterator to traverse a collection in at least one meaningful operation."
     */
    public List<Order> filterOrders(String productIdFilter, OrderStatus statusFilter) {
        synchronized (orderLock) {
            List<Order> filtered = new ArrayList<>();
            // Explicit Iterator traversal
            Iterator<Order> iterator = orderList.iterator();
            while (iterator.hasNext()) {
                Order order = iterator.next();
                boolean matchesProduct = (productIdFilter == null || productIdFilter.equalsIgnoreCase("ALL")
                        || order.getProductId().equalsIgnoreCase(productIdFilter));
                boolean matchesStatus = (statusFilter == null || order.getStatus() == statusFilter);

                if (matchesProduct && matchesStatus) {
                    filtered.add(order);
                }
            }
            return filtered;
        }
    }

    /**
     * Calculates total revenue of SUCCESS orders using explicit Iterator traversal.
     */
    public BigDecimal calculateSuccessfulRevenue() {
        synchronized (orderLock) {
            BigDecimal total = BigDecimal.ZERO;
            Iterator<Order> iterator = orderList.iterator();
            while (iterator.hasNext()) {
                Order order = iterator.next();
                if (order.getStatus() == OrderStatus.SUCCESS) {
                    total = total.add(order.getFinalAmount());
                }
            }
            return total;
        }
    }

    public Order findById(String orderId) {
        synchronized (orderLock) {
            return orderMap.get(orderId);
        }
    }

    public int getOrderCount() {
        synchronized (orderLock) {
            return orderList.size();
        }
    }
}
