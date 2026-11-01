# MNR UNIVERSITY
### School of Engineering & Technology (SOET)
### Department of Computer Science and Engineering

\pagebreak

---

# MINI PROJECT REPORT
### on
## FLASHDEAL PRO: E-COMMERCE FLASH SALE SIMULATOR
### (Java Programming Implementation-Based Mini-Project)

**Course:** Object Oriented Programming (Java)  
**Program:** B.Tech – Computer Science and Engineering (CSE / AIML)  
**Academic Year:** 2026 – 2027  
**Assigned Project Number:** **7**  

---

### **Submitted by**

| S.No | Student Name | Roll Number |
| :---: | :--- | :---: |
| 1 | **Shanmukhi** *(Team Lead)* | **35** |
| 2 | [Team Member 2 Name] | [Roll No. --] |
| 3 | [Team Member 3 Name] | [Roll No. --] |
| 4 | [Team Member 4 Name] | [Roll No. --] |
| 5 | [Team Member 5 Name] | [Roll No. --] |

---

### **Under the guidance of**
**[Faculty Name]**, Lecturer, Department of CSE  
School of Engineering & Technology, MNR University  

**Academic Year 2026 – 2027**

\pagebreak

---

## CERTIFICATE

This is to certify that the mini project report titled **“FLASHDEAL PRO: E-COMMERCE FLASH SALE SIMULATOR”** is a bonafide record of the work carried out by the students listed on the title page, in partial fulfilment of the requirements of the course **Object Oriented Programming (Java)**, **B.Tech – Computer Science and Engineering**, Department of Computer Science and Engineering, School of Engineering & Technology, **MNR University**, during the academic year 2026–2027.

<br><br><br>

**Faculty Guide** &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **Dean, School of Engineering & Technology**

<br>

**Date:** \_\_\_\_\_\_\_\_\_\_\_\_ &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; **Place:** \_\_\_\_\_\_\_\_\_\_\_\_

---

## DECLARATION

We hereby declare that this mini project report is the result of our own work. The program was written, executed and tested by us. Any help, book or website used has been mentioned in the references.

<br><br><br>

**Signature of the Students:**

1. \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ (Shanmukhi, Roll No: 35)
2. \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ ([Team Member 2 Name])
3. \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ ([Team Member 3 Name])
4. \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ ([Team Member 4 Name])
5. \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ ([Team Member 5 Name])

\pagebreak

---

## CONTENTS

| Chapter | Title | Page |
| :---: | :--- | :---: |
| **1** | **Aim and Abstract** | 1 |
| **2** | **Introduction and Problem Statement** | 1 |
| **3** | **Objectives** | 1 |
| **4** | **Requirements** | 2 |
| **5** | **Class Design** | 2 |
| **6** | **Java Concepts Used** | 3 |
| **7** | **Program Logic (Algorithm)** | 4 |
| **8** | **Implementation** | 4 |
| **9** | **Output Screenshots** | 5 |
| **10** | **Test Cases** | 5 |
| **11** | **Results and Discussion** | 6 |
| **12** | **Conclusion** | 6 |
| **13** | **References** | 6 |
| **A** | **Appendix A – Complete Source Code & Repository** | 7 |
| **B** | **Appendix B – Team Contribution** | 7 |

\pagebreak

---

## CHAPTER 1: AIM AND ABSTRACT

### 1.1 Aim
To design and develop **FLASHDEAL PRO: E-Commerce Flash Sale Simulator** in Java using Object Oriented Programming concepts, so that limited promotional inventory can be stored, searched, atomically updated, and processed concurrently under extreme shopping rush without race conditions, overselling, or negative stock.

### 1.2 Abstract
This project is an advanced e-commerce flash sale simulation application developed in Java Swing. It is used to emulate high-velocity "Big Sale Day" flash events where hundreds of customers compete simultaneously for limited stock items. The program uses classes and objects, encapsulation, inheritance, abstract classes, interfaces, custom checked exceptions (`OutOfStockException`), multithreading (`Thread`, `Runnable`), thread synchronization barriers (`CountDownLatch`), monitor locking (`synchronized`), and the Java Collections Framework (`LinkedHashMap`, `ArrayList`, `Iterator`). Transaction records and catalog data are persistently stored using standard RFC-4180 compliant CSV files with atomic stream locks.

---

## CHAPTER 2: INTRODUCTION AND PROBLEM STATEMENT

### 2.1 Introduction
In modern e-commerce ecosystems during mega-sales events (such as Flipkart Big Billion Days, Amazon Great Indian Festival, or limited-quantity product drops), thousands of shoppers click "Buy Now" at the exact same second. A robust software architecture is critical to guarantee data consistency and fairness under sudden concurrency spikes. Java is particularly well-suited for this domain because it is object-oriented, strictly typed, platform-independent, and provides built-in enterprise-grade concurrency utilities (`java.util.concurrent`), atomic synchronization primitives, and powerful data structures such as `LinkedHashMap` and `ArrayList`.

### 2.2 Problem Statement
In traditional or uncoordinated systems, when multiple threads access and update shared inventory variables at the same instant, critical section race conditions occur:
1. Multiple customer threads read `stock = 1` simultaneously and approve purchases, leaving the inventory in an invalid negative state (e.g., `stock = -49`).
2. The same physical unit is double-sold to multiple distinct customers.
3. Transaction ledgers desynchronize from inventory, causing accounting discrepancies and unhandled runtime exceptions that freeze the user interface.

