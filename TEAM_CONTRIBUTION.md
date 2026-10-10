# Team Contribution & Module Allocation
## MNR University — Department of CSE / AIML
### Course: Object-Oriented Programming (Java) — Project #7
**Project Title:** E-Commerce Flash Sale Simulator (FLASHDEAL PRO)  
**Theme:** Big Sale Day  
**Submission Deadline:** 30 October 2026  
**Repository Name:** `OOP_MiniProject_ECommerceFlashSale_Team7`  

---

## 1. Team Allocation Matrix

| Module # | Module Title | Assigned Member | Roll Number | Primary Java Classes / Responsibilities |
| :---: | :--- | :--- | :---: | :--- |
| **Module 1** | **Product & Limited Inventory Modeling** | Shanmukhi *(Lead)* | **35** | `com.flashdeal.model.Product`<br>`com.flashdeal.persistence.ProductRepository`<br>`com.flashdeal.service.ProductService`<br>`com.flashdeal.ui.ProductPanel` |
| **Module 2** | **Customer & Multi-Threaded Purchase Simulation** | [Team Member 2 Name] | [Roll No. --] | `com.flashdeal.model.Customer`<br>`com.flashdeal.concurrency.CustomerPurchaseTask`<br>`com.flashdeal.concurrency.FlashSaleCoordinator`<br>`com.flashdeal.persistence.CustomerRepository` |
| **Module 3** | **Thread-Safe Synchronized Inventory Operations** | [Team Member 3 Name] | [Roll No. --] | `com.flashdeal.inventory.Inventory`<br>`com.flashdeal.inventory.InventoryService`<br>`com.flashdeal.inventory.OutOfStockException`<br>`com.flashdeal.service.FlashSaleService` |
| **Module 4** | **Polymorphic Discount Strategies & Coupons** | [Team Member 4 Name] | [Roll No. --] | `com.flashdeal.discount.DiscountStrategy`<br>`com.flashdeal.discount.DiscountStrategyBase`<br>`com.flashdeal.discount.PercentageDiscount`<br>`com.flashdeal.discount.FlatDiscount`<br>`com.flashdeal.discount.BankOfferDiscount`<br>`com.flashdeal.discount.DiscountService` |
| **Module 5** | **Sales Analytics, Order History & CSV Reporting** | [Team Member 5 Name] | [Roll No. --] | `com.flashdeal.model.Order`<br>`com.flashdeal.model.OrderStatus`<br>`com.flashdeal.model.SaleMetric`<br>`com.flashdeal.persistence.OrderRepository`<br>`com.flashdeal.service.SalesReportService`<br>`com.flashdeal.ui.ReportsPanel` |

> *Note:* In accordance with university project guidelines, only Roll Number 35 is specified; placeholders (`[Team Member Name]`, `[Roll No. --]`) are provided for team members to enter their official university credentials prior to evaluation.

---

## 2. Module Explanations & Technical Breakdown

### Module 1: Product & Limited Inventory Modeling (Member 1 - Roll 35)
- **Objective:** Design and encapsulate product catalog entities, enforce invariant pricing rules (original price, flash sale price in Indian Rupees ₹), validate inventory thresholds, and persist catalog state.
- **Key Java Concepts:**
  - Encapsulation (`private` fields, getters, setters with defensive checks).
  - Validation (preventing negative prices and negative stock).
  - Data formatting (Indian Rupee `CurrencyUtil` using `BigDecimal`).
  - File Persistence (`data/products.csv`).
- **Important Classes:**
  - `Product`: Encapsulates product attributes, dynamic discount percentage calculation, and stock status transitions (`IN_STOCK`, `LOW_STOCK`, `OUT_OF_STOCK`).
  - `ProductRepository`: Manages CSV reading and writing with UTF-8 encoding.
  - `ProductService`: Exposes catalog queries and search filters.

### Module 2: Customer & Multi-Threaded Purchase Simulation (Member 2)
- **Objective:** Model concurrent shoppers vying for limited stock during high-rush flash sales, executing purchase requests on concurrent threads using synchronization barriers.
- **Key Java Concepts:**
  - Multithreading (`Thread`, `Runnable` interface implementation).
  - Thread Synchronization Barriers (`CountDownLatch`).
  - Thread pool management (`ExecutorService`, `Executors.newFixedThreadPool`).
  - Non-blocking Swing GUI communication via `SwingUtilities.invokeLater`.
