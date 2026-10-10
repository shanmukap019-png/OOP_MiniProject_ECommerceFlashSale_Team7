package com.flashdeal.concurrency;

import com.flashdeal.model.Customer;
import com.flashdeal.model.Product;
import com.flashdeal.service.FlashSaleService;

import java.util.concurrent.CountDownLatch;

/**
 * Task representing an individual customer executing a concurrent purchase attempt.
 * Implements Runnable to be executed within a dedicated Thread or ExecutorService.
 */
public class CustomerPurchaseTask implements Runnable {
    private final Customer customer;
    private final Product product;
    private final int quantity;
    private final String couponCode;
    private final FlashSaleService flashSaleService;
    private final CountDownLatch startLatch;
    private final CountDownLatch completionLatch;

    public CustomerPurchaseTask(Customer customer, Product product, int quantity, String couponCode,
                                FlashSaleService flashSaleService,
                                CountDownLatch startLatch, CountDownLatch completionLatch) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.couponCode = couponCode;
        this.flashSaleService = flashSaleService;
        this.startLatch = startLatch;
        this.completionLatch = completionLatch;
    }

    @Override
    public void run() {
        try {
            // Await synchronization barrier so all customer threads start racing simultaneously
            if (startLatch != null) {
                startLatch.await();
            }

            // Execute the purchase transaction through the thread-safe flash sale service
            flashSaleService.processPurchase(customer, product, quantity, couponCode);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Unexpected exception in CustomerPurchaseTask: " + e.getMessage());
        } finally {
            if (completionLatch != null) {
                completionLatch.countDown();
            }
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
