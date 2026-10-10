package com.flashdeal.model;

import java.util.concurrent.CountDownLatch;

/**
 * Customer model representing a buyer in the flash sale.
 * Implements Runnable to allow concurrent purchase simulation threads.
 */
public class Customer implements Runnable {
    private String customerId;
    private String name;
    private String email;
    private String phone;

    // Simulation context fields (transient / used during concurrent runs)
    private transient Runnable purchaseAction;
    private transient CountDownLatch startLatch;

    public Customer() {
    }

    public Customer(String customerId, String name, String email, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public void setSimulationContext(CountDownLatch startLatch, Runnable purchaseAction) {
        this.startLatch = startLatch;
        this.purchaseAction = purchaseAction;
    }

    @Override
    public void run() {
        try {
            // Await synchronization latch to ensure all customer threads launch simultaneously
            if (startLatch != null) {
                startLatch.await();
            }
            if (purchaseAction != null) {
                purchaseAction.run();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " (" + customerId + ")";
    }
}