- **Important Classes:**
  - `Customer`: Implements `Runnable` to represent an active shopper.
  - `CustomerPurchaseTask`: Task executed by customer threads that waits at a start barrier (`startLatch.await()`) before racing to purchase.
  - `FlashSaleCoordinator`: Coordinates thundering-herd simulation of up to 500 simultaneous shoppers without freezing the GUI.
  - `CustomerRepository`: Seeded with 50 realistic Indian customer identities.

### Module 3: Thread-Safe Synchronized Inventory Operations (Member 3)
- **Objective:** Eliminate race conditions, overselling, and negative inventory by guaranteeing atomic check-and-decrement inventory transactions.
- **Key Java Concepts:**
  - Thread synchronization (`synchronized` methods and synchronized monitor locks).
  - Custom exceptions (`OutOfStockException`).
  - Atomicity: Checking stock availability and decrementing count within a single critical section.
  - Exception handling (`try-catch-finally`).
- **Important Classes:**
  - `Inventory`: Implements thread-safe internal monitor locking ensuring no two threads can deduct the same inventory unit.
  - `OutOfStockException`: Custom checked exception conveying product ID, requested count, and remaining units.
  - `InventoryService`: Dispatches inventory changes and broadcasts live updates to listeners.
  - `FlashSaleService`: Transaction orchestrator handling atomic stock deduction and exception recovery.

### Module 4: Polymorphic Discount Strategies & Coupons (Member 4)
- **Objective:** Implement an extensible discount architecture using the Strategy Pattern to dynamically calculate price deductions based on coupon rules, threshold minimums, and discount caps.
- **Key Java Concepts:**
  - Interfaces (`DiscountStrategy`).
  - Abstract classes (`DiscountStrategyBase`) implementing the Template Method pattern.
  - Polymorphism & Method Overriding (`PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount`).
  - High-precision monetary arithmetic (`BigDecimal`).
- **Important Classes:**
  - `DiscountStrategy`: Defines standard contract for all promotional algorithms.
  - `DiscountStrategyBase`: Abstract base enforcing minimum order limits and maximum discount caps.
  - `PercentageDiscount`: E.g., `SAVE10` (10% instant off, min ₹500, max ₹300).
  - `FlatDiscount`: E.g., `FLAT100` (₹100 flat off on orders >= ₹999).
  - `BankOfferDiscount`: E.g., `BANK15` (15% card discount on orders >= ₹1500, max ₹750).
  - `DiscountService`: O(1) coupon registry using `HashMap`.

### Module 5: Sales Analytics, Order History & CSV Reporting (Member 5)
- **Objective:** Audit every purchase attempt (both successful orders and out-of-stock rejections), aggregate total revenue and units sold, and export comprehensive CSV analytical reports.
- **Key Java Concepts:**
  - Enumerations (`OrderStatus`: `SUCCESS`, `FAILED`, `OUT_OF_STOCK`).
  - Collections Framework (`ArrayList`, `HashMap`, `LinkedHashMap`).
  - Explicit `Iterator` traversal for collection auditing and filtering.
  - File I/O (`BufferedReader`, `BufferedWriter`, `FileWriter`) with try-with-resources.
- **Important Classes:**
  - `Order`: Represents transactional audit records with final amounts and timestamps.
  - `SaleMetric`: Tracks product-level aggregated metrics and success percentages.
  - `OrderRepository`: Persists order ledger to `data/orders.csv` and uses `Iterator<Order>` to calculate revenue.
  - `SalesReportService`: Computes executive KPIs and formats CSV export reports.
  - `ReportsPanel`: GUI dashboard displaying analytics tables and trigger for CSV download.

---

## 3. Integration & Peer Review Plan

1. **Independent Verification:** Each member tests their respective module classes using the provided test runner (`com.flashdeal.test.FlashSaleTestSuite`).
2. **Integration Testing:** Modules converge in `FlashSaleService` where `Inventory` (Member 3), `Customer` threads (Member 2), `Product` (Member 1), and `Discount` (Member 4) unite to produce `Order` records (Member 5).
3. **Defense Preparation:** All 5 team members must review the UML class diagram in `docs/uml/class-diagram.png` and understand the critical section synchronization logic in `Inventory.java` for university viva questions.
