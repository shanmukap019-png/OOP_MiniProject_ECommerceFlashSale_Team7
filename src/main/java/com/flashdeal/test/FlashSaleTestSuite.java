package com.flashdeal.test;

import com.flashdeal.concurrency.FlashSaleCoordinator;
import com.flashdeal.concurrency.FlashSaleListener;
import com.flashdeal.discount.DiscountService;
import com.flashdeal.inventory.Inventory;
import com.flashdeal.inventory.InventoryService;
import com.flashdeal.inventory.OutOfStockException;
import com.flashdeal.model.Customer;
import com.flashdeal.model.Order;
import com.flashdeal.model.OrderStatus;
import com.flashdeal.model.Product;
import com.flashdeal.persistence.CustomerRepository;
import com.flashdeal.persistence.FileStorageService;
import com.flashdeal.persistence.OrderRepository;
import com.flashdeal.persistence.ProductRepository;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.service.OrderService;
import com.flashdeal.service.ProductService;
import com.flashdeal.service.SalesReportService;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Comprehensive Automated Concurrency & Unit Test Suite for FLASHDEAL PRO.
 * Validates all 15 required scenarios including high-concurrency race condition testing.
 */
public class FlashSaleTestSuite {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("=======================================================================");
        System.out.println("   FLASHDEAL PRO - AUTOMATED CONCURRENCY & UNIT TEST SUITE");
        System.out.println("   University: MNR University | Project #7: E-Commerce Flash Sale");
        System.out.println("=======================================================================\n");

        runTest("1. Successful purchase", FlashSaleTestSuite::test1_SuccessfulPurchase);
        runTest("2. Purchase with insufficient stock", FlashSaleTestSuite::test2_InsufficientStockPurchase);
        runTest("3. Inventory never becoming negative", FlashSaleTestSuite::test3_InventoryNeverBecomesNegative);
        runTest("4. Concurrent purchase attempts (Race condition stress test)", FlashSaleTestSuite::test4_ConcurrentPurchases);
        runTest("5. Invalid quantity handling", FlashSaleTestSuite::test5_InvalidQuantity);
        runTest("6. Percentage discount calculation & capping", FlashSaleTestSuite::test6_PercentageDiscount);
        runTest("7. Flat discount calculation & threshold", FlashSaleTestSuite::test7_FlatDiscount);
        runTest("8. Bank offer discount calculation & limits", FlashSaleTestSuite::test8_BankOfferDiscount);
        runTest("9. Invalid coupon handling", FlashSaleTestSuite::test9_InvalidCoupon);
        runTest("10. Successful order recording & status", FlashSaleTestSuite::test10_SuccessfulOrderRecording);
        runTest("11. Failed order recording & status", FlashSaleTestSuite::test11_FailedOrderRecording);
        runTest("12. Revenue calculation consistency", FlashSaleTestSuite::test12_RevenueCalculation);
        runTest("13. CSV file persistence integrity", FlashSaleTestSuite::test13_CsvPersistence);
        runTest("14. Reloading inventory after restart", FlashSaleTestSuite::test14_ReloadInventoryAfterRestart);
        runTest("15. CSV report export generation", FlashSaleTestSuite::test15_CsvReportExport);

        System.out.println("\n=======================================================================");
        System.out.printf("   TEST EXECUTION SUMMARY: %d Total | %d Passed | %d Failed\n", totalTests, passedTests, failedTests);
        System.out.println("=======================================================================");