### 2.3 Proposed Solution
The proposed Java application addresses these issues through a layered, object-oriented, thread-safe architecture:
* **Add and store records:** Maintain a structured catalog of consumer electronics products with dual pricing (original and flash sale in ₹) and stock counters.
* **Search and update records:** Search products dynamically by name, filter by category, and adjust inventory levels in real time.
* **Perform atomic purchase operations:** Encapsulate check-and-decrement inventory operations within a `synchronized` critical section, preventing overselling.
* **Simulate concurrent shoppers:** Launch up to 500 competing customer threads simultaneously using a `CountDownLatch(1)` release gate.
* **Display real-time visual reports:** Render responsive KPI metric cards, high-contrast live activity logging, and audited transaction ledgers in a modern Java Swing GUI.
* **Save data permanently:** Store and reload product catalogs, registered customers, and order ledgers using thread-safe CSV file I/O.

---

## CHAPTER 3: OBJECTIVES

The objectives of this project are:
* To apply classes, objects, constructors, and methods in structuring domain models (`Product`, `Customer`, `Order`, `SaleMetric`).
* To apply encapsulation using private data members with defensive validation getters and setters.
* To apply inheritance and method overriding using an abstract base class (`DiscountStrategyBase`) extended by concrete promotional strategies (`PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount`).
* To use abstract classes and interfaces (`DiscountStrategy`, `FlashSaleListener`, `Runnable`, `StockChangeListener`).
* To handle errors using robust exception handling, including a user-defined checked exception (`OutOfStockException`).
* To store and process data using the Java Collections Framework (`LinkedHashMap`, `ArrayList`, and explicit `Iterator<Order>` traversal).
* To execute multi-threaded concurrent simulations using `Thread`, `Runnable`, `ExecutorService`, and `CountDownLatch`.
* To save data permanently using standard File I/O (`BufferedReader`, `BufferedWriter`, CSV parsing).
* To organise the program into modular packages (`app`, `model`, `inventory`, `discount`, `concurrency`, `service`, `persistence`, `ui`, `util`, `test`) and verify all invariants with 15 automated test cases.

\pagebreak

---

## CHAPTER 4: REQUIREMENTS

### 4.1 Functional Requirements
The program should be able to:
* **Add and browse product records:** Display catalog items with original price, flash sale price (₹), stock status, and discount badges.
* **Search and filter products:** Search items by keyword and category (Smartphones, Audio, Wearables, Accessories).
* **Update inventory stock:** Reset or modify product inventory levels for demonstration purposes.
* **Execute concurrent flash sale simulations (Core Operation):** Spawn 1 to 500 customer threads racing at the same millisecond to buy limited stock.
* **Enforce atomic stock deduction:** Guarantee that stock never becomes negative and that oversold attempts are rejected safely.
* **Apply polymorphic discounts:** Evaluate coupons (`SAVE10`, `FLAT100`, `BANK15`) with minimum spend rules and maximum discount caps.
* **Display comprehensive reports:** Present executive KPI summary cards, filterable order transaction history, and product-wise revenue analytics.
* **Export sales reports:** Export detailed sales and performance audit data to external CSV files.
* **Show clear messages for every wrong input:** Validate quantities, product selections, coupon codes, and handle out-of-stock conditions with informative alerts.

### 4.2 Input and Output

| Input | Java Data Type | Validation | Output |
| :--- | :---: | :--- | :--- |
| **Product Selection** | `String` (ID) | Must match an existing product in repository | Selected product preview and stock updated |
| **Customer Count** | `int` | Range: `1` to `500` (positive integer) | Number of worker threads spawned |
| **Units per Customer** | `int` | Range: `1` to `10` (strictly > 0) | Quantity requested per purchase task |
| **Coupon Code** | `String` | Optional; validated against coupon registry | Applied discount amount in ₹ (or ₹0.00) |
| **Demo Stock Reset** | `int` | Non-negative integer (`>= 0`) | Available inventory updated in memory and CSV |
| **Order Search/Filter**| `String` / `Enum` | Valid text or status (`SUCCESS`, `OUT_OF_STOCK`) | Filtered table of audited customer orders |

### 4.3 Software and Hardware Requirements

| Item | Details |
| :--- | :--- |
| **Programming Language** | Java (JDK 17 LTS or later, tested up to JDK 26) |
| **Development Tools / IDE** | VS Code / IntelliJ IDEA / Eclipse, PowerShell, Git |
| **User Interface** | Java Swing & AWT (Desktop GUI) + Responsive HTML/CSS/JS (Web Showcase) |
| **Storage** | Plain-text CSV files (`products.csv`, `orders.csv`, `customers.csv`) with UTF-8 encoding |
| **Concurrency API** | `java.util.concurrent` (`CountDownLatch`, `ExecutorService`, `ThreadPoolExecutor`, `synchronized`) |
| **Operating System** | Windows 10 / 11, Linux, or macOS |
| **Hardware Requirements** | Dual-core CPU 2.0 GHz or higher, 4 GB RAM minimum (8 GB recommended), 200 MB disk space |

\pagebreak

---

## CHAPTER 5: CLASS DESIGN

### 5.1 List of Classes
List of every class, interface, and exception used in the project:

| Class / Interface | Type | Purpose |
| :--- | :---: | :--- |
| `Main` | Driver Class | Program bootstrap; initializes FlatLaf look-and-feel and launches `MainFrame` on the Swing Event Dispatch Thread (EDT). |
| `Product` | Domain Entity Class | Encapsulates product details, dual pricing in ₹, inventory status transitions, and discount calculations. |
| `Customer` | Entity Class (`Runnable`) | Represents an individual customer shopper capable of executing purchase tasks in a concurrent thread. |
| `Order` | Domain Entity Class | Encapsulates immutable transaction records with quantities, discounts, final amounts, and status. |
| `OrderStatus` | `enum` | Enumerates valid lifecycle states of an order attempt: `SUCCESS`, `OUT_OF_STOCK`, `FAILED`. |
| `SaleMetric` | Aggregation Class | Tracks product-level aggregated metrics: attempts, units sold, revenue, discounts, and success rate. |
| `Inventory` | Thread-Safe Monitor | Core synchronization monitor encapsulating `stockMap` and atomic `synchronized` deduction logic. |
| `OutOfStockException` | User-Defined Exception | Custom checked exception thrown when requested stock exceeds remaining units. |
| `InventoryService` | Service Class | Coordinates stock updates and dispatches notifications to registered `StockChangeListener`s. |
| `DiscountStrategy` | Interface | Defines the contract for all promotional coupon calculations. |
| `DiscountStrategyBase` | Abstract Class | Implements template method pattern enforcing eligibility thresholds and maximum discount ceilings. |
| `PercentageDiscount` | Class (extends Base) | Computes percentage-based savings with maximum monetary caps (e.g., `SAVE10`). |
| `FlatDiscount` | Class (extends Base) | Computes flat rupee deductions when minimum order amount is satisfied (e.g., `FLAT100`). |
| `BankOfferDiscount` | Class (extends Base) | Computes instant bank payment partner card discounts with spending limits (e.g., `BANK15`). |
| `DiscountService` | Service Class | Registry and dispatcher for coupon codes with O(1) hash map lookups. |
| `CustomerPurchaseTask` | Class (`Runnable`) | Concurrent worker task waiting on a `CountDownLatch` barrier before racing to process a purchase. |
| `FlashSaleCoordinator`| Concurrency Manager | Spawns thread pools, initializes start/completion latches, and orchestrates the synchronized flash sale rush. |
| `FlashSaleListener` | Interface | Observer callback interface notifying the GUI of sale lifecycle and purchase outcomes. |
| `FlashSaleService` | Core Service Class | Transaction orchestrator linking inventory verification, discount calculation, and order generation. |
| `ProductRepository` | Persistence Class | Reads and writes `data/products.csv` with automatic file seeding. |
| `OrderRepository` | Persistence Class | Appends and queries `data/orders.csv`; implements explicit `Iterator<Order>` traversal for audits. |
| `CustomerRepository` | Persistence Class | Reads `data/customers.csv` pre-populated with 50 realistic customer identities. |
| `FileStorageService` | Utility Class | Handles atomic file streams, RFC-4180 CSV escaping, and directory integrity. |
| `ProductService` | Service Class | Exposes catalog browsing, filtering, and stock synchronization routines. |
| `OrderService` | Service Class | Exposes order query, filtering, and customer transaction history. |
| `SalesReportService`| Service Class | Computes high-level KPI summaries and exports formatted CSV audit reports. |
| `MainFrame` | GUI Container Class | Top-level window hosting sidebar navigation and `CardLayout` view transitions. |
| `DashboardPanel` | GUI Panel Class | Executive overview displaying live metric cards and recent transactions. |
| `ProductPanel` | GUI Panel Class | Product catalog management grid with demo stock adjustment tools. |
| `FlashSalePanel` | GUI Panel Class | Interactive concurrency simulator with thread controls, live progress, and activity console. |
| `OrderHistoryPanel` | GUI Panel Class | Searchable and filterable transaction audit ledger. |
| `ReportsPanel` | GUI Panel Class | Tabular breakdown of product-wise performance and trigger for CSV exports. |
| `CurrencyUtil` | Utility Class | Handles Indian Rupee (₹) formatting and arithmetic with `BigDecimal`. |
| `ValidationUtil` | Utility Class | Validates IDs, quantities, monetary values, and text inputs defensively. |
| `DateUtil` | Utility Class | Formats timestamps using modern `java.time.LocalDateTime`. |
| `FlashSaleTestSuite` | Test Suite Class | Automated unit and concurrency test runner executing 15 comprehensive verification scenarios. |

\pagebreak

---

### 5.2 Data Members and Methods of Main Classes

#### Class: `Product`
* **Data members:** `private String productId; private String name; private String description; private String category; private BigDecimal originalPrice; private BigDecimal flashSalePrice; private int initialStock; private int availableStock; private String status;`
* **Constructors:** Parameterized constructor initializing all 9 fields with defensive validation.
* **Methods:** `getProductId()`, `getName()`, `getOriginalPrice()`, `getFlashSalePrice()`, `getAvailableStock()`, `setAvailableStock(int stock)`, `calculateDiscountPercentage()`, `hasDiscount()`, `toCsvRow()`, `fromCsvRow(String line)`

#### Class: `Inventory`
* **Data members:** `private final Map<String, Integer> stockMap; private final Object inventoryLock;`
* **Constructors:** `public Inventory(Map<String, Integer> initialStock)` (defensively copies stock into `LinkedHashMap`).
* **Methods:**
  * `public int deductStock(String productId, int requestedQuantity) throws OutOfStockException`
  * `public void restoreStock(String productId, int quantity)`
  * `public int getStock(String productId)`
  * `public void setStock(String productId, int newStock)`
  * `public Map<String, Integer> getAllStockSnapshot()`

#### Class: `DiscountStrategyBase`
* **Data members:** `private final String couponCode; private final String description; private final BigDecimal minimumOrderAmount; private final BigDecimal maxDiscountAmount;`
* **Constructors:** Parameterized constructor initializing coupon rules and constraints.
* **Methods:**
  * `public final BigDecimal calculateDiscount(BigDecimal totalAmount, int quantity)` *(Template Method)*
  * `protected abstract BigDecimal computeRawDiscount(BigDecimal totalAmount, int quantity)`
  * `public String getCouponCode()`, `public String getDescription()`

