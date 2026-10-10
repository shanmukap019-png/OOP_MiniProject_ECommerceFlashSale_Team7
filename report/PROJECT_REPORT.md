# ACADEMIC MINI-PROJECT REPORT
## Object-Oriented Programming (Java)

---

### **PROJECT TITLE:**
# FLASHDEAL PRO: E-COMMERCE FLASH SALE SIMULATOR
### **THEME: BIG SALE DAY CONCURRENCY & INVENTORY SYNCHRONIZATION**

---

**Institution:** MNR University  
**Department:** Department of Computer Science & Engineering (CSE) / Artificial Intelligence & Machine Learning (AIML)  
**Course Title:** Object-Oriented Programming (Java)  
**Academic Year:** 2026  
**Assigned Project Number:** **7**  
**Submission Deadline:** 30 October 2026  
**Repository Name:** `OOP_MiniProject_ECommerceFlashSale_Team7`  

**Team Details (Team 7):**
- **Lead Student Name:** Shanmukhi  
- **Lead Student Roll Number:** **35**  
- **Team Member 2:** [Team Member 2 Name] (Roll No: [Roll No. --])  
- **Team Member 3:** [Team Member 3 Name] (Roll No: [Roll No. --])  
- **Team Member 4:** [Team Member 4 Name] (Roll No: [Roll No. --])  
- **Team Member 5:** [Team Member 5 Name] (Roll No: [Roll No. --])  

---

## 1. Title
**FLASHDEAL PRO: E-Commerce Flash Sale Simulator**  
*A Thread-Safe, Object-Oriented Desktop Application for High-Concurrency Inventory Management and Flash Sale Emulation.*

---

## 2. Aim
To design, architect, and implement a fully functional, desktop-based e-commerce flash sale simulator in Java Swing demonstrating how Object-Oriented Programming principles, multithreading (`Thread`, `Runnable`), synchronization barriers (`CountDownLatch`), and mutual exclusion (`synchronized` monitors) prevent race conditions, overselling, and negative inventory during high-volume concurrent purchase rushes.

---

## 3. Real-World Problem
During peak e-commerce sales events such as Flipkart Big Billion Days, Amazon Great Indian Festival, or flash device drops, massive spikes of shoppers attempt to acquire items with strictly limited stock within the first few seconds of sale commencement.

In software systems lacking robust synchronization:
1. **Critical Section Race Conditions:** Multiple worker threads read the same available inventory value simultaneously. If 50 threads read `stock = 1`, all 50 threads evaluate the purchase condition as true and proceed to deduct stock, resulting in `stock = -49`.
2. **Double Selling (Inventory Duplication):** The same physical inventory unit is allocated to multiple distinct customer orders.
3. **Data Inconsistency:** Ledger databases and memory caches fall out of sync, leading to severe financial reconciliation discrepancies.
4. **Thread Crashes & Freezes:** Unhandled out-of-stock exceptions or uncoordinated background work block the user interface and crash client/server systems.

---

## 4. Proposed Solution
**FLASHDEAL PRO** addresses these challenges using an object-oriented, thread-safe architecture:
- **Atomic Mutual Exclusion:** Encapsulates the inventory verification and decrement logic within a `synchronized` critical section. This guarantees that checking availability and deducting stock cannot be interleaved or split across competing threads.
- **CountDownLatch Synchronization Barrier:** Simulates real-world "flash sale rush" dynamics by holding all customer threads at a starting gate until released simultaneously.
- **Custom Exception Handling (`OutOfStockException`):** Gracefully captures out-of-stock events, transforming rejected attempts into audited failed orders without crashing worker threads or freezing the UI.
- **Polymorphic Promotional Strategies:** Implements the Strategy Pattern (`PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount`) with input validation and discount caps to prevent negative transaction prices.
- **Multi-Threaded Desktop GUI:** Decouples long-running concurrency simulations from the Swing Event Dispatch Thread (EDT) using `SwingUtilities.invokeLater`, providing live visual monitoring, progress tracking, and activity logging.
- **Lightweight File Persistence:** Automatically saves products, orders, and customer databases in local CSV files using Java's standard try-with-resources file streams.

---

## 5. Objectives
1. Implement a complete, modular, and maintainable Java application following clean architecture.
2. Demonstrate core Java OOP concepts: Encapsulation, Inheritance, Polymorphism, Abstraction, Interfaces, and Custom Exceptions.
3. Simulate multi-threaded customer purchases using `Runnable`, `Thread`, and `CountDownLatch`.
4. Guarantee that available stock never drops below zero and that no unit is sold more than once under intense concurrent load.
5. Provide a professional, responsive Swing GUI with executive dashboards, real-time activity consoles, catalog managers, order ledgers, and analytics reports.
6. Support persistent storage using CSV files without requiring external databases or paid dependencies.
7. Provide comprehensive automated unit and concurrency tests validating all system invariants.