        if (failedTests > 0) {
            System.err.println("\n[FAILURE] One or more automated tests failed!");
            System.exit(1);
        } else {
            System.out.println("\n[SUCCESS] ALL 15 AUTOMATED TEST SUITE SCENARIOS PASSED PERFECTLY!");
            System.exit(0);
        }
    }

    private static void runTest(String name, TestRunnable test) {
        totalTests++;
        System.out.printf("[RUNNING] Test %-60s ", name);
        try {
            test.run();
            passedTests++;
            System.out.println("[\u2714 PASSED]");
        } catch (Throwable t) {
            failedTests++;
            System.out.println("[\u2718 FAILED]");
            System.err.println("   --> Error: " + t.getMessage());
            t.printStackTrace(System.err);
        }
    }

    @FunctionalInterface
    interface TestRunnable {
        void run() throws Exception;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected instanceof BigDecimal && actual instanceof BigDecimal) {
            if (((BigDecimal) expected).compareTo((BigDecimal) actual) == 0) return;
        }
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(String.format("%s [Expected: %s, Actual: %s]", message, expected, actual));
    }

    // =========================================================================
    // TEST IMPLEMENTATIONS
    // =========================================================================

    private static void test1_SuccessfulPurchase() throws Exception {
        Inventory inv = new Inventory();
        inv.setStock("TEST-P1", 10);
        int rem = inv.deductStock("TEST-P1", 2);
        assertEquals(8, rem, "Remaining stock after deducting 2 from 10 should be 8");
        assertEquals(8, inv.getStock("TEST-P1"), "Live stock should reflect 8");
    }

    private static void test2_InsufficientStockPurchase() {
        Inventory inv = new Inventory();
        inv.setStock("TEST-P2", 2);
        try {
            inv.deductStock("TEST-P2", 5);
            throw new AssertionError("Should have thrown OutOfStockException when requesting 5 with stock 2");
        } catch (OutOfStockException e) {
            assertEquals(2, e.getAvailableQuantity(), "Reported available quantity should be 2");
            assertEquals(5, e.getRequestedQuantity(), "Reported requested quantity should be 5");
            assertEquals(2, inv.getStock("TEST-P2"), "Stock should remain unchanged at 2");
        }
    }

    private static void test3_InventoryNeverBecomesNegative() {
        Inventory inv = new Inventory();
        inv.setStock("TEST-P3", 1);
        try {
            inv.deductStock("TEST-P3", 2);
        } catch (OutOfStockException expected) {
            // expected
        }
        assertTrue(inv.getStock("TEST-P3") >= 0, "Inventory must never become negative. Current: " + inv.getStock("TEST-P3"));
        assertEquals(1, inv.getStock("TEST-P3"), "Inventory should remain 1");
    }

    private static void test4_ConcurrentPurchases() throws Exception {
        // Core demonstration scenario: 10 units in stock, 50 concurrent customer threads
        String testDir = "build_test_data_" + System.currentTimeMillis();
        FileStorageService storage = new FileStorageService(testDir);
        ProductRepository prodRepo = new ProductRepository(storage);
        OrderRepository orderRepo = new OrderRepository(storage);

        Inventory inventory = new Inventory();
        InventoryService invService = new InventoryService(inventory);
        DiscountService discService = new DiscountService();
        ProductService prodService = new ProductService(prodRepo, invService);
        OrderService ordService = new OrderService(orderRepo);
        SalesReportService repService = new SalesReportService(orderRepo, prodRepo, invService);

        FlashSaleService flashService = new FlashSaleService(invService, prodService, ordService, discService, repService);

        Product headphone = new Product("RACE-101", "Wireless Headphones", "High fidelity", "Audio",
                new BigDecimal("2999.00"), new BigDecimal("1499.00"), 10);
        prodRepo.saveOrUpdate(headphone);
        invService.setStock("RACE-101", 10);

        int totalCustomers = 50;
        int initialStock = 10;
        int qtyPerCustomer = 1;

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalCustomers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger oosCount = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= totalCustomers; i++) {
            final int id = i;
            Customer cust = new Customer("C-" + id, "Customer " + id, "c" + id + "@test.com", "9999999999");
            Thread t = new Thread(() -> {
                try {
                    startLatch.await(); // All threads wait here
                    Order order = flashService.processPurchase(cust, headphone, qtyPerCustomer, null);
                    if (order.getStatus() == OrderStatus.SUCCESS) {
                        successCount.incrementAndGet();
                    } else if (order.getStatus() == OrderStatus.OUT_OF_STOCK) {
                        oosCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
            threads.add(t);
            t.start();
        }

        // Release barrier simultaneously!
        startLatch.countDown();
        boolean finished = doneLatch.await(10, TimeUnit.SECONDS);

        assertTrue(finished, "All 50 customer threads should complete within timeout");
        assertEquals(initialStock, successCount.get(), "Exact 10 successful purchases expected");
        assertEquals(totalCustomers - initialStock, oosCount.get(), "Exact 40 out-of-stock rejections expected");
        assertEquals(0, invService.getAvailableStock("RACE-101"), "Final inventory must be strictly 0");
        assertTrue(invService.getAvailableStock("RACE-101") >= 0, "Inventory must never be negative");

        cleanDirectory(new File(testDir));
    }

    private static void test5_InvalidQuantity() {
        Inventory inv = new Inventory();
        inv.setStock("TEST-P5", 10);
        try {
            inv.deductStock("TEST-P5", 0);
            throw new AssertionError("Should reject zero quantity");
        } catch (IllegalArgumentException expected) {
            // pass
        } catch (Exception e) {
            throw new AssertionError("Expected IllegalArgumentException but got: " + e);
        }

        try {
            inv.deductStock("TEST-P5", -5);
            throw new AssertionError("Should reject negative quantity");
        } catch (IllegalArgumentException expected) {
            // pass
        } catch (Exception e) {
            throw new AssertionError("Expected IllegalArgumentException but got: " + e);
        }
    }

    private static void test6_PercentageDiscount() {
        DiscountService service = new DiscountService();
        // SAVE10: 10% off, min ₹500, max ₹300
        BigDecimal d1 = service.computeDiscount("SAVE10", new BigDecimal("1500.00"), 1);
        assertEquals(new BigDecimal("150.00"), d1, "10% of 1500 should be 150.00");

        // Capping at ₹300
        BigDecimal d2 = service.computeDiscount("SAVE10", new BigDecimal("4000.00"), 1);
        assertEquals(new BigDecimal("300.00"), d2, "10% of 4000 is 400, but capped at 300.00");

        // Below minimum ₹500
        BigDecimal d3 = service.computeDiscount("SAVE10", new BigDecimal("400.00"), 1);
        assertEquals(BigDecimal.ZERO, d3, "Order below minimum should receive 0.00 discount");
    }

    private static void test7_FlatDiscount() {
        DiscountService service = new DiscountService();
        // FLAT100: Flat ₹100 on min ₹999
        BigDecimal d1 = service.computeDiscount("FLAT100", new BigDecimal("1200.00"), 1);
        assertEquals(new BigDecimal("100.00"), d1, "Eligible order should receive 100.00 discount");

        BigDecimal d2 = service.computeDiscount("FLAT100", new BigDecimal("800.00"), 1);
        assertEquals(BigDecimal.ZERO, d2, "Ineligible order below 999 should receive 0.00 discount");
    }

    private static void test8_BankOfferDiscount() {
        DiscountService service = new DiscountService();
        // BANK15: 15% instant bank discount, min ₹1500, max ₹750
        BigDecimal d1 = service.computeDiscount("BANK15", new BigDecimal("2000.00"), 1);
        assertEquals(new BigDecimal("300.00"), d1, "15% of 2000 should be 300.00");

        // Capped at ₹750
        BigDecimal d2 = service.computeDiscount("BANK15", new BigDecimal("6000.00"), 1);
        assertEquals(new BigDecimal("750.00"), d2, "15% of 6000 is 900, but capped at 750.00");
    }

    private static void test9_InvalidCoupon() {
        DiscountService service = new DiscountService();
        BigDecimal d = service.computeDiscount("INVALID_XYZ", new BigDecimal("5000.00"), 2);
        assertEquals(BigDecimal.ZERO, d, "Invalid coupon code must yield 0.00 discount");
    }

    private static void test10_SuccessfulOrderRecording() {
        String testDir = "build_test_data_" + System.currentTimeMillis();
        FileStorageService storage = new FileStorageService(testDir);
        OrderRepository repo = new OrderRepository(storage);

        Order o = new Order("ORD-TEST-1", "C-1", "Aarav", "P-1", "Product 1", 1,
                new BigDecimal("1000.00"), new BigDecimal("100.00"), new BigDecimal("900.00"),
                java.time.LocalDateTime.now(), OrderStatus.SUCCESS, "");

        repo.save(o);
        assertEquals(1, repo.getOrderCount(), "Order count should be 1");
        Order fetched = repo.findById("ORD-TEST-1");
        assertTrue(fetched != null, "Fetched order must not be null");
        assertEquals(OrderStatus.SUCCESS, fetched.getStatus(), "Status must be SUCCESS");

        cleanDirectory(new File(testDir));
    }

    private static void test11_FailedOrderRecording() {
        String testDir = "build_test_data_" + System.currentTimeMillis();
        FileStorageService storage = new FileStorageService(testDir);
        OrderRepository repo = new OrderRepository(storage);

        Order o = new Order("ORD-FAIL-1", "C-2", "Diya", "P-1", "Product 1", 3,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                java.time.LocalDateTime.now(), OrderStatus.OUT_OF_STOCK, "Stock unavailable");

        repo.save(o);
        Order fetched = repo.findById("ORD-FAIL-1");
        assertEquals(OrderStatus.OUT_OF_STOCK, fetched.getStatus(), "Status should be OUT_OF_STOCK");
        assertEquals(BigDecimal.ZERO, fetched.getFinalAmount(), "Failed order final amount should be 0.00");

        cleanDirectory(new File(testDir));
    }

    private static void test12_RevenueCalculation() {
        String testDir = "build_test_data_" + System.currentTimeMillis();
        FileStorageService storage = new FileStorageService(testDir);
        OrderRepository repo = new OrderRepository(storage);

        // 2 success orders: ₹1,500 + ₹2,500 = ₹4,000
        repo.save(new Order("ORD-1", "C-1", "A", "P1", "P", 1, new BigDecimal("1500"), BigDecimal.ZERO, new BigDecimal("1500"), java.time.LocalDateTime.now(), OrderStatus.SUCCESS, ""));
        repo.save(new Order("ORD-2", "C-2", "B", "P1", "P", 1, new BigDecimal("2500"), BigDecimal.ZERO, new BigDecimal("2500"), java.time.LocalDateTime.now(), OrderStatus.SUCCESS, ""));
        // 1 failed order with amount: must NOT be counted in revenue
        repo.save(new Order("ORD-3", "C-3", "C", "P1", "P", 1, new BigDecimal("5000"), BigDecimal.ZERO, new BigDecimal("5000"), java.time.LocalDateTime.now(), OrderStatus.OUT_OF_STOCK, "OOS"));

        BigDecimal rev = repo.calculateSuccessfulRevenue();
        assertEquals(new BigDecimal("4000.00"), rev.setScale(2), "Total revenue must only sum SUCCESS orders (4000.00)");

        cleanDirectory(new File(testDir));
    }

    private static void test13_CsvPersistence() throws Exception {
        String testDir = "build_test_data_" + System.currentTimeMillis();
        FileStorageService storage = new FileStorageService(testDir);

        List<String> lines = new ArrayList<>();
        lines.add("PROD-1,Sample Item,Desc,Category,1000.00,800.00,10,10,IN_STOCK");
        storage.writeLines("test_products.csv", "header", lines);

        List<String> read = storage.readLines("test_products.csv");
        assertTrue(read.size() >= 2, "Read lines should contain header and record");

        cleanDirectory(new File(testDir));
    }

    private static void test14_ReloadInventoryAfterRestart() {
        String testDir = "build_test_data_" + System.currentTimeMillis();
        FileStorageService storage = new FileStorageService(testDir);
        ProductRepository repo1 = new ProductRepository(storage);

        Product p = repo1.getAllProducts().get(0);
        String pid = p.getProductId();
        repo1.updateStock(pid, 7);

        // Simulate app restart by instantiating new repo pointing to same files
        ProductRepository repo2 = new ProductRepository(storage);
        Product reloaded = repo2.findById(pid);
        assertEquals(7, reloaded.getAvailableStock(), "Persisted stock after reload must be 7");

        cleanDirectory(new File(testDir));
    }

    private static void test15_CsvReportExport() throws Exception {
        String testDir = "build_test_data_" + System.currentTimeMillis();
        FileStorageService storage = new FileStorageService(testDir);
        ProductRepository prodRepo = new ProductRepository(storage);
        OrderRepository orderRepo = new OrderRepository(storage);
        InventoryService invService = new InventoryService(new Inventory());

        SalesReportService repService = new SalesReportService(orderRepo, prodRepo, invService);
        File exportFile = new File(testDir + "/exported_report.csv");
        File result = repService.exportReportToCsv(exportFile);

        assertTrue(result.exists() && result.length() > 0, "Exported CSV report must exist and not be empty");
        String content = Files.readString(result.toPath());
        assertTrue(content.contains("FLASHDEAL PRO - BIG SALE DAY ANALYTICS REPORT"), "Report must contain header banner");
        assertTrue(content.contains("PRODUCT PERFORMANCE BREAKDOWN"), "Report must contain product breakdown");

        cleanDirectory(new File(testDir));
    }

    private static void cleanDirectory(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            dir.delete();
        }
    }
}