#### Class: `FlashSaleCoordinator`
* **Data members:** `private final FlashSaleService flashSaleService; private final List<FlashSaleListener> listeners; private ExecutorService threadPool; private volatile boolean isRunning;`
* **Constructors:** `public FlashSaleCoordinator(FlashSaleService flashSaleService)`
* **Methods:**
  * `public void startSimulation(String productId, int customerCount, int unitsPerCustomer, String couponCode, List<Customer> customers)`
  * `public void addListener(FlashSaleListener listener)`
  * `public boolean isRunning()`

#### Class: `Order`
* **Data members:** `private final String orderId; private final String customerId; private final String customerName; private final String productId; private final String productName; private final int quantity; private final BigDecimal originalAmount; private final BigDecimal discountAmount; private final BigDecimal finalAmount; private final LocalDateTime timestamp; private final OrderStatus status; private final String failureReason;`
* **Constructors:** Parameterized constructor enforcing non-null fields and non-negative amounts.
* **Methods:** Getters for all fields, `isSuccess()`, `toCsvRow()`, `fromCsvRow(String line)`

---

### 5.3 Relationships Between Classes

| Classes | Relationship | Explanation |
| :--- | :---: | :--- |
| `DiscountStrategyBase – PercentageDiscount` | Inheritance (IS-A) | `PercentageDiscount` extends `DiscountStrategyBase` and overrides `computeRawDiscount()`. |
| `DiscountStrategyBase – FlatDiscount` | Inheritance (IS-A) | `FlatDiscount` extends `DiscountStrategyBase` and overrides `computeRawDiscount()`. |
| `DiscountStrategyBase – BankOfferDiscount` | Inheritance (IS-A) | `BankOfferDiscount` extends `DiscountStrategyBase` and overrides `computeRawDiscount()`. |
| `DiscountStrategy – DiscountStrategyBase` | Implements | `DiscountStrategyBase` implements interface `DiscountStrategy`. |
| `Customer – Runnable` | Implements | `Customer` implements `java.lang.Runnable` to execute purchase tasks on separate threads. |
| `CustomerPurchaseTask – Runnable` | Implements | `CustomerPurchaseTask` implements `Runnable` to race at the `CountDownLatch` barrier. |
| `FlashSalePanel – FlashSaleListener` | Implements | `FlashSalePanel` implements `FlashSaleListener` to receive real-time UI callback notifications. |
| `InventoryService – Inventory` | Composition (HAS-A)| `InventoryService` owns and manages the thread-safe `Inventory` monitor instance. |
| `FlashSaleService – InventoryService` | Association (USES) | `FlashSaleService` invokes `InventoryService` for atomic stock validation and deduction. |
| `OrderRepository – Order` | Aggregation (HAS-A) | `OrderRepository` manages a collection of `Order` records and iterates over them using `Iterator<Order>`. |

\pagebreak

---

## CHAPTER 6: JAVA CONCEPTS USED

| Java Concept | Where Used (Class / Method) |
| :--- | :--- |
| **Classes, objects and constructors** | `Product`, `Customer`, `Order`, `SaleMetric` (all constructors instantiate domain state) |
| **Encapsulation (private, getters, setters)** | `Product.getAvailableStock()`, `Product.setAvailableStock()`, `Order.getFinalAmount()` |
| **Inheritance and super keyword** | `PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount` calling `super(couponCode, ...)` in `DiscountStrategyBase` |
| **Method overloading** | `ProductService.searchProducts(String query)` and `ProductService.searchProducts(String query, String category)` |
| **Method overriding** | `PercentageDiscount.computeRawDiscount()`, `Order.toString()`, `BadgeRenderer.getTableCellRendererComponent()` |
| **Abstract class / interface** | Abstract class `DiscountStrategyBase`; Interfaces: `DiscountStrategy`, `FlashSaleListener`, `StockChangeListener`, `Runnable` |
| **Packages** | 10 custom packages: `com.flashdeal.app`, `model`, `inventory`, `discount`, `concurrency`, `service`, `persistence`, `ui`, `util`, `test` |
| **static and final keywords** | `CurrencyUtil.formatINR()`, `ValidationUtil.validatePositive()`, `private final Object inventoryLock` |
| **Exception handling (try, catch, throw, custom exception)** | Custom checked `OutOfStockException`; `try-with-resources` in `FileStorageService.writeLines()`; `try-catch` in `CustomerPurchaseTask.run()` |
| **Collections (ArrayList, HashMap)** | `LinkedHashMap` for product indexing, `ArrayList<Order>` in `OrderRepository`, `HashMap<String, DiscountStrategy>` in `DiscountService` |
| **Collection Traversal (Iterator)** | Explicit `Iterator<Order>` used in `OrderRepository.calculateSuccessfulRevenue()` and `OrderRepository.filterOrders()` |
| **Multithreading (Thread / Runnable)** | `CustomerPurchaseTask implements Runnable`, `Thread` pool in `FlashSaleCoordinator`, `SwingUtilities.invokeLater` for GUI decoupling |
| **Thread Synchronization** | `synchronized (inventoryLock)` block in `Inventory.deductStock()` guaranteeing mutual exclusion |
| **Concurrency Coordination** | `CountDownLatch startLatch = new CountDownLatch(1)` and `completionLatch` in `FlashSaleCoordinator` |
| **File I/O or JDBC** | `BufferedReader`, `BufferedWriter`, `Files.newBufferedReader()`, UTF-8 CSV streams in `FileStorageService` |