---

## 6. Requirement Analysis

### 6.1 Functional Requirements
- **FR1 (Catalog Management):** Browse, search, filter, and adjust stock levels for at least 8 consumer electronics products in Indian Rupees (₹).
- **FR2 (Simulation Configuration):** Allow configuration of simulated customer counts (1 to 500), units requested per customer, initial stock levels, and promotional coupons.
- **FR3 (Concurrent Execution):** Spawn dedicated threads for each customer and trigger simultaneous purchase attempts via a shared barrier.
- **FR4 (Atomic Stock Deduction):** Ensure atomic check-and-decrement inventory operations using `synchronized` blocks.
- **FR5 (Discount Evaluation):** Dynamically compute valid coupon discounts (`SAVE10`, `FLAT100`, `BANK15`) while enforcing minimum order thresholds and maximum discount ceilings.
- **FR6 (Order Auditing):** Record detailed order records (`SUCCESS`, `OUT_OF_STOCK`, `FAILED`) and persist them to `orders.csv`.
- **FR7 (Analytics & CSV Export):** Compute total revenue, units sold, success rates, and product breakdown, with full CSV export capabilities.

### 6.2 Non-Functional Requirements
- **NFR1 (Thread Safety):** Zero race conditions; stock invariant `availableStock >= 0` must hold under all concurrency conditions.
- **NFR2 (Responsiveness):** Background simulation threads must never block or freeze the Swing GUI Event Dispatch Thread.
- **NFR3 (Portability & Offline Operation):** The system must execute locally on any standard JDK 17+ environment without internet access or external databases.
- **NFR4 (Robust Error Handling):** Malformed inputs, zero quantities, and missing files must be handled gracefully without silent failures.

---

## 7. System Design & Architecture

The architecture follows a Layered Modular Design ensuring separation of concerns:

```
+--------------------------------------------------------------------------+
|                             PRESENTATION LAYER                           |
|  MainFrame | DashboardPanel | ProductPanel | FlashSalePanel | ...       |
+--------------------------------------------------------------------------+
                                    |
+--------------------------------------------------------------------------+
|                              SERVICE LAYER                               |
|  FlashSaleService | ProductService | OrderService | SalesReportService   |
+--------------------------------------------------------------------------+
       |                           |                            |
+------------------+     +--------------------+     +----------------------+
| CONCURRENCY      |     | INVENTORY          |     | DISCOUNT STRATEGY    |
| Coordinator      |     | Inventory          |     | DiscountStrategyBase |
| CustomerTask     |     | InventoryService   |     | PercentageDiscount   |
| CountDownLatch   |     | OutOfStockException|     | FlatDiscount / Bank  |
+------------------+     +--------------------+     +----------------------+
                                    |
+--------------------------------------------------------------------------+
|                            PERSISTENCE LAYER                             |
|  ProductRepository | OrderRepository | CustomerRepository | FileStorage  |
+--------------------------------------------------------------------------+
                                    |
+--------------------------------------------------------------------------+
|                            DATA STORAGE (CSV)                            |
|       data/products.csv | data/orders.csv | data/customers.csv           |
+--------------------------------------------------------------------------+
```

---

## 8. UML Class Diagram

The project UML diagram models all core domain classes, inheritance hierarchies, interface contracts, and service associations.

- **Diagram File Location:** `docs/uml/class-diagram.png`
- **PlantUML Specification:** `docs/uml/class-diagram.puml`

### Key Class Relationships:
- `Product`, `Order`, `Customer`, and `SaleMetric` encapsulate domain models.
- `Customer` and `CustomerPurchaseTask` implement `java.lang.Runnable`.
- `PercentageDiscount`, `FlatDiscount`, and `BankOfferDiscount` extend `DiscountStrategyBase` which implements `DiscountStrategy`.
- `Inventory` encapsulates monitor locking for atomic stock deductions.
- `FlashSaleService` coordinates `InventoryService`, `ProductService`, `OrderService`, and `DiscountService`.
- `ProductRepository` and `OrderRepository` manage persistence using `FileStorageService`.

---

## 9. Package Structure

The codebase is organized into 9 user-defined packages under `com.flashdeal`:

1. `com.flashdeal.app` — Application entry point (`Main.java`).
2. `com.flashdeal.model` — Domain entities (`Product`, `Customer`, `Order`, `OrderStatus`, `SaleMetric`).
3. `com.flashdeal.inventory` — Thread-safe inventory monitor (`Inventory`, `InventoryService`, `OutOfStockException`).
4. `com.flashdeal.discount` — Polymorphic discount strategy implementations (`DiscountStrategy`, `DiscountStrategyBase`, `PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount`, `DiscountService`).
5. `com.flashdeal.concurrency` — Multi-threaded simulation components (`CustomerPurchaseTask`, `FlashSaleCoordinator`, `FlashSaleListener`).
6. `com.flashdeal.service` — Core business services (`ProductService`, `OrderService`, `SalesReportService`, `FlashSaleService`).
7. `com.flashdeal.persistence` — CSV repositories (`FileStorageService`, `ProductRepository`, `OrderRepository`, `CustomerRepository`).
8. `com.flashdeal.ui` — Swing presentation views, components, and design system.
9. `com.flashdeal.util` — Cross-cutting utilities (`CurrencyUtil`, `ValidationUtil`, `DateUtil`, `UmlDiagramRenderer`).

---

## 10. Java Concepts Used

| Java Concept | Implementation Details |
| :--- | :--- |
| **Encapsulation** | Private attributes, defensive copying, and input validation in `Product` and `Order`. |
| **Inheritance** | `DiscountStrategyBase` extended by concrete discount subclasses; `OutOfStockException` extending `Exception`. |
| **Polymorphism** | `DiscountStrategy` references dynamically dispatching to percentage, flat, or bank discount algorithms. |
| **Interfaces** | `DiscountStrategy`, `FlashSaleListener`, `Runnable`, `StockChangeListener`. |
| **Multithreading** | Dedicated worker threads managed via `ExecutorService` and `Thread`. |
| **Synchronization** | `synchronized (inventoryLock)` ensuring atomic check-and-decrement operations in `Inventory.java`. |
| **Concurrency Barriers** | `CountDownLatch` ensuring all customer purchase tasks fire simultaneously. |
| **Collections Framework** | `LinkedHashMap` for fast key-based lookups, `ArrayList` for ordered lists. |
| **Iterator Traversal** | Explicit `Iterator<Order>` traversal in `OrderRepository` to calculate revenue and filter records. |
| **Exception Handling** | Custom checked `OutOfStockException` and comprehensive `try-catch-finally` handling. |
| **File I/O Streams** | `BufferedReader`, `BufferedWriter`, `Files` with UTF-8 encoding and try-with-resources. |
| **Swing Event Dispatch** | Background simulation threads notifying GUI via `SwingUtilities.invokeLater`. |

---

## 11. Implementation Details

### 11.1 Atomic Inventory Deduction (`Inventory.java`)
```java
public int deductStock(String productId, int requestedQuantity) throws OutOfStockException {
    if (requestedQuantity <= 0) {
        throw new IllegalArgumentException("Requested quantity must be positive.");
    }
    synchronized (inventoryLock) {
        int currentStock = stockMap.getOrDefault(productId, 0);
        if (currentStock < requestedQuantity) {
            throw new OutOfStockException(productId, requestedQuantity, currentStock);
        }
        int remainingStock = currentStock - requestedQuantity;
        stockMap.put(productId, remainingStock);
        return remainingStock;
    }
}
```
*Design Note:* The monitor lock on `inventoryLock` guarantees that no other thread can read or modify `stockMap` between the availability verification and the stock decrement.

### 11.2 Concurrency Barrier Orchestration (`FlashSaleCoordinator.java`)
```java
CountDownLatch startLatch = new CountDownLatch(1);
CountDownLatch completionLatch = new CountDownLatch(totalCustomers);

for (Customer customer : customers) {
    threadPool.submit(new CustomerPurchaseTask(
        customer, product, unitsPerCust, couponCode, flashSaleService, startLatch, completionLatch
    ));
}

// Release barrier: all threads launch simultaneously
startLatch.countDown();
completionLatch.await(30, TimeUnit.SECONDS);
```

---

## 12. Results and Verification

### 12.1 Primary Demonstration Scenario
- **Product:** OnePlus Nord Buds 2 TWS Earbuds (`PROD-101`)
- **Initial Stock Configured:** 10 units
- **Simulated Competing Customers:** 50 customers
- **Requested Units per Customer:** 1 unit
- **Observed Results:**
  - **Successful Orders:** Exactly 10 orders fulfilled.
  - **Out-of-Stock Rejections:** Exactly 40 purchase attempts rejected.
  - **Final Inventory:** Exactly 0 units remaining.
  - **Negative Inventory:** 0 (Never occurred).
  - **Double Selling:** 0 (Never occurred).
  - **Revenue:** Exactly 10 × Flash Price (minus applied coupon discounts).
  - **GUI Performance:** Fully fluid throughout execution; zero frame drops.

### 12.2 Screenshots
Screenshots captured from the running application are cataloged in `screenshots/`:
1. `screenshots/01_dashboard.png` — Real-Time Executive Dashboard.
2. `screenshots/02_catalog.png` — Product Catalog and Stock Adjustment.
3. `screenshots/03_simulator_running.png` — Flash Sale Rush Activity Log.
4. `screenshots/04_order_history.png` — Transaction Audit Ledger.
5. `screenshots/05_sales_report.png` — Performance Breakdown & CSV Export.

