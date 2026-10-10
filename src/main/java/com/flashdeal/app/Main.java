package com.flashdeal.app;

import com.flashdeal.discount.DiscountService;
import com.flashdeal.inventory.Inventory;
import com.flashdeal.inventory.InventoryService;
import com.flashdeal.persistence.CustomerRepository;
import com.flashdeal.persistence.FileStorageService;
import com.flashdeal.persistence.OrderRepository;
import com.flashdeal.persistence.ProductRepository;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.service.OrderService;
import com.flashdeal.service.ProductService;
import com.flashdeal.service.SalesReportService;
import com.flashdeal.ui.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for FLASHDEAL PRO - E-Commerce Flash Sale Simulator.
 * University: MNR University | Course: Object-Oriented Programming (Java) | Project #7
 */
public class Main {

    public static void main(String[] args) {
        // Configure high-DPI and smooth font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to cross-platform Look and Feel gracefully
        }

        // Initialize Persistence Layer
        FileStorageService storageService = new FileStorageService("data");
        ProductRepository productRepository = new ProductRepository(storageService);
        OrderRepository orderRepository = new OrderRepository(storageService);
        CustomerRepository customerRepository = new CustomerRepository(storageService);

        // Initialize Core Domain & Thread-Safe Concurrency Services
        Inventory inventory = new Inventory();
        InventoryService inventoryService = new InventoryService(inventory);
        DiscountService discountService = new DiscountService();

        ProductService productService = new ProductService(productRepository, inventoryService);
        OrderService orderService = new OrderService(orderRepository);
        SalesReportService salesReportService = new SalesReportService(orderRepository, productRepository, inventoryService);

        FlashSaleService flashSaleService = new FlashSaleService(
                inventoryService,
                productService,
                orderService,
                discountService,
                salesReportService
        );

        // Launch Desktop UI on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(flashSaleService, customerRepository);
            mainFrame.setVisible(true);
        });
    }
}