\pagebreak

---

## CHAPTER 7: PROGRAM LOGIC (ALGORITHM)

### 7.1 Main Program Flow
1. **Start the program:** `Main.java` executes on the main thread and sets the modern UI Look-and-Feel.
2. **Initialize Services & Repositories:** Load saved CSV data into memory (`products.csv`, `customers.csv`, `orders.csv`).
3. **Launch Desktop UI:** Display `MainFrame` with sidebar navigation and show the Executive Dashboard.
4. **User Action Selection:** User selects one of the functional panels (Catalog, Simulator, Order History, Reports).
5. **Configure Flash Sale Simulation:** User selects a product, sets customer count (1–500), units per customer, and coupon code.
6. **Trigger Concurrency Barrier:** `FlashSaleCoordinator` spawns concurrent customer threads, releases `CountDownLatch`, and processes simultaneous purchase requests.
7. **Perform Atomic Operations:** `Inventory.deductStock()` verifies stock inside `synchronized (inventoryLock)`. Successful orders decrement stock; exhausted stock throws `OutOfStockException`.
8. **Update GUI & Save Data:** Broadcast live log updates to `FlashSalePanel` via `SwingUtilities.invokeLater` and atomically append transaction records to `orders.csv`.
9. **Display Reports / Exit:** User reviews audited orders, views aggregated analytics, exports CSV reports, or exits the program cleanly.

### 7.2 Algorithm for the Core Operation (Atomic Flash Sale Purchase)
```text
Algorithm: AtomicConcurrentPurchase(customerId, productId, requestedQty, couponCode)
Input: Customer ID, Product ID, Requested Quantity (int), Coupon Code (String)
Output: Order object (Status: SUCCESS, OUT_OF_STOCK, or FAILED)

Step 1: Validate input parameters (requestedQty > 0, non-empty productId, valid customerId).
Step 2: Enter atomic critical section using synchronized(inventoryLock):
          a. Read currentStock from stockMap for productId.
          b. If currentStock < requestedQty:
               Throw OutOfStockException(productId, requestedQty, currentStock).
          c. Deduct stock: remainingStock = currentStock - requestedQty.
          d. Update stockMap with remainingStock.
Step 3: If OutOfStockException is caught:
          a. Create Order with status = OUT_OF_STOCK, finalAmount = 0.00, failureReason = "Insufficient stock".
          b. Persist Order to orders.csv and return Order.
Step 4: Retrieve product unit flash sale price and compute originalTotal = unitPrice × requestedQty.
Step 5: Look up coupon in DiscountService and evaluate polymorphic discount:
          discountAmount = strategy.calculateDiscount(originalTotal, requestedQty).
Step 6: Compute finalPayable = originalTotal - discountAmount.
Step 7: Generate unique Order ID ("ORD-" + UUID/timestamp) with status = SUCCESS.
Step 8: Persist Order to orders.csv, notify UI listeners, and return Order.
```

\pagebreak

---

## CHAPTER 8: IMPLEMENTATION

### 8.1 Project Structure
```text
OOP_MiniProject_ECommerceFlashSale_Team7/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── flashdeal/
│                   ├── app/
│                   │   └── Main.java                      # Program entry point & Look-and-Feel
│                   ├── model/
│                   │   ├── Product.java                   # Product entity with dual pricing
│                   │   ├── Customer.java                  # Customer entity implementing Runnable
│                   │   ├── Order.java                     # Transaction ledger record
│                   │   ├── OrderStatus.java               # Enum: SUCCESS, FAILED, OUT_OF_STOCK
│                   │   └── SaleMetric.java                # Aggregated sales performance metric
│                   ├── inventory/
│                   │   ├── Inventory.java                 # Thread-safe monitor with atomic deduction
│                   │   ├── InventoryService.java          # Inventory management & stock listeners
│                   │   └── OutOfStockException.java       # Custom exception for depleted stock
│                   ├── discount/
│                   │   ├── DiscountStrategy.java          # Promotional strategy interface
│                   │   ├── DiscountStrategyBase.java      # Abstract base class (Template Method)
│                   │   ├── PercentageDiscount.java        # Strategy: % off with max cap (SAVE10)
│                   │   ├── FlatDiscount.java              # Strategy: Flat rupee discount (FLAT100)
│                   │   ├── BankOfferDiscount.java         # Strategy: Instant bank card off (BANK15)
│                   │   └── DiscountService.java           # O(1) coupon registry & dispatcher
│                   ├── concurrency/
│                   │   ├── CustomerPurchaseTask.java      # Runnable purchase task waiting at barrier
│                   │   ├── FlashSaleCoordinator.java      # CountDownLatch orchestrator & thread pool
│                   │   └── FlashSaleListener.java         # Event observer interface
│                   ├── persistence/
│                   │   ├── FileStorageService.java        # CSV reader/writer with UTF-8 locks
│                   │   ├── ProductRepository.java         # products.csv storage & sample seeder
│                   │   ├── OrderRepository.java           # orders.csv storage & Iterator audits
│                   │   └── CustomerRepository.java        # customers.csv storage & Indian names
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
│                   │   ├── theme/UITheme.java             # SaaS color palette, fonts, buttons
│                   │   └── components/                    # MetricCard, CustomTable, BadgeRenderer
│                   ├── util/
│                   │   ├── CurrencyUtil.java              # Indian Rupee (₹) formatting & parsing
│                   │   ├── ValidationUtil.java            # Defensive input validation routines
│                   │   ├── DateUtil.java                  # Modern java.time date formatting
│                   │   └── UmlDiagramRenderer.java        # Java 2D UML PNG diagram generator
│                   └── test/
│                       └── FlashSaleTestSuite.java        # 15 automated concurrency & unit tests
├── data/
│   ├── products.csv                                       # Persisted product catalog (8 items)
│   ├── orders.csv                                         # Persisted transaction ledger
│   └── customers.csv                                      # Persisted customer profiles (50 items)
├── docs/uml/                                              # PlantUML source & rendered UML image
├── screenshots/                                           # 5 captured UI demonstration screenshots
├── compile.bat, run.bat, test.bat                         # Automated execution batch scripts
└── index.html, vercel.json                                # Interactive live web showcase
```