---

## 13. Team Contribution

| Module # | Module Title | Assigned Member | Roll No. | Key Java Classes |
| :---: | :--- | :--- | :---: | :--- |
| **Module 1** | Product & Inventory Modeling | Shanmukhi *(Lead)* | **35** | `Product`, `ProductRepository`, `ProductService`, `ProductPanel` |
| **Module 2** | Customer & Concurrent Simulation | [Team Member 2 Name] | [Roll No. --] | `Customer`, `CustomerPurchaseTask`, `FlashSaleCoordinator` |
| **Module 3** | Synchronized Inventory | [Team Member 3 Name] | [Roll No. --] | `Inventory`, `InventoryService`, `OutOfStockException` |
| **Module 4** | Discount Strategy & Coupons | [Team Member 4 Name] | [Roll No. --] | `DiscountStrategy`, `PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount` |
| **Module 5** | Sales Analytics & CSV Reports | [Team Member 5 Name] | [Roll No. --] | `Order`, `OrderRepository`, `SalesReportService`, `ReportsPanel` |

---

## 14. Testing and Verification

The automated test runner (`com.flashdeal.test.FlashSaleTestSuite`) was executed via `.\test.bat`, verifying 15 rigorous test scenarios:

```
=======================================================================
   FLASHDEAL PRO - AUTOMATED CONCURRENCY & UNIT TEST SUITE
   University: MNR University | Project #7: E-Commerce Flash Sale
=======================================================================

[RUNNING] Test 1. Successful purchase                                       [✔ PASSED]
[RUNNING] Test 2. Purchase with insufficient stock                          [✔ PASSED]
[RUNNING] Test 3. Inventory never becoming negative                         [✔ PASSED]
[RUNNING] Test 4. Concurrent purchase attempts (Race condition stress test) [✔ PASSED]
[RUNNING] Test 5. Invalid quantity handling                                 [✔ PASSED]
[RUNNING] Test 6. Percentage discount calculation & capping                 [✔ PASSED]
[RUNNING] Test 7. Flat discount calculation & threshold                     [✔ PASSED]
[RUNNING] Test 8. Bank offer discount calculation & limits                  [✔ PASSED]
[RUNNING] Test 9. Invalid coupon handling                                   [✔ PASSED]
[RUNNING] Test 10. Successful order recording & status                      [✔ PASSED]
[RUNNING] Test 11. Failed order recording & status                          [✔ PASSED]
[RUNNING] Test 12. Revenue calculation consistency                          [✔ PASSED]
[RUNNING] Test 13. CSV file persistence integrity                           [✔ PASSED]
[RUNNING] Test 14. Reloading inventory after restart                        [✔ PASSED]
[RUNNING] Test 15. CSV report export generation                             [✔ PASSED]

=======================================================================
   TEST EXECUTION SUMMARY: 15 Total | 15 Passed | 0 Failed
=======================================================================
[SUCCESS] ALL 15 AUTOMATED TEST SUITE SCENARIOS PASSED PERFECTLY!
```

---

## 15. Conclusion
The **FLASHDEAL PRO** mini-project successfully fulfills all curriculum and technical objectives stipulated by MNR University. By combining fundamental Object-Oriented Programming structures with core Java multithreading and monitor synchronization, the simulator demonstrates an impervious defense against race conditions, overselling, and negative inventory during extreme flash sale concurrency. The application delivers a high standard of code quality, comprehensive automated test coverage, and an elegant desktop user interface.

---

## 16. Future Scope
1. **Database Integration:** Migration from CSV files to embedded SQLite / H2 or PostgreSQL via JDBC.
2. **Fair Customer Scheduling:** Implementation of fair queuing algorithms to prioritize loyal or queue-waiting shoppers.
3. **Distributed Architecture:** Extending the simulation across distributed network nodes using Java RMI or REST sockets.
4. **Dynamic Data Visualizations:** Embedding dynamic charts for sales velocity and real-time revenue curves.

---

## 17. References
1. Gosling, J., Joy, B., Steele, G., Bracha, G., & Buckley, A. *The Java Language Specification, Java SE 17 Edition*. Oracle Corporation.
2. Bloch, J. (2018). *Effective Java (3rd ed.)*. Addison-Wesley Professional.
3. Goetz, B., Peierls, T., Bloch, J., Bowbeer, J., Holmes, D., & Lea, D. (2006). *Java Concurrency in Practice*. Addison-Wesley.
4. Eckel, B. (2006). *Thinking in Java (4th ed.)*. Prentice Hall.
5. Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
6. Oracle Java Documentation: *Lesson: Concurrency in Swing* (https://docs.oracle.com/javase/tutorial/uiswing/concurrency/).
