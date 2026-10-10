package com.flashdeal.concurrency;

import com.flashdeal.model.Customer;
import com.flashdeal.model.Product;
import com.flashdeal.service.FlashSaleService;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Coordinates concurrent flash sale simulations using multithreading and CountDownLatch synchronization.
 * Demonstrates high-concurrency race condition testing and synchronization enforcement.
 */
public class FlashSaleCoordinator {
    private final FlashSaleService flashSaleService;
    private final AtomicBoolean isSimulationRunning;
    private ExecutorService threadPool;

    public FlashSaleCoordinator(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
        this.isSimulationRunning = new AtomicBoolean(false);
    }

    /**
     * Executes a concurrent flash sale simulation with the given customers.
     * All customer purchase requests are started simultaneously using a CountDownLatch barrier.
     *
     * @param product      The targeted product.
     * @param customers    The list of participating customers.
     * @param unitsPerCust Requested quantity per customer.
     * @param couponCode   Optional coupon code to apply.
     * @param listener     Observer receiving real-time purchase updates.
     */
    public void startSimulation(Product product, List<Customer> customers, int unitsPerCust,
                                String couponCode, FlashSaleListener listener) {
        if (!isSimulationRunning.compareAndSet(false, true)) {
            throw new IllegalStateException("A flash sale simulation is already running!");
        }

        int totalCustomers = customers.size();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(totalCustomers);
        AtomicInteger completedCounter = new AtomicInteger(0);

        // Create a fixed or cached thread pool matching the concurrency load
        int poolSize = Math.min(totalCustomers, 100);
        threadPool = Executors.newFixedThreadPool(poolSize);

        // Temporarily register listener with the flash sale service for this run
        flashSaleService.setSaleListener(new FlashSaleListener() {
            @Override
            public void onPurchaseAttempt(Customer c, Product p, int qty) {
                if (listener != null) listener.onPurchaseAttempt(c, p, qty);
            }

            @Override
            public void onPurchaseSuccess(com.flashdeal.model.Order order, int remainingStock) {
                int done = completedCounter.incrementAndGet();
                if (listener != null) {
                    listener.onPurchaseSuccess(order, remainingStock);
                    listener.onSimulationProgress(done, totalCustomers, remainingStock);
                }
            }

            @Override
            public void onPurchaseFailed(Customer c, Product p, int qty, String reason) {
                int done = completedCounter.incrementAndGet();
                int currentStock = flashSaleService.getInventoryService().getAvailableStock(p.getProductId());
                if (listener != null) {
                    listener.onPurchaseFailed(c, p, qty, reason);
                    listener.onSimulationProgress(done, totalCustomers, currentStock);
                }
            }

            @Override
            public void onSimulationProgress(int done, int total, int stock) {
                if (listener != null) listener.onSimulationProgress(done, total, stock);
            }

            @Override
            public void onSimulationComplete(int total, int success, int failed, BigDecimal revenue) {
                // Delegated below
            }
        });

        // Submit all customer tasks to the thread pool
        for (Customer customer : customers) {
            CustomerPurchaseTask task = new CustomerPurchaseTask(
                    customer,
                    product,
                    unitsPerCust,
                    couponCode,
                    flashSaleService,
                    startLatch,
                    completionLatch
            );
            threadPool.submit(task);
        }

        // Run the orchestrator in a separate background thread so the UI thread remains completely responsive
        Thread orchestrator = new Thread(() -> {
            try {
                // Short pause to ensure all threads are queued and ready at the gate
                Thread.sleep(100);

                // FIRE! Release all customer threads at the exact same millisecond
                startLatch.countDown();

                // Wait until all customer threads have completed their purchase attempt
                completionLatch.await(30, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isSimulationRunning.set(false);
                if (threadPool != null) {
                    threadPool.shutdown();
                }

                // Compute aggregate run metrics and notify listener
                int currentStock = flashSaleService.getInventoryService().getAvailableStock(product.getProductId());
                var report = flashSaleService.getSalesReportService().getProductSalesSummary(product.getProductId());
                
                int successful = (report != null) ? report.getSuccessfulOrders() : 0;
                int failed = totalCustomers - successful;
                BigDecimal rev = (report != null) ? report.getTotalRevenue() : BigDecimal.ZERO;

                if (listener != null) {
                    listener.onSimulationComplete(totalCustomers, successful, failed, rev);
                }
            }
        }, "FlashSale-Coordinator-Thread");

        orchestrator.start();
    }

    public boolean isRunning() {
        return isSimulationRunning.get();
    }
}