---

### 8.2 Key Classes and Code

#### Class: `Inventory` &nbsp;|&nbsp; Purpose: Thread-safe monitor ensuring atomic check-and-decrement stock operations
```java
public int deductStock(String productId, int requestedQuantity) throws OutOfStockException {
    if (requestedQuantity <= 0) {
        throw new IllegalArgumentException("Requested quantity must be positive (> 0).");
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

#### Class: `OutOfStockException` &nbsp;|&nbsp; Purpose: Custom checked exception signaling inventory depletion
```java
public class OutOfStockException extends Exception {
    private final String productId;
    private final int requestedQuantity;
    private final int availableStock;

    public OutOfStockException(String productId, int requestedQuantity, int availableStock) {
        super(String.format("Out of stock for Product '%s'! Requested: %d, Available: %d",
                productId, requestedQuantity, availableStock));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }
    // Getters for productId, requestedQuantity, availableStock
}
```

#### Class: `FlashSaleCoordinator` &nbsp;|&nbsp; Purpose: Coordinates simultaneous customer thread rush using CountDownLatch
```java
CountDownLatch startLatch = new CountDownLatch(1);
CountDownLatch completionLatch = new CountDownLatch(totalCustomers);

for (int i = 0; i < totalCustomers; i++) {
    Customer customer = customers.get(i % customers.size());
    threadPool.submit(new CustomerPurchaseTask(
        customer, product, unitsPerCust, couponCode, flashSaleService, startLatch, completionLatch
    ));
}

