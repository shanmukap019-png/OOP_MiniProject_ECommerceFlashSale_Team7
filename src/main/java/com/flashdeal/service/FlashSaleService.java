package com.flashdeal.service;

import com.flashdeal.concurrency.FlashSaleListener;
import com.flashdeal.discount.DiscountService;
import com.flashdeal.inventory.InventoryService;
import com.flashdeal.inventory.OutOfStockException;
import com.flashdeal.model.Customer;
import com.flashdeal.model.Order;
import com.flashdeal.model.OrderStatus;
import com.flashdeal.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Core business transaction service managing flash sale orders, stock deductions,
 * discount applications, and event listener notifications.
 */
public class FlashSaleService {
    private final InventoryService inventoryService;
    private final ProductService productService;
    private final OrderService orderService;
    private final DiscountService discountService;
    private final SalesReportService salesReportService;

    private static final AtomicLong ORDER_ID_SEQUENCE = new AtomicLong(System.currentTimeMillis() % 1000000);
    private FlashSaleListener saleListener;

    public FlashSaleService(InventoryService inventoryService,
                            ProductService productService,
                            OrderService orderService,
                            DiscountService discountService,
                            SalesReportService salesReportService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
        this.orderService = orderService;
        this.discountService = discountService;
        this.salesReportService = salesReportService;
    }

    public synchronized void setSaleListener(FlashSaleListener listener) {
        this.saleListener = listener;
    }

    private String generateOrderId() {
        return "ORD-" + ORDER_ID_SEQUENCE.incrementAndGet();
    }

    /**
     * Processes an individual purchase attempt atomically and thread-safely.
     *
     * @param customer          The purchasing customer.
     * @param product           The targeted product.
     * @param requestedQuantity Number of units requested.
     * @param couponCode        Optional coupon discount code.
     * @return Generated Order record reflecting SUCCESS or OUT_OF_STOCK/FAILED.
     */
    public Order processPurchase(Customer customer, Product product, int requestedQuantity, String couponCode) {
        if (customer == null) {
            customer = new Customer("CUST-GUEST", "Guest Customer", "guest@flashdeal.com", "+91 0000000000");
        }

        // Notify attempt
        FlashSaleListener listener = this.saleListener;
        if (listener != null) {
            listener.onPurchaseAttempt(customer, product, requestedQuantity);
        }

        // 1. Input validations
        if (requestedQuantity <= 0) {
            Order invalidOrder = new Order(
                    generateOrderId(), customer.getCustomerId(), customer.getName(),
                    product != null ? product.getProductId() : "N/A",
                    product != null ? product.getName() : "Unknown Product",
                    requestedQuantity, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    LocalDateTime.now(), OrderStatus.FAILED, "Invalid quantity requested (" + requestedQuantity + ")"
            );
            orderService.recordOrder(invalidOrder);
            if (listener != null) {
                listener.onPurchaseFailed(customer, product, requestedQuantity, "Invalid quantity requested");
            }
            return invalidOrder;
        }

        if (product == null) {
            Order invalidOrder = new Order(
                    generateOrderId(), customer.getCustomerId(), customer.getName(),
                    "N/A", "Unknown Product", requestedQuantity,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    LocalDateTime.now(), OrderStatus.FAILED, "Product not found"
            );
            orderService.recordOrder(invalidOrder);
            if (listener != null) {
                listener.onPurchaseFailed(customer, null, requestedQuantity, "Product not found");
            }
            return invalidOrder;
        }

        // 2. ATOMIC Inventory Check & Deduction
        int remainingStock;
        try {
            remainingStock = inventoryService.deductStockAtomically(product.getProductId(), requestedQuantity);
        } catch (OutOfStockException e) {
            // Stock insufficient -> create and record OUT_OF_STOCK order
            Order oosOrder = new Order(
                    generateOrderId(), customer.getCustomerId(), customer.getName(),
                    product.getProductId(), product.getName(), requestedQuantity,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    LocalDateTime.now(), OrderStatus.OUT_OF_STOCK, e.getMessage()
            );
            orderService.recordOrder(oosOrder);
            if (listener != null) {
                listener.onPurchaseFailed(customer, product, requestedQuantity, e.getMessage());
            }
            return oosOrder;
        }

        // 3. Deduction Succeeded -> Calculate Pricing & Discounts
        BigDecimal basePrice = product.getFlashSalePrice().multiply(BigDecimal.valueOf(requestedQuantity));
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            discountAmount = discountService.computeDiscount(couponCode, basePrice, requestedQuantity);
        }
        BigDecimal finalAmount = basePrice.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // 4. Create and record SUCCESS Order
        Order successOrder = new Order(
                generateOrderId(), customer.getCustomerId(), customer.getName(),
                product.getProductId(), product.getName(), requestedQuantity,
                basePrice, discountAmount, finalAmount,
                LocalDateTime.now(), OrderStatus.SUCCESS, ""
        );

        orderService.recordOrder(successOrder);
        productService.getRepository().updateStock(product.getProductId(), remainingStock);

        if (listener != null) {
            listener.onPurchaseSuccess(successOrder, remainingStock);
        }

        return successOrder;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public DiscountService getDiscountService() {
        return discountService;
    }

    public SalesReportService getSalesReportService() {
        return salesReportService;
    }
}
