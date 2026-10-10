# FLASHDEAL PRO — E-Commerce Flash Sale Simulator
### Big Sale Day Concurrency & Inventory Synchronization Engine

[![Java](https://img.shields.io/badge/Java-JDK_17%2B-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Swing](https://img.shields.io/badge/GUI-Java_Swing-007396?logo=java&logoColor=white)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Concurrency](https://img.shields.io/badge/Concurrency-CountDownLatch_%26_Synchronized-555555)](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CountDownLatch.html)
[![License](https://img.shields.io/badge/License-Academic_Use_Only-green)](LICENSE)

---

## 1. Project Title
**FLASHDEAL PRO: E-Commerce Flash Sale Simulator (Big Sale Day)**  
*Repository Name:* `OOP_MiniProject_ECommerceFlashSale_Team7`

---

## 2. University and Course Details
- **University:** MNR University
- **Department:** Computer Science & Engineering (CSE) / Artificial Intelligence & Machine Learning (AIML)
- **Course:** Object-Oriented Programming (Java) — Academic Mini-Project
- **Team Size:** 5 Students (Team 7)
- **Submission Deadline:** 30 October 2026

---

## 3. Project Number
**Assigned Project Number:** **7**

---

## 4. Student Roll Number
- **Lead Student Roll Number:** **35** (Shanmukhi)
- **Team:** Team 7 (5 Members)

---

## 5. Project Aim
The primary aim of **FLASHDEAL PRO** is to build a robust, desktop-based e-commerce flash sale simulator in Java Swing. It demonstrates how core Object-Oriented Programming (OOP) paradigms, combined with multi-threading, mutual exclusion, and thread synchronization, prevent critical concurrency flaws such as race conditions, negative inventory, and double-selling when dozens or hundreds of customers attempt to buy limited stock simultaneously.

---

## 6. Real-World Problem
During massive e-commerce flash sale campaigns (such as Flipkart Big Billion Days, Amazon Great Indian Festival, or Black Friday), thousands of shoppers click "Buy Now" at the exact same second.

Without atomic verification and thread-safe synchronization:
- **Inventory Over-selling:** Multiple threads read `stock = 1` concurrently, approve the transactions, and decrement the stock multiple times, leaving inventory negative (`stock = -49`).
- **Data Inconsistency:** The same physical unit is sold to multiple distinct customers.
- **Financial & Accounting Errors:** Total reported sales and order values exceed available physical goods.
- **Application Crashing:** Unhandled out-of-stock scenarios cause server/client threads to crash or deadlock.

---

## 7. Proposed Solution
FLASHDEAL PRO resolves these issues through a layered, object-oriented desktop architecture:
1. **Thread-Safe Inventory Monitor:** Encapsulates the inventory check-and-deduct routine inside an atomic `synchronized` critical section.
2. **Deterministic Concurrency Barrier:** Utilizes `java.util.concurrent.CountDownLatch` to simulate genuine thundering-herd flash sale rushes, launching up to 500 simultaneous threads at the exact same millisecond.
3. **Graceful Exception Recovery:** Implements a custom `OutOfStockException` that safely catches failed stock requests without crashing threads or freezing the UI.
4. **Polymorphic Discount Strategies:** Employs the Strategy Pattern with coupon rules (`SAVE10`, `FLAT100`, `BANK15`) to validate prices and prevent negative payable amounts.
5. **Non-Blocking Swing Dashboard:** Decouples background concurrency worker threads from the Swing Event Dispatch Thread (EDT) using `SwingUtilities.invokeLater`, maintaining responsive UI updates, live metrics, and real-time activity logging.
6. **Data Persistence:** Automatically preserves products, customers, and order history across runs using standard CSV file storage.

---

## 8. Main Features

- 📊 **Executive Dashboard:** Displays live KPI cards (Total Products, Live Inventory Units, Successful Orders, Blocked Attempts, Total Revenue in ₹, and Overall Success Rate).
- 📦 **Product Catalog Management:** Browse items in Indian Rupees (₹), search dynamically by keyword, filter by category, and adjust demonstration stock levels.
- ⚡ **Flash Sale Simulator:** Configure competing customers (1 to 500), units requested per customer, coupon strategies, and launch synchronized simultaneous purchase races.
- 📝 **Real-Time Activity Console:** High-contrast console displaying color-coded log entries of thread requests, order confirmations, and out-of-stock rejections with millisecond timestamps.
- 📜 **Audited Order Ledger:** Filter transactions by product ID, status (`SUCCESS`, `OUT_OF_STOCK`, `FAILED`), or search by customer name. Uses explicit Java `Iterator` collection traversal.
- 📈 **Sales Analytics & Reports:** Detailed product-wise performance breakdown with total attempts, units sold, remaining stock, revenue generated, and discount absorption.
- 💾 **CSV Report Export:** Export complete analytical audits to external CSV files using standard Java file streams.

---

## 9. Technologies Used
- **Language:** Java JDK (JDK 17+, tested on JDK 26)
- **GUI Framework:** Java Swing & AWT (`JFrame`, `CardLayout`, `JTable`, `JProgressBar`, `JScrollPane`, `JTextArea`, `JSpinner`)
- **Concurrency Tools:** `java.lang.Thread`, `java.lang.Runnable`, `java.util.concurrent.CountDownLatch`, `java.util.concurrent.ExecutorService`, `synchronized` blocks
- **Collections Framework:** `ArrayList`, `HashMap`, `LinkedHashMap`, `Iterator`
- **Monetary Arithmetic:** `java.math.BigDecimal` with `RoundingMode.HALF_UP`
- **File I/O:** `java.io.BufferedReader`, `java.io.BufferedWriter`, `java.nio.file.Files` (UTF-8, RFC 4180 CSV compliant)
- **Time API:** Modern `java.time.LocalDateTime` and `DateTimeFormatter`

---

## 10. Object-Oriented Programming (OOP) Concepts Demonstrated

| OOP Principle | Implementation in FLASHDEAL PRO |
| :--- | :--- |
| **Encapsulation** | Private fields, validation getters/setters in `Product`, `Order`, `Customer`, and `SaleMetric`. Invariants like non-negative prices and stock limits are strictly enforced. |
| **Inheritance** | `DiscountStrategyBase` serves as an abstract superclass extended by `PercentageDiscount`, `FlatDiscount`, and `BankOfferDiscount`. Custom exception `OutOfStockException` extends `java.lang.Exception`. |
| **Polymorphism** | `DiscountStrategy` interface allows runtime polymorphic swapping of coupon algorithms. Common calling code invokes `.calculateDiscount()` uniformly regardless of strategy type. |
| **Interfaces** | `DiscountStrategy` (promotional rules), `FlashSaleListener` (observer pattern callbacks), `Runnable` (multi-threaded customer simulation), and `StockChangeListener`. |
| **Abstraction** | Template Method Pattern in `DiscountStrategyBase` handles eligibility and capping while delegating `computeRawDiscount()` to concrete subclasses. |
| **Exception Handling** | Custom checked `OutOfStockException` with `try-catch-finally` and `try-with-resources` ensuring file stream cleanups and safe transaction rollbacks. |
| **Collections & Iterators** | `LinkedHashMap` for O(1) product lookups, `ArrayList` for order records, and explicit `Iterator<Order>` traversal for calculating revenue and filtering orders. |
| **Packages** | Clean separation across 8 custom packages (`app`, `model`, `inventory`, `discount`, `concurrency`, `service`, `persistence`, `ui`, `util`). |

---

## 11. Package Structure

```
OOP_MiniProject_ECommerceFlashSale_Team7/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── flashdeal/
│                   ├── app/
│                   │   └── Main.java                      # Application bootstrap & Look-and-Feel
│                   ├── model/
│                   │   ├── Product.java                   # Product entity & discount badge logic
│                   │   ├── Customer.java                  # Customer entity implementing Runnable
│                   │   ├── Order.java                     # Transaction ledger record
│                   │   ├── OrderStatus.java               # Enum: SUCCESS, FAILED, OUT_OF_STOCK
│                   │   └── SaleMetric.java                # Aggregated product sales performance
│                   ├── inventory/
│                   │   ├── Inventory.java                 # Thread-safe monitor with atomic deduction
│                   │   ├── InventoryService.java          # Inventory management & stock listeners
│                   │   └── OutOfStockException.java       # Custom exception for depleted stock
│                   ├── discount/
│                   │   ├── DiscountStrategy.java          # Discount interface
│                   │   ├── DiscountStrategyBase.java      # Abstract base with template validation
│                   │   ├── PercentageDiscount.java        # Strategy: % off with max cap (SAVE10)
│                   │   ├── FlatDiscount.java              # Strategy: Flat rupee discount (FLAT100)
│                   │   ├── BankOfferDiscount.java          # Strategy: Instant bank card off (BANK15)
│                   │   └── DiscountService.java           # Coupon registry & calculation engine
│                   ├── concurrency/
│                   │   ├── CustomerPurchaseTask.java      # Runnable task awaiting start barrier
│                   │   ├── FlashSaleCoordinator.java      # Multi-thread pool & CountDownLatch manager
│                   │   └── FlashSaleListener.java         # Event observer interface
│                   ├── persistence/
│                   │   ├── FileStorageService.java        # CSV reader/writer with UTF-8 locks
│                   │   ├── ProductRepository.java         # products.csv storage & sample seeder
│                   │   ├── OrderRepository.java           # orders.csv storage & Iterator audits
│                   │   └── CustomerRepository.java        # customers.csv storage & 50 Indian names
│                   ├── service/
│                   │   ├── ProductService.java            # Catalog operations & stock sync
│                   │   ├── OrderService.java              # Order retrieval & filtering
│                   │   ├── SalesReportService.java        # KPI aggregation & CSV report generator
│                   │   └── FlashSaleService.java          # Core atomic transaction controller
│                   ├── ui/
│                   │   ├── MainFrame.java                 # Desktop window with sidebar navigation
│                   │   ├── DashboardPanel.java            # Executive KPIs & recent orders table
│                   │   ├── ProductPanel.java              # Catalog browsing & stock adjustment
│                   │   ├── FlashSalePanel.java            # Interactive concurrency simulator
│                   │   ├── OrderHistoryPanel.java         # Filterable transaction audit ledger
│                   │   ├── ReportsPanel.java              # Product breakdown & CSV export screen
│                   │   ├── theme/
│                   │   │   └── UITheme.java               # SaaS color palette, fonts, buttons
│                   │   └── components/
│                   │       ├── MetricCard.java            # KPI card UI tile
│                   │       ├── BadgeRenderer.java         # Table cell status badge renderer
│                   │       └── CustomTable.java           # Modern styled JTable
│                   ├── util/
│                   │   ├── CurrencyUtil.java              # Indian Rupee (₹) formatting & parsing
│                   │   ├── ValidationUtil.java            # Input validation routines
│                   │   ├── DateUtil.java                  # java.time date formatting
│                   │   └── UmlDiagramRenderer.java        # Java 2D UML PNG image generator
│                   └── test/
│                       └── FlashSaleTestSuite.java        # 15 automated concurrency & unit tests
├── data/
│   ├── products.csv                                       # Persisted product catalog
│   ├── orders.csv                                         # Persisted transaction ledger
│   └── customers.csv                                      # Persisted customer profiles
├── docs/
│   └── uml/
│       ├── class-diagram.puml                             # PlantUML diagram source
│       └── class-diagram.png                              # Rendered high-resolution UML diagram
├── screenshots/                                           # Application screenshots
├── report/
│   └── PROJECT_REPORT.md                                  # Academic university project report
├── TEAM_CONTRIBUTION.md                                   # 5-Member contribution allocation table
├── compile.bat                                            # Batch compilation script
├── run.bat                                                # Application execution script
├── test.bat                                               # Automated test execution script
├── .gitignore
└── README.md
```

---

## 12. Detailed Class Explanations

- **`Inventory` (`com.flashdeal.inventory`):**  
  The linchpin of concurrency safety. Contains an internal `inventoryLock` monitor. Method `deductStock(productId, qty)` performs stock verification and deduction within an uninterrupted atomic block, throwing `OutOfStockException` if requested stock exceeds remaining units.
- **`FlashSaleCoordinator` (`com.flashdeal.concurrency`):**  
  Manages the concurrent simulation. Queues `N` customer tasks in an `ExecutorService`, initialises a `CountDownLatch(1)` start barrier, and triggers `.countDown()`, ensuring all customer threads start racing simultaneously.
- **`FlashSaleService` (`com.flashdeal.service`):**  
  Central orchestrator connecting `InventoryService`, `DiscountService`, and `OrderService`. Processes each purchase, verifies quantities, executes atomic stock deduction, calculates discounts, and records the resulting `Order`.
- **`DiscountStrategyBase` (`com.flashdeal.discount`):**  
  Abstract class defining template method `calculateDiscount(total, qty)`. Validates minimum order amounts, invokes subclass `computeRawDiscount()`, caps discounts to maximum limits, and guarantees that discounts cannot exceed total payable amounts.
- **`OrderRepository` (`com.flashdeal.persistence`):**  
  Persists transactions to `orders.csv`. Uses explicit `Iterator<Order>` traversal in `filterOrders()` and `calculateSuccessfulRevenue()` to fulfill collection traversal requirements.

---

## 13. UML Class Diagram

The project includes both the PlantUML specification and a rendered high-resolution diagram:
- **PlantUML Source:** [docs/uml/class-diagram.puml](file:///c:/Users/SHANMUKHI/Downloads/java%20mini%20project/docs/uml/class-diagram.puml)
- **Rendered Image:** [docs/uml/class-diagram.png](file:///c:/Users/SHANMUKHI/Downloads/java%20mini%20project/docs/uml/class-diagram.png)

*(You can regenerate the PNG at any time by running `java -cp bin com.flashdeal.util.UmlDiagramRenderer`)*

---

## 14. Instructions for Installing Prerequisites

1. **Java Development Kit (JDK):**
   - Install **JDK 17 or later** (JDK 21 LTS or JDK 26 recommended).
   - Verify installation in terminal/PowerShell:
     ```powershell
     java -version
     javac -version
     ```
2. **Operating System:**
   - Windows 10/11, macOS, or Linux (cross-platform pure Java).

---

## 15. Instructions for Compiling and Running

### Option A: Using Provided Batch Scripts (Windows)

1. **Compile all Java source files:**
   ```powershell
   .\compile.bat
   ```
2. **Run the FLASHDEAL PRO Desktop Application:**
   ```powershell
   .\run.bat
   ```
3. **Execute the Automated Test Suite (15 Test Cases):**
   ```powershell
   .\test.bat
   ```

### Option B: Using Standard Java Commands (Any OS / Terminal)

1. **Create output directory:**
   ```bash
   mkdir -p bin data
   ```
2. **Compile sources:**
   ```bash
   javac -encoding UTF-8 -d bin $(find src/main/java -name "*.java")
   ```
   *(On Windows PowerShell:)*
   ```powershell
   $src = Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName; javac -encoding UTF-8 -d bin $src
   ```
3. **Run Application:**
   ```bash
   java -Dfile.encoding=UTF-8 -cp bin com.flashdeal.app.Main
   ```
4. **Run Test Suite:**
   ```bash
   java -Dfile.encoding=UTF-8 -cp bin com.flashdeal.test.FlashSaleTestSuite
   ```

---

## 16. Sample Flash Sale Demonstration Walkthrough

Follow these steps to demonstrate thread-safe concurrency to the examiner:

1. Launch the application (`.\run.bat`).
2. Navigate to **Flash Sale Simulator** from the sidebar.
3. Select **OnePlus Nord Buds 2 TWS Earbuds** (`PROD-101`) or **boAt Rockerz 450** (`PROD-102`).
4. Click **Reset Stock for Demo** and set stock to **10 units**.
5. Set **Simulated Customers** to **50**.
6. Set **Units per Customer** to **1**.
7. Select Coupon **SAVE10**.
8. Click **START FLASH SALE SIMULATION**.
9. **Observe Expected Results:**
   - All 50 threads rush simultaneously.
   - The Activity Console displays instant order timestamps.
   - **Exactly 10 purchases succeed** (Orders `ORD-xxx` generated).
   - **Exactly 40 purchases are rejected** with `OutOfStockException` (Overselling blocked).
   - **Remaining Inventory drops to strictly 0 units** (Never negative).
   - Total Revenue reflects the sum of the 10 successful orders only.
10. Navigate to **Order History** and verify 10 `SUCCESS` orders and 40 `OUT_OF_STOCK` records.
11. Navigate to **Sales Analytics** and click **Export Sales Report (CSV)**.

---

## 17. File Storage Details

All application data is persistently saved in the local `data/` directory:

1. **`data/products.csv`**
   - Format: `productId,name,description,category,originalPrice,flashSalePrice,initialStock,availableStock,status`
   - Automatically seeded with 8 realistic sample consumer electronics products in Indian Rupees (₹).
2. **`data/orders.csv`**
   - Format: `orderId,customerId,customerName,productId,productName,quantity,originalAmount,discountAmount,finalAmount,timestamp,status,failureReason`
   - Appended atomically whenever an order attempt is processed.
3. **`data/customers.csv`**
   - Format: `customerId,name,email,phone`
   - Pre-populated with 50 realistic Indian customer identities.
4. **`data/sales_history.csv` / Exported Reports**
   - Comprehensive audit reports containing high-level financial KPIs and product performance breakdowns.

---

## 18. Automated Test Scenarios

The automated test runner (`com.flashdeal.test.FlashSaleTestSuite`) executes 15 exhaustive automated scenarios:

| Test # | Test Scenario | Verification Objective | Observed Result |
| :---: | :--- | :--- | :---: |
| 1 | Successful Purchase | Deducts stock atomically from 10 to 8 | ✅ PASSED |
| 2 | Insufficient Stock | Throws `OutOfStockException` when requesting 5 with stock 2 | ✅ PASSED |
| 3 | Non-Negative Inventory | Inventory remains >= 0 under excessive deduction attempts | ✅ PASSED |
| 4 | **Concurrent Race Condition** | **50 concurrent customer threads competing for 10 units: exactly 10 succeed, 40 rejected, final stock = 0** | ✅ **PASSED** |
| 5 | Invalid Quantity | Rejects zero and negative purchase quantities with `IllegalArgumentException` | ✅ PASSED |
| 6 | Percentage Discount | Validates `SAVE10` 10% discount computation and maximum cap ceiling | ✅ PASSED |
| 7 | Flat Discount | Validates `FLAT100` fixed ₹100 deduction and minimum order threshold | ✅ PASSED |
| 8 | Bank Offer Discount | Validates `BANK15` 15% instant card discount and maximum savings cap | ✅ PASSED |
| 9 | Invalid Coupon | Unknown coupon code yields ₹0.00 discount without failure | ✅ PASSED |
| 10 | Successful Order Recording | Successfully persists valid order record to repository | ✅ PASSED |
| 11 | Failed Order Recording | Persists failed order with `OUT_OF_STOCK` status and ₹0.00 final amount | ✅ PASSED |
| 12 | Revenue Calculation | Revenue calculation sums only successful orders, ignoring failed ones | ✅ PASSED |
| 13 | CSV File Persistence | Validates RFC-4180 CSV parsing, quotes, and file stream writing | ✅ PASSED |
| 14 | Restart Inventory Reload | Persisted stock updates survive application restart | ✅ PASSED |
| 15 | CSV Report Export | Generates formatted analytics CSV audit report file | ✅ PASSED |

---

## 19. Screenshots Section

Place captured application screenshots into the `screenshots/` directory for project submission:
- `screenshots/01_dashboard.png` — Executive KPI Dashboard and recent transactions table.
- `screenshots/02_catalog.png` — Product catalog with price discount badges and stock adjustment.
- `screenshots/03_simulator_running.png` — Flash sale simulator showing concurrent activity log.
- `screenshots/04_order_history.png` — Filtered order history with color-coded status badges.
- `screenshots/05_sales_report.png` — Sales analytics breakdown and CSV export confirmation.

---

## 20. Team Contribution Summary

| Module # | Module Title | Assigned Member | Roll No. | Key Responsibilities |
| :---: | :--- | :--- | :---: | :--- |
| **Module 1** | Product & Inventory Modeling | Shanmukhi *(Lead)* | **35** | `Product`, `ProductRepository`, `ProductService`, `ProductPanel` |
| **Module 2** | Customer & Concurrent Simulation | [Team Member 2 Name] | [Roll No. --] | `Customer`, `CustomerPurchaseTask`, `FlashSaleCoordinator` |
| **Module 3** | Synchronized Inventory | [Team Member 3 Name] | [Roll No. --] | `Inventory`, `InventoryService`, `OutOfStockException` |
| **Module 4** | Discount Strategy & Coupons | [Team Member 4 Name] | [Roll No. --] | `DiscountStrategy`, `PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount` |
| **Module 5** | Sales Analytics & CSV Reports | [Team Member 5 Name] | [Roll No. --] | `Order`, `OrderRepository`, `SalesReportService`, `ReportsPanel` |

*(Detailed module breakdown available in [TEAM_CONTRIBUTION.md](file:///c:/Users/SHANMUKHI/Downloads/java%20mini%20project/TEAM_CONTRIBUTION.md))*

---

## 21. Known Limitations
- The simulation runs locally on the host JVM without distributed network sockets or external database clusters (designed intentionally per university standalone offline requirements).
- Maximum tested concurrency within default JVM thread stack memory is 500 simultaneous customer threads.

---

## 22. Future Enhancements
- Integration with JDBC / SQLite for relational transactional storage.
- Implementation of fair queuing using virtual customer priority queues (e.g., Prime / VIP shoppers).
- Real-time charting using JFreeChart or JavaFX for dynamic revenue curves.
- Networked socket server allowing multi-machine client simulation.