// Release barrier: All customer threads execute simultaneously at the exact same millisecond
startLatch.countDown();
completionLatch.await(30, TimeUnit.SECONDS);
```

#### Class: `DiscountStrategyBase` &nbsp;|&nbsp; Purpose: Template Method base class enforcing thresholds and discount caps
```java
public final BigDecimal calculateDiscount(BigDecimal totalAmount, int quantity) {
    if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
    if (minimumOrderAmount != null && totalAmount.compareTo(minimumOrderAmount) < 0) return BigDecimal.ZERO;

    BigDecimal rawDiscount = computeRawDiscount(totalAmount, quantity);
    if (maxDiscountAmount != null && rawDiscount.compareTo(maxDiscountAmount) > 0) {
        rawDiscount = maxDiscountAmount;
    }
    return rawDiscount.min(totalAmount); // Discount cannot exceed payable amount
}
```

---

### 8.3 Exceptions, Collections and Threads

| Concept | Used In | Purpose |
| :--- | :--- | :--- |
| **User-defined exception** | `OutOfStockException` | Thrown atomically when requested stock exceeds remaining units; converted to failed order without crashing threads. |
| **Built-in exception** | `IllegalArgumentException` | Thrown defensively for zero or negative quantities and null parameter arguments. |
| **Built-in exception** | `IOException` | Handled with `try-with-resources` during CSV reading and writing in `FileStorageService`. |
| **Collection** | `LinkedHashMap<String, Integer>` | Maintains predictable insertion order and O(1) inventory key lookups in `Inventory`. |
| **Collection** | `ArrayList<Order>` | Stores sequential history of executed orders in `OrderRepository`. |
| **Collection Traversal** | `Iterator<Order>` | Explicit collection traversal in `OrderRepository.calculateSuccessfulRevenue()` and order filtering. |
| **Multithreading** | `ExecutorService` & `Thread` | Pools up to 50 concurrent worker threads to process customer purchase tasks. |
| **Synchronization** | `CountDownLatch` | Synchronizes the thundering-herd flash sale start barrier and tracks simulation completion. |

\pagebreak

---

## CHAPTER 9: OUTPUT SCREENSHOTS

*All screenshots are captured from the running application and cataloged under `screenshots/`:*

* **Figure 9.1: Main Menu & Executive Dashboard** (`screenshots/01_dashboard.png`)  
  Displays live KPI cards (Total Products, Active Inventory, Successful Orders, Blocked Attempts, Total Revenue in ₹, Success Rate) and the recent transactions ledger.

* **Figure 9.2: Product Catalog & Adding/Viewing Records** (`screenshots/02_catalog.png`)  
  Displays the 8 catalog items with images, dual prices (original & flash price), percentage discount badges, and stock levels.

* **Figure 9.3: Searching and Filtering Records** (`screenshots/02_catalog.png`)  
  Dynamic search bar filtering products by name and category dropdown filter.

* **Figure 9.4: Updating and Resetting Stock Records** (`screenshots/03_simulator.png`)  
  Interactive "Reset Stock for Demo" dialog allowing instant reconfiguration of available units.

* **Figure 9.5: Core Operation – Flash Sale Simulation Running** (`screenshots/03_simulator.png`)  
  Shows the active concurrency simulation arena: 50 customers racing simultaneously, real-time progress bar, and color-coded timestamped activity log console.

* **Figure 9.6: Report Display & Product Performance Breakdown** (`screenshots/05_sales_reports.png`)  
  Detailed analytical table displaying attempts, units sold, remaining stock, revenue generated, discounts absorbed, and success percentage per product.

* **Figure 9.7: Invalid Input & Custom Exception Message** (`screenshots/04_order_history.png`)  
  Order history ledger highlighting red `OUT_OF_STOCK` badges and audit reason: *"OutOfStockException: Insufficient stock remaining"*.

* **Figure 9.8: Data Saved and Reloaded from File** (`data/orders.csv`, `data/products.csv`)  
  Demonstrates automated CSV persistence across application restarts with zero data corruption.

\pagebreak

---

## CHAPTER 10: TEST CASES

The project features an automated concurrency and unit test suite ([`FlashSaleTestSuite.java`](file:///c:/Users/SHANMUKHI/Downloads/java%20mini%20project/src/main/java/com/flashdeal/test/FlashSaleTestSuite.java)) executing 15 comprehensive test scenarios:

| ID | Test Scenario | Input | Expected Result | Actual Result | Pass / Fail |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **T1** | Add valid record & purchase | Stock = 10, buy 2 | Stock becomes 8, order recorded | Stock = 8, order recorded | **PASS** |
| **T2** | Purchase with insufficient stock | Stock = 2, buy 5 | `OutOfStockException` thrown | `OutOfStockException` thrown | **PASS** |
| **T3** | Inventory non-negativity under excess | Stock = 3, buy 10 | Stock remains 3 (never negative) | Stock remains 3 | **PASS** |
| **T4** | **Concurrent purchase race condition** | **50 concurrent threads, Stock = 10** | **Exactly 10 succeed, 40 rejected, final stock = 0** | **10 SUCCESS, 40 BLOCKED, Stock = 0** | **PASS** |
| **T5** | Invalid quantity handling | Quantity = 0 or -3 | `IllegalArgumentException` thrown | Exception handled cleanly | **PASS** |
| **T6** | Percentage discount calculation | Amount = ₹2000, `SAVE10` | ₹200 discount calculated (capped) | ₹200.00 discount applied | **PASS** |
| **T7** | Flat discount threshold | Amount = ₹1500, `FLAT100` | ₹100 flat deduction applied | ₹100.00 flat discount applied | **PASS** |
| **T8** | Bank offer calculation & limits | Amount = ₹3000, `BANK15` | 15% discount capped at ₹750 max | ₹450.00 discount applied | **PASS** |
| **T9** | Invalid coupon code | Code = `"INVALID999"` | ₹0.00 discount (no crash) | ₹0.00 discount returned | **PASS** |
| **T10**| Successful order recording | Valid purchase completed | Order status = `SUCCESS`, price > 0 | Order status = `SUCCESS` | **PASS** |
| **T11**| Failed order recording | Stock depleted attempt | Order status = `OUT_OF_STOCK`, ₹0 | Order status = `OUT_OF_STOCK` | **PASS** |
| **T12**| Revenue calculation consistency | 10 success + 40 failed | Revenue sums only 10 success orders | Revenue matches expected sum | **PASS** |
| **T13**| CSV file persistence integrity | Save order & product rows | Valid RFC-4180 CSV formatting | CSV parsed and written cleanly | **PASS** |
| **T14**| Restart inventory reload | Mutate stock & reload | Persisted stock matches reloaded state | Inventory reloaded correctly | **PASS** |
| **T15**| CSV report export generation | Export analytics audit | CSV file generated on disk | Export file created successfully | **PASS** |

\pagebreak

---

## CHAPTER 11: RESULTS AND DISCUSSION

### 11.1 Results
The application executes reliably across all modules and successfully passes all 15 automated concurrency and unit test scenarios. Under extreme concurrency stress testing (50 concurrent shopper threads simultaneously competing for 10 physical stock units), the application achieved **100% data consistency**: exactly 10 units were sold, exactly 40 out-of-stock attempts were blocked, and available inventory dropped strictly to 0 without ever going negative or double-selling.

### 11.2 Challenges Faced

| Problem Encountered | Solution in Java |
| :--- | :--- |
| **Race conditions in stock deduction** | Replaced unsynchronized decrement with atomic `synchronized (inventoryLock)` monitor in `Inventory.java`. |
| **Worker threads freezing Swing GUI** | Decoupled background simulation threads from the Event Dispatch Thread (EDT) using `SwingUtilities.invokeLater`. |
| **Negative payable amounts with coupons** | Enforced `.min(totalAmount)` constraint in `DiscountStrategyBase` ensuring discounts never exceed order totals. |
| **Concurrent order history write collisions** | Applied synchronized file appending in `FileStorageService` to prevent file corruption during simultaneous writes. |
| **CSV parsing errors with commas/quotes** | Implemented RFC-4180 standard escaping in `FileStorageService.escapeCsvField()` to handle commas and quotes cleanly. |

### 11.3 Advantages
* **Guaranteed Thread Safety:** Mutual exclusion eliminates double-selling and negative inventory under heavy concurrency.
* **Extensible Architecture:** Polymorphic discount strategy allows new promotional rules to be added without modifying existing order code.
* **Responsive Visual Feedback:** Real-time activity console and KPI dashboards provide live visual proof of synchronization mechanics.
* **Zero Database Dependency:** Pure Java CSV persistence enables instant deployment and offline evaluation on any standard computer.

### 11.4 Limitations
* The desktop application runs on a single JVM without distributed network sockets or external database clusters (designed intentionally per university standalone guidelines).
* The maximum tested concurrency within default JVM stack limits is 500 simultaneous threads.

### 11.5 Future Scope
* Database connectivity using JDBC and SQLite / PostgreSQL for relational transaction management.
* Network socket client-server architecture allowing multiple client laptops to participate in the flash sale.
* Priority queue scheduling for VIP / Prime customer tiers.
* Live revenue curve visualizations using JFreeChart.

---

## CHAPTER 12: CONCLUSION

The **FLASHDEAL PRO: E-Commerce Flash Sale Simulator** project was successfully designed, developed, and tested. The project comprehensively demonstrates the practical application of core Object-Oriented Programming principles—encapsulation, inheritance, polymorphism, abstraction, interfaces, and custom checked exceptions—alongside advanced Java concurrency primitives (`synchronized` monitors, `CountDownLatch`, `ExecutorService`). Through this project, we acquired practical mastery over multithreaded mutual exclusion, race condition prevention, thread-safe file persistence, and building responsive desktop user interfaces in Java Swing.

---

## CHAPTER 13: REFERENCES

1. Herbert Schildt, *Java: The Complete Reference*, 12th Edition, McGraw Hill Education.
2. E. Balagurusamy, *Programming with Java: A Primer*, 6th Edition, McGraw Hill.
3. Brian Goetz et al., *Java Concurrency in Practice*, Addison-Wesley Professional.
4. Oracle Official Java Documentation: Multithreading & Concurrency Utilities, https://docs.oracle.com/en/java/
5. Object Oriented Programming (Java) Course Curriculum & Laboratory Guidelines, Department of CSE, MNR University.

\pagebreak

---

## APPENDIX A: COMPLETE SOURCE CODE & REPOSITORY

The complete source code, test suites, UML specifications, batch compilation scripts, and documentation are available in the project repository:

* **GitHub Repository:** [https://github.com/shanmukap019-png/OOP_MiniProject_ECommerceFlashSale_Team7](https://github.com/shanmukap019-png/OOP_MiniProject_ECommerceFlashSale_Team7)
* **Live Web Demonstration:** [https://flashdeal-pro-simulator.vercel.app](https://flashdeal-pro-simulator.vercel.app)

### Source Code Summary:
1. `com.flashdeal.app.Main` — Application entry point and FlatLaf Look-and-Feel configuration.
2. `com.flashdeal.model.Product` — Product entity, discount calculations, CSV serialization.
3. `com.flashdeal.model.Customer` — Customer entity implementing `Runnable`.
4. `com.flashdeal.model.Order` & `OrderStatus` — Transaction records with status auditing.
5. `com.flashdeal.model.SaleMetric` — Aggregated sales metrics per product.
6. `com.flashdeal.inventory.Inventory` — Thread-safe atomic inventory deduction monitor.
7. `com.flashdeal.inventory.OutOfStockException` — Checked custom exception.
8. `com.flashdeal.inventory.InventoryService` — Stock update dispatcher and listener management.
9. `com.flashdeal.discount.DiscountStrategy` & `DiscountStrategyBase` — Strategy pattern contracts.
10. `com.flashdeal.discount.PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount` — Concrete strategies.
11. `com.flashdeal.discount.DiscountService` — Coupon registry and lookup engine.
12. `com.flashdeal.concurrency.CustomerPurchaseTask` — Runnable task awaiting barrier release.
13. `com.flashdeal.concurrency.FlashSaleCoordinator` — CountDownLatch orchestrator.
14. `com.flashdeal.concurrency.FlashSaleListener` — Observer interface for UI events.
15. `com.flashdeal.persistence.FileStorageService` — RFC-4180 CSV reader and writer.
16. `com.flashdeal.persistence.ProductRepository`, `OrderRepository`, `CustomerRepository` — Repositories.
17. `com.flashdeal.service.ProductService`, `OrderService`, `SalesReportService`, `FlashSaleService` — Services.
18. `com.flashdeal.ui.MainFrame`, `DashboardPanel`, `ProductPanel`, `FlashSalePanel`, `OrderHistoryPanel`, `ReportsPanel` — GUI views.
19. `com.flashdeal.util.CurrencyUtil`, `DateUtil`, `ValidationUtil`, `UmlDiagramRenderer` — Utilities.
20. `com.flashdeal.test.FlashSaleTestSuite` — 15 automated concurrency and unit test scenarios.

---

## APPENDIX B: TEAM CONTRIBUTION

| Roll Number | Name | Classes / Modules Developed | Report Chapters Written |
| :---: | :--- | :--- | :--- |
| **35** | **Shanmukhi** *(Lead)* | `Product`, `ProductRepository`, `ProductService`, `ProductPanel`, `MainFrame`, `Main` | Chapters 1, 2, 3, 4, 8.1, Appendix A |
| **[Roll No. --]** | [Team Member 2 Name] | `Customer`, `CustomerPurchaseTask`, `FlashSaleCoordinator`, `CustomerRepository` | Chapters 5.1, 5.2, 7 (Algorithms) |
| **[Roll No. --]** | [Team Member 3 Name] | `Inventory`, `InventoryService`, `OutOfStockException`, `FlashSaleService` | Chapters 6 (Java Concepts), 8.2 |
| **[Roll No. --]** | [Team Member 4 Name] | `DiscountStrategy`, `DiscountStrategyBase`, `PercentageDiscount`, `FlatDiscount`, `BankOfferDiscount`, `DiscountService` | Chapters 5.3, 8.3, 11 (Results) |
| **[Roll No. --]** | [Team Member 5 Name] | `Order`, `OrderStatus`, `SaleMetric`, `OrderRepository`, `SalesReportService`, `ReportsPanel` | Chapters 9 (Screenshots), 10 (Test Cases), 12, 13 |
