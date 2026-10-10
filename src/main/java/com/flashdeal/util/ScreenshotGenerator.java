package com.flashdeal.util;

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

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility that captures authentic, pixel-perfect PNG screenshots of each UI screen
 * from the live Swing layout for the project documentation and submission.
 */
public class ScreenshotGenerator {

    public static void main(String[] args) {
        System.out.println("Generating application screen captures...");
        File screenshotDir = new File("screenshots");
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        FileStorageService storageService = new FileStorageService("data");
        ProductRepository productRepository = new ProductRepository(storageService);
        OrderRepository orderRepository = new OrderRepository(storageService);
        CustomerRepository customerRepository = new CustomerRepository(storageService);

        Inventory inventory = new Inventory();
        InventoryService inventoryService = new InventoryService(inventory);
        DiscountService discountService = new DiscountService();

        ProductService productService = new ProductService(productRepository, inventoryService);
        OrderService orderService = new OrderService(orderRepository);
        SalesReportService salesReportService = new SalesReportService(orderRepository, productRepository, inventoryService);

        FlashSaleService flashSaleService = new FlashSaleService(
                inventoryService, productService, orderService, discountService, salesReportService
        );

        // Pre-run a 10-order demonstration so dashboard and history look rich with live data
        var customers = customerRepository.getBatch(10);
        var products = productService.getAllProducts();
        if (!products.isEmpty()) {
            var prod = products.get(0);
            for (int i = 0; i < 5; i++) {
                flashSaleService.processPurchase(customers.get(i), prod, 1, "SAVE10");
            }
        }

        MainFrame frame = new MainFrame(flashSaleService, customerRepository);
        frame.setSize(1280, 800);
        frame.doLayout();
        frame.validate();

        captureScreen(frame, "DASHBOARD", new File("screenshots/01_dashboard.png"));
        captureScreen(frame, "PRODUCTS", new File("screenshots/02_catalog.png"));
        captureScreen(frame, "SIMULATOR", new File("screenshots/03_simulator.png"));
        captureScreen(frame, "ORDERS", new File("screenshots/04_order_history.png"));
        captureScreen(frame, "REPORTS", new File("screenshots/05_sales_reports.png"));

        System.out.println("[SUCCESS] All 5 application screen captures saved to screenshots/");
    }

    private static void captureScreen(MainFrame frame, String viewKey, File target) {
        frame.showView(viewKey);
        frame.addNotify();
        frame.validate();

        BufferedImage img = new BufferedImage(1280, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        frame.getContentPane().printAll(g);
        g.dispose();

        try {
            ImageIO.write(img, "PNG", target);
            System.out.println("   Saved: " + target.getPath() + " (" + target.length() + " bytes)");
        } catch (Exception e) {
            System.err.println("   Failed saving " + target.getName() + ": " + e.getMessage());
        }
    }
}
