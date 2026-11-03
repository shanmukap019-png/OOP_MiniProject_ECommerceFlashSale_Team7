import os
import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def create_report():
    doc = Document()
    
    # Page setup - 1 inch margins
    sections = doc.sections
    for section in sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)
        
        # Header & Footer
        header = section.header
        hp = header.paragraphs[0]
        hp.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        hrun = hp.add_run("FLASHDEAL PRO – Java Mini Project Report")
        hrun.font.name = "Times New Roman"
        hrun.font.size = Pt(8.5)
        hrun.font.color.rgb = RGBColor(120, 120, 120)

    # Style defaults
    style = doc.styles['Normal']
    font = style.font
    font.name = 'Times New Roman'
    font.size = Pt(11.5)
    font.color.rgb = RGBColor(20, 20, 20)

    # Helper functions
    def add_p(text="", align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_after=6, bold=False, italic=False, size=11.5, color=RGBColor(20, 20, 20)):
        p = doc.add_paragraph()
        p.alignment = align
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = 1.15
        if text:
            r = p.add_run(text)
            r.bold = bold
            r.italic = italic
            r.font.name = "Times New Roman"
            r.font.size = Pt(size)
            r.font.color.rgb = color
        return p

    def add_h1(text, space_before=14, space_after=6):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(space_before)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.keep_with_next = True
        r = p.add_run(text)
        r.bold = True
        r.font.name = "Times New Roman"
        r.font.size = Pt(14)
        r.font.color.rgb = RGBColor(15, 23, 42)
        return p

    def add_h2(text, space_before=10, space_after=4):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(space_before)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.keep_with_next = True
        r = p.add_run(text)
        r.bold = True
        r.font.name = "Times New Roman"
        r.font.size = Pt(12.5)
        r.font.color.rgb = RGBColor(30, 41, 59)
        return p

    def add_bullet(bold_prefix, text):
        p = doc.add_paragraph(style='List Bullet')
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.line_spacing = 1.15
        r1 = p.add_run(bold_prefix)
        r1.bold = True
        r1.font.name = "Times New Roman"
        r1.font.size = Pt(11)
        r2 = p.add_run(text)
        r2.font.name = "Times New Roman"
        r2.font.size = Pt(11)
        return p

    def add_table_data(headers, rows, col_widths=None):
        table = doc.add_table(rows=len(rows) + 1, cols=len(headers))
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        table.style = 'Table Grid'
        
        # Header Row
        hdr_cells = table.rows[0].cells
        for i, header_text in enumerate(headers):
            hdr_cells[i].text = header_text
            set_cell_background(hdr_cells[i], "F1F5F9")
            set_cell_margins(hdr_cells[i], top=120, bottom=120, left=150, right=150)
            p = hdr_cells[i].paragraphs[0]
            p.alignment = WD_ALIGN_PARAGRAPH.LEFT
            for r in p.runs:
                r.bold = True
                r.font.name = "Times New Roman"
                r.font.size = Pt(10)
                r.font.color.rgb = RGBColor(15, 23, 42)

        # Body Rows
        for r_idx, row_data in enumerate(rows):
            row_cells = table.rows[r_idx + 1].cells
            for c_idx, cell_value in enumerate(row_data):
                row_cells[c_idx].text = str(cell_value)
                set_cell_margins(row_cells[c_idx], top=80, bottom=80, left=150, right=150)
                p = row_cells[c_idx].paragraphs[0]
                p.alignment = WD_ALIGN_PARAGRAPH.LEFT
                for r in p.runs:
                    r.font.name = "Times New Roman"
                    r.font.size = Pt(9.5)
                    r.font.color.rgb = RGBColor(30, 41, 59)
                if r_idx % 2 == 1:
                    set_cell_background(row_cells[c_idx], "F8FAFC")

        if col_widths:
            for row in table.rows:
                for idx, width in enumerate(col_widths):
                    row.cells[idx].width = Inches(width)

        doc.add_paragraph().paragraph_format.space_after = Pt(8)
        return table

    def add_code_block(code_str):
        tbl = doc.add_table(rows=1, cols=1)
        tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        tbl.style = 'Table Grid'
        cell = tbl.rows[0].cells[0]
        set_cell_background(cell, "F8FAFC")
        set_cell_margins(cell, top=140, bottom=140, left=180, right=180)
        p = cell.paragraphs[0]
        p.paragraph_format.space_after = Pt(0)
        p.paragraph_format.line_spacing = 1.05
        r = p.add_run(code_str.strip())
        r.font.name = "Consolas"
        r.font.size = Pt(9)
        r.font.color.rgb = RGBColor(15, 23, 42)
        doc.add_paragraph().paragraph_format.space_after = Pt(6)

    # =========================================================================
    # PAGE 1: TITLE PAGE
    # =========================================================================
    add_p("", space_after=30)
    add_p("MNR UNIVERSITY", align=WD_ALIGN_PARAGRAPH.CENTER, size=18, bold=True, space_after=4)
    add_p("School of Engineering & Technology (SOET)", align=WD_ALIGN_PARAGRAPH.CENTER, size=13, bold=True, space_after=2)
    add_p("Department of Computer Science and Engineering", align=WD_ALIGN_PARAGRAPH.CENTER, size=12, bold=False, space_after=36)

    add_p("MINI PROJECT REPORT", align=WD_ALIGN_PARAGRAPH.CENTER, size=15, bold=True, space_after=6)
    add_p("on", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, italic=True, space_after=6)
    add_p("FLASHDEAL PRO: E-COMMERCE FLASH SALE SIMULATOR", align=WD_ALIGN_PARAGRAPH.CENTER, size=15, bold=True, color=RGBColor(30, 58, 138), space_after=6)
    add_p("(Java Programming Implementation-Based Mini-Project)", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, italic=True, space_after=20)

    add_p("Course: Object Oriented Programming (Java)", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=True, space_after=2)
    add_p("B.Tech – Computer Science and Engineering (CSE / AIML)", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=False, space_after=2)
    add_p("Assigned Project Number: 7", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=True, space_after=26)

    add_p("Submitted by", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=True, space_after=8)
    
    team_data = [
        ["1", "Shanmuka Priya Katta", "35"],
        ["2", "Butul Fatima", "31"],
        ["3", "Soundarya", "33"],
        ["4", "Sathwik K", "34"]
    ]
    add_table_data(["S.No", "Student Name", "Roll Number"], team_data, [0.8, 3.8, 1.8])

    add_p("", space_after=20)
    add_p("Under the guidance of", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=True, space_after=2)
    add_p("[Faculty Name], Lecturer, Department of CSE", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=False, space_after=2)
    add_p("School of Engineering & Technology, MNR University", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=False, space_after=14)
    add_p("Academic Year 2026 – 2027", align=WD_ALIGN_PARAGRAPH.CENTER, size=11, bold=True, space_after=0)

    doc.add_page_break()

    # =========================================================================
    # PAGE 2: CERTIFICATE & DECLARATION
    # =========================================================================
    add_p("CERTIFICATE", align=WD_ALIGN_PARAGRAPH.CENTER, size=15, bold=True, space_after=16)
    add_p(
        "This is to certify that the mini project report titled “FLASHDEAL PRO: E-COMMERCE FLASH SALE SIMULATOR” is a "
        "bonafide record of the work carried out by the students listed on the title page, in partial fulfilment of the requirements "
        "of the course Object Oriented Programming (Java), B.Tech – Computer Science and Engineering, Department of Computer Science "
        "and Engineering, School of Engineering & Technology, MNR University, during the academic year 2026–2027.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_after=50
    )

    # Signature table for Faculty Guide and Dean
    cert_table = doc.add_table(rows=1, cols=2)
    cert_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    cert_table.rows[0].cells[0].width = Inches(3.2)
    cert_table.rows[0].cells[1].width = Inches(3.2)
    p_guide = cert_table.rows[0].cells[0].paragraphs[0]
    p_guide.add_run("Faculty Guide").bold = True
    p_dean = cert_table.rows[0].cells[1].paragraphs[0]
    p_dean.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    p_dean.add_run("Dean, School of Engineering & Technology").bold = True

    add_p("", space_after=25)
    add_p("Date: ________________________                                 Place: ________________________", space_after=30)

    add_p("DECLARATION", align=WD_ALIGN_PARAGRAPH.CENTER, size=15, bold=True, space_after=16)
    add_p(
        "We hereby declare that this mini project report is the result of our own work. The program was written, executed "
        "and tested by us. Any help, book or website used has been mentioned in the references.",
        align=WD_ALIGN_PARAGRAPH.JUSTIFY, space_after=20
    )

    add_p("Signature of the Students:", bold=True, space_after=12)
    add_p("1. Shanmuka Priya Katta (Roll No: 35)", space_after=6, bold=True)
    add_p("2. Butul Fatima (Roll No: 31)", space_after=6, bold=True)
    add_p("3. Soundarya (Roll No: 33)", space_after=6, bold=True)
    add_p("4. Sathwik K (Roll No: 34)", space_after=10, bold=True)

    doc.add_page_break()

    # =========================================================================
    # PAGE 3: CONTENTS
    # =========================================================================
    add_p("CONTENTS", align=WD_ALIGN_PARAGRAPH.CENTER, size=15, bold=True, space_after=16)
    
    contents_data = [
        ["1", "Aim and Abstract", "1"],
        ["2", "Introduction and Problem Statement", "1"],
        ["3", "Objectives", "1"],
        ["4", "Requirements", "2"],
        ["5", "Class Design", "2"],
        ["6", "Java Concepts Used", "3"],
        ["7", "Program Logic (Algorithm)", "4"],
        ["8", "Implementation", "4"],
        ["9", "Output Screenshots", "5"],
        ["10", "Test Cases", "5"],
        ["11", "Results and Discussion", "6"],
        ["12", "Conclusion", "6"],
        ["13", "References", "6"],
        ["A", "Appendix A – Complete Source Code & Repository", "7"],
        ["B", "Appendix B – Team Contribution", "7"]
    ]
    add_table_data(["Chapter", "Title", "Page"], contents_data, [1.0, 4.4, 1.0])

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 1: AIM AND ABSTRACT
    # =========================================================================
    add_h1("CHAPTER 1: AIM AND ABSTRACT")
    add_h2("1.1 Aim")
    add_p(
        "To design and develop FLASHDEAL PRO: E-Commerce Flash Sale Simulator in Java using Object Oriented Programming concepts, "
        "so that limited promotional inventory can be stored, searched, atomically updated, and processed concurrently under extreme shopping "
        "rush without race conditions, overselling, or negative stock."
    )

    add_h2("1.2 Abstract")
    add_p(
        "This project is an advanced e-commerce flash sale simulation desktop application developed in Java Swing. It is used to emulate high-velocity "
        "\"Big Sale Day\" flash events where hundreds of customers compete simultaneously for limited stock items. The program uses classes and objects, "
        "encapsulation, inheritance, abstract classes, interfaces, custom checked exceptions (OutOfStockException), multithreading (Thread, Runnable), "
        "thread synchronization barriers (CountDownLatch), monitor locking (synchronized), and the Java Collections Framework (LinkedHashMap, ArrayList, Iterator). "
        "Transaction records and catalog data are persistently stored using standard RFC-4180 compliant CSV files with atomic stream locks."
    )

    # =========================================================================
    # CHAPTER 2: INTRODUCTION AND PROBLEM STATEMENT
    # =========================================================================
    add_h1("CHAPTER 2: INTRODUCTION AND PROBLEM STATEMENT")
    add_h2("2.1 Introduction")
    add_p(
        "In modern e-commerce ecosystems during mega-sales events (such as Flipkart Big Billion Days, Amazon Great Indian Festival, or limited-quantity "
        "flash drops), thousands of shoppers click \"Buy Now\" at the exact same second. A robust software architecture is critical to guarantee data "
        "consistency and fairness under sudden concurrency spikes. Java is particularly well-suited for this domain because it is object-oriented, strictly "
        "typed, platform-independent, and provides built-in enterprise-grade concurrency utilities (java.util.concurrent), atomic synchronization primitives, "
        "and powerful data structures such as LinkedHashMap and ArrayList."
    )

    add_h2("2.2 Problem Statement")
    add_p(
        "In traditional or uncoordinated systems, when multiple threads access and update shared inventory variables at the same instant, critical section "
        "race conditions occur: multiple customer threads read stock = 1 simultaneously and approve purchases, leaving the inventory in an invalid negative state "
        "(e.g., stock = -49), double-selling units to distinct customers, and freezing the application interface."
    )

    add_h2("2.3 Proposed Solution")
    add_p("The proposed Java application addresses these challenges through a layered, object-oriented, thread-safe architecture:")
    add_bullet("• Add and store records: ", "Maintain a structured catalog of consumer electronics products with dual pricing (original and flash sale in ₹) and stock counters.")
    add_bullet("• Search and update records: ", "Search products dynamically by name, filter by category, and adjust inventory levels in real time.")
    add_bullet("• Perform atomic purchase operations: ", "Encapsulate check-and-decrement inventory operations within a synchronized critical section.")
    add_bullet("• Simulate concurrent shoppers: ", "Launch up to 500 competing customer threads simultaneously using a CountDownLatch(1) release barrier.")
    add_bullet("• Display real-time visual reports: ", "Render executive KPI cards, high-contrast live activity logging, and audited transaction ledgers in a responsive Swing GUI.")
    add_bullet("• Save data permanently: ", "Store and reload product catalogs, registered customers, and order ledgers using thread-safe CSV file I/O.")

    # =========================================================================
    # CHAPTER 3: OBJECTIVES
    # =========================================================================
    add_h1("CHAPTER 3: OBJECTIVES")
    add_p("The objectives of this project are:")
    add_bullet("• ", "To apply classes, objects, constructors, and methods in structuring domain models (Product, Customer, Order, SaleMetric).")
    add_bullet("• ", "To apply encapsulation using private data members with defensive validation getters and setters.")
    add_bullet("• ", "To apply inheritance and method overriding using an abstract base class (DiscountStrategyBase) extended by concrete promotional strategies (PercentageDiscount, FlatDiscount, BankOfferDiscount).")
    add_bullet("• ", "To use abstract classes and interfaces (DiscountStrategy, FlashSaleListener, Runnable, StockChangeListener).")
    add_bullet("• ", "To handle errors using robust exception handling, including a user-defined checked exception (OutOfStockException).")
    add_bullet("• ", "To store and process data using the Java Collections Framework (LinkedHashMap, ArrayList, and explicit Iterator<Order> traversal).")
    add_bullet("• ", "To execute multi-threaded concurrent simulations using Thread, Runnable, ExecutorService, and CountDownLatch.")
    add_bullet("• ", "To save data permanently using standard File I/O (BufferedReader, BufferedWriter, CSV parsing).")
    add_bullet("• ", "To organise the program into modular packages and verify all invariants with 15 automated test cases.")

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 4: REQUIREMENTS
    # =========================================================================
    add_h1("CHAPTER 4: REQUIREMENTS")
    add_h2("4.1 Functional Requirements")
    add_p("The program should be able to:")
    add_bullet("• ", "Add and browse product records with dual pricing in ₹ and dynamic discount percentage badges.")
    add_bullet("• ", "Search and filter products by name and category (Smartphones, Audio, Wearables, Accessories).")
    add_bullet("• ", "Update and reset product stock levels for demonstration purposes.")
    add_bullet("• ", "Execute concurrent flash sale simulations (Core Operation) with 1 to 500 simultaneous customer threads.")
    add_bullet("• ", "Enforce atomic check-and-decrement stock deduction without overselling.")
    add_bullet("• ", "Apply polymorphic promotional coupons (SAVE10, FLAT100, BANK15) with minimum order rules and maximum discount ceilings.")
    add_bullet("• ", "Display executive KPI summary cards, filterable order history, and product analytics reports.")
    add_bullet("• ", "Show clear informative error messages for invalid quantities, coupon rejections, and out-of-stock events.")

    add_h2("4.2 Input and Output")
    io_data = [
        ["Product Selection", "String (ID)", "Must match existing product ID in repository", "Selected product preview and live stock updated"],
        ["Customer Count", "int", "Range: 1 to 500 (positive integer)", "Worker threads spawned in pool"],
        ["Units per Customer", "int", "Range: 1 to 10 (strictly > 0)", "Quantity requested per purchase task"],
        ["Coupon Code", "String", "Optional; validated against coupon registry", "Applied discount amount in ₹ (or ₹0.00)"],
        ["Demo Stock Reset", "int", "Non-negative integer (>= 0)", "Available stock updated in memory and CSV"],
        ["Order Filter", "String / Enum", "Valid text or status (SUCCESS, OUT_OF_STOCK)", "Filtered table of audited customer orders"]
    ]
    add_table_data(["Input", "Java Data Type", "Validation", "Output"], io_data, [1.4, 1.2, 1.8, 2.0])

    add_h2("4.3 Software and Hardware Requirements")
    req_data = [
        ["Programming Language", "Java (JDK 17 LTS or later, tested up to JDK 26)"],
        ["Development Tools / IDE", "VS Code / IntelliJ IDEA / Eclipse, PowerShell, Git"],
        ["User Interface", "Java Swing & AWT (Desktop GUI) + Responsive HTML/CSS/JS (Web Showcase)"],
        ["Storage", "Plain-text CSV files (products.csv, orders.csv, customers.csv) with UTF-8 encoding"],
        ["Concurrency API", "java.util.concurrent (CountDownLatch, ExecutorService, synchronized)"],
        ["Operating System", "Windows 10 / 11, Linux, or macOS"],
        ["Hardware Requirements", "Dual-core CPU 2.0 GHz or higher, 4 GB RAM minimum (8 GB recommended), 200 MB disk space"]
    ]
    add_table_data(["Item", "Details"], req_data, [2.0, 4.4])

    # =========================================================================
    # CHAPTER 5: CLASS DESIGN
    # =========================================================================
    add_h1("CHAPTER 5: CLASS DESIGN")
    add_h2("5.1 List of Classes")
    class_list_data = [
        ["Main", "Driver Class", "Bootstrap; sets Look-and-Feel and launches MainFrame on EDT."],
        ["Product", "Domain Entity", "Encapsulates pricing in ₹, inventory status transitions, and discount math."],
        ["Customer", "Class (implements Runnable)", "Customer profile capable of executing purchase tasks in concurrent threads."],
        ["Order", "Domain Entity", "Immutable transaction audit record with status, discounts, and timestamp."],
        ["OrderStatus", "enum", "Order states: SUCCESS, OUT_OF_STOCK, FAILED."],
        ["SaleMetric", "Aggregation Class", "Tracks attempts, units sold, revenue, discounts, and success rate."],
        ["Inventory", "Thread-Safe Monitor", "Encapsulates monitor lock for atomic stock deduction and restoration."],
        ["OutOfStockException", "User-Defined Exception", "Custom checked exception thrown when requested units exceed stock."],
        ["DiscountStrategy", "Interface", "Standard contract for promotional algorithms."],
        ["DiscountStrategyBase", "Abstract Class", "Template method base enforcing order thresholds and discount caps."],
        ["PercentageDiscount", "Class (extends Base)", "Calculates % discount with maximum ceiling (e.g., SAVE10)."],
        ["FlatDiscount", "Class (extends Base)", "Calculates flat rupee discount on eligible orders (e.g., FLAT100)."],
        ["BankOfferDiscount", "Class (extends Base)", "Calculates instant card discount with savings limit (e.g., BANK15)."],
        ["FlashSaleCoordinator", "Concurrency Manager", "Spawns thread pool and coordinates simultaneous CountDownLatch rush."]
    ]
    add_table_data(["Class / Interface", "Type", "Purpose"], class_list_data, [1.8, 1.8, 2.8])

    doc.add_page_break()

    add_h2("5.2 Data Members and Methods of Main Classes")
    members_data = [
        ["Product", "Data members: productId, name, description, category, originalPrice, flashSalePrice, initialStock, availableStock, status\nConstructors: Parameterized constructor with validation.\nMethods: getProductId(), getAvailableStock(), setAvailableStock(), calculateDiscountPercentage(), toCsvRow()"],
        ["Inventory", "Data members: stockMap (Map<String, Integer>), inventoryLock (Object)\nConstructors: Inventory(Map<String, Integer> initialStock)\nMethods: deductStock(id, qty), restoreStock(id, qty), getStock(id), setStock(id, qty), getAllStockSnapshot()"],
        ["DiscountStrategyBase", "Data members: couponCode, description, minimumOrderAmount, maxDiscountAmount\nConstructors: Parameterized base constructor.\nMethods: calculateDiscount(total, qty) (Template Method), computeRawDiscount(total, qty) (Abstract)"],
        ["FlashSaleCoordinator", "Data members: flashSaleService, listeners, threadPool, isRunning\nConstructors: FlashSaleCoordinator(FlashSaleService service)\nMethods: startSimulation(prodId, count, qty, coupon, customers), addListener(), isRunning()"]
    ]
    add_table_data(["Class", "Details (Data Members, Constructors, and Methods)"], members_data, [1.8, 4.6])

    add_h2("5.3 Relationships Between Classes")
    rel_data = [
        ["DiscountStrategyBase – PercentageDiscount", "Inheritance (IS-A)", "PercentageDiscount extends DiscountStrategyBase and overrides computeRawDiscount()."],
        ["DiscountStrategy – DiscountStrategyBase", "Implements", "DiscountStrategyBase implements the DiscountStrategy interface contract."],
        ["Customer – Runnable", "Implements", "Customer implements Runnable to execute purchase tasks concurrently."],
        ["CustomerPurchaseTask – Runnable", "Implements", "CustomerPurchaseTask implements Runnable to race at the CountDownLatch."],
        ["InventoryService – Inventory", "Composition (HAS-A)", "InventoryService owns and manages the thread-safe Inventory monitor."],
        ["OrderRepository – Order", "Aggregation (HAS-A)", "OrderRepository manages a collection of Orders and traverses via Iterator<Order>."]
    ]
    add_table_data(["Classes", "Relationship", "Explanation"], rel_data, [2.2, 1.5, 2.7])

    # Embed UML Diagram if exists
    if os.path.exists("docs/uml/class-diagram.png"):
        add_p("Figure 5.1: High-Resolution UML Class Diagram Specification", align=WD_ALIGN_PARAGRAPH.CENTER, bold=True, size=10)
        doc.add_picture("docs/uml/class-diagram.png", width=Inches(6.2))
        add_p("", space_after=10)

    # =========================================================================
    # CHAPTER 6: JAVA CONCEPTS USED
    # =========================================================================
    add_h1("CHAPTER 6: JAVA CONCEPTS USED")
    concepts_data = [
        ["Classes, objects and constructors", "Product, Customer, Order, SaleMetric (Constructors initialize state)"],
        ["Encapsulation (private, getters, setters)", "Product.getAvailableStock(), Product.setAvailableStock(), Order.getFinalAmount()"],
        ["Inheritance and super keyword", "PercentageDiscount, FlatDiscount calling super(...) in DiscountStrategyBase"],
        ["Method overloading", "ProductService.searchProducts(query) and searchProducts(query, category)"],
        ["Method overriding", "PercentageDiscount.computeRawDiscount(), BadgeRenderer.getTableCellRendererComponent()"],
        ["Abstract class / interface", "Abstract class DiscountStrategyBase; Interfaces: DiscountStrategy, FlashSaleListener, Runnable"],
        ["Packages", "10 custom packages: com.flashdeal.app, model, inventory, discount, concurrency, service, etc."],
        ["static and final keywords", "CurrencyUtil.formatINR(), ValidationUtil.validatePositive(), private final Object inventoryLock"],
        ["Exception handling", "User-defined checked OutOfStockException; try-with-resources in FileStorageService"],
        ["Collections (ArrayList, HashMap)", "LinkedHashMap in Inventory, ArrayList<Order> in OrderRepository, HashMap in DiscountService"],
        ["Collection Traversal (Iterator)", "Explicit Iterator<Order> in OrderRepository.calculateSuccessfulRevenue() and order filtering"],
        ["Multithreading (Thread / Runnable)", "CustomerPurchaseTask implements Runnable, ExecutorService thread pool, SwingUtilities.invokeLater"],
        ["File I/O or JDBC", "BufferedReader, BufferedWriter, UTF-8 CSV streams in FileStorageService"]
    ]
    add_table_data(["Java Concept", "Where Used (Class / Method)"], concepts_data, [2.5, 3.9])

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 7: PROGRAM LOGIC (ALGORITHM)
    # =========================================================================
    add_h1("CHAPTER 7: PROGRAM LOGIC (ALGORITHM)")
    add_h2("7.1 Main Program Flow")
    flow_steps = [
        "Start the program: Main.java executes and configures the modern UI Look-and-Feel.",
        "Load saved data into collection: Load CSV records into memory (products.csv, customers.csv, orders.csv).",
        "Display the main menu: Launch MainFrame with sidebar navigation and show the Executive Dashboard.",
        "Read the user's choice: User selects one of the operational panels (Catalog, Simulator, Order History, Reports).",
        "Configure flash sale simulation: User selects product, configures customer count (1–500), units, and coupon code.",
        "Perform the operation: FlashSaleCoordinator spawns concurrent threads, releases CountDownLatch, and executes atomic purchases.",
        "Validate and handle exceptions: Stock checked inside synchronized (inventoryLock); throws OutOfStockException if exhausted.",
        "Save data to file: Append executed transactions atomically to orders.csv and update product stock in products.csv.",
        "Repeat from step 4 until user selects Exit / Stop."
    ]
    for idx, step in enumerate(flow_steps, 1):
        add_p(f"{idx}. {step}", space_after=3)

    add_h2("7.2 Algorithm for the Core Operation (Atomic Stock Deduction)")
    add_p(
        "Algorithm: AtomicConcurrentPurchase(customerId, productId, requestedQty, couponCode)\n"
        "Input: Customer ID, Product ID, Requested Quantity (int), Coupon Code (String)\n"
        "Output: Order object (Status: SUCCESS, OUT_OF_STOCK, or FAILED)",
        bold=True, size=10.5
    )
    core_steps = [
        "Validate input parameters (requestedQty > 0, non-empty productId, valid customerId).",
        "Enter atomic critical section using synchronized(inventoryLock):\n  a. Read currentStock from stockMap for productId.\n  b. If currentStock < requestedQty: throw OutOfStockException(productId, requestedQty, currentStock).\n  c. Deduct stock: remainingStock = currentStock - requestedQty.\n  d. Update stockMap with remainingStock.",
        "If OutOfStockException is caught: create Order with status = OUT_OF_STOCK, finalAmount = 0.00, failureReason = \"Insufficient stock\", persist to orders.csv, and return.",
        "Retrieve product unit flash sale price and compute originalTotal = unitPrice × requestedQty.",
        "Look up coupon in DiscountService and evaluate polymorphic discount: discountAmount = strategy.calculateDiscount(originalTotal, requestedQty).",
        "Compute finalPayable = originalTotal - discountAmount. Generate unique Order ID (\"ORD-\" + sequence) with status = SUCCESS, persist to orders.csv, notify UI listeners, and return Order."
    ]
    for idx, step in enumerate(core_steps, 1):
        add_p(f"Step {idx}: {step}", space_after=4)

    # =========================================================================
    # CHAPTER 8: IMPLEMENTATION
    # =========================================================================
    add_h1("CHAPTER 8: IMPLEMENTATION")
    add_h2("8.1 Project Structure")
    project_tree = (
        "OOP_MiniProject_ECommerceFlashSale_Team7/\n"
        "├── src/main/java/com/flashdeal/\n"
        "│   ├── app/Main.java                         # Driver class & UI bootstrap\n"
        "│   ├── model/ (Product, Customer, Order, OrderStatus, SaleMetric)\n"
        "│   ├── inventory/ (Inventory, InventoryService, OutOfStockException)\n"
        "│   ├── discount/ (DiscountStrategy, DiscountStrategyBase, Percentage, Flat, Bank)\n"
        "│   ├── concurrency/ (CustomerPurchaseTask, FlashSaleCoordinator, FlashSaleListener)\n"
        "│   ├── service/ (ProductService, OrderService, SalesReportService, FlashSaleService)\n"
        "│   ├── persistence/ (FileStorageService, ProductRepository, OrderRepository, CustomerRepo)\n"
        "│   ├── ui/ (MainFrame, DashboardPanel, ProductPanel, FlashSalePanel, OrderHistoryPanel, ReportsPanel)\n"
        "│   └── util/ (CurrencyUtil, ValidationUtil, DateUtil, UmlDiagramRenderer)\n"
        "├── data/ (products.csv, orders.csv, customers.csv)\n"
        "└── test/FlashSaleTestSuite.java              # 15 automated concurrency & unit tests"
    )
    add_code_block(project_tree)

    add_h2("8.2 Key Classes and Code")
    add_p("Class: Inventory | Purpose: Thread-safe monitor ensuring atomic check-and-decrement stock operations", bold=True, size=10.5)
    code_inv = (
        "public int deductStock(String productId, int requestedQuantity) throws OutOfStockException {\n"
        "    if (requestedQuantity <= 0) {\n"
        "        throw new IllegalArgumentException(\"Requested quantity must be positive (> 0).\");\n"
        "    }\n"
        "    synchronized (inventoryLock) {\n"
        "        int currentStock = stockMap.getOrDefault(productId, 0);\n"
        "        if (currentStock < requestedQuantity) {\n"
        "            throw new OutOfStockException(productId, requestedQuantity, currentStock);\n"
        "        }\n"
        "        int remainingStock = currentStock - requestedQuantity;\n"
        "        stockMap.put(productId, remainingStock);\n"
        "        return remainingStock;\n"
        "    }\n"
        "}"
    )
    add_code_block(code_inv)

    add_p("Class: FlashSaleCoordinator | Purpose: Multi-threaded start barrier releasing concurrent shoppers simultaneously", bold=True, size=10.5)
    code_coord = (
        "CountDownLatch startLatch = new CountDownLatch(1);\n"
        "CountDownLatch completionLatch = new CountDownLatch(totalCustomers);\n"
        "for (Customer customer : customers) {\n"
        "    threadPool.submit(new CustomerPurchaseTask(\n"
        "        customer, product, unitsPerCust, couponCode, flashSaleService, startLatch, completionLatch\n"
        "    ));\n"
        "}\n"
        "startLatch.countDown(); // Barrier lifted: all customer threads race simultaneously\n"
        "completionLatch.await(30, TimeUnit.SECONDS);"
    )
    add_code_block(code_coord)

    add_h2("8.3 Exceptions, Collections and Threads")
    ect_data = [
        ["User-defined exception", "OutOfStockException", "Thrown when stock is exhausted; caught gracefully to record failed order."],
        ["Built-in exception", "IllegalArgumentException", "Enforces positive purchase quantities and non-empty inputs."],
        ["Collection", "LinkedHashMap<String, Integer>", "Maintains thread-safe inventory map with O(1) lookups."],
        ["Thread", "CustomerPurchaseTask", "Background concurrency worker executing purchases at start barrier."]
    ]
    add_table_data(["Concept", "Used In", "Purpose"], ect_data, [1.8, 2.0, 2.6])

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 9: OUTPUT SCREENSHOTS
    # =========================================================================
    add_h1("CHAPTER 9: OUTPUT SCREENSHOTS")
    screenshots = [
        ("screenshots/01_dashboard.png", "Figure 9.1: Main Menu & Executive Dashboard (KPI Metrics & Recent Orders)"),
        ("screenshots/02_catalog.png", "Figure 9.2: Product Catalog & Stock Management View"),
        ("screenshots/03_simulator.png", "Figure 9.3: Core Operation – Flash Sale Simulator Running with 50 Concurrent Customer Threads"),
        ("screenshots/04_order_history.png", "Figure 9.4: Order History Ledger showing OutOfStockException Handling and Status Badges"),
        ("screenshots/05_sales_reports.png", "Figure 9.5: Product Breakdown Analytics & CSV Export View")
    ]
    for img_path, caption in screenshots:
        if os.path.exists(img_path):
            add_p(caption, align=WD_ALIGN_PARAGRAPH.CENTER, bold=True, size=10, space_after=4)
            doc.add_picture(img_path, width=Inches(5.6))
            add_p("", space_after=12)

    doc.add_page_break()

    # =========================================================================
    # CHAPTER 10: TEST CASES
    # =========================================================================
    add_h1("CHAPTER 10: TEST CASES")
    add_p("The project features an automated concurrency and unit test suite (FlashSaleTestSuite.java) executing 15 comprehensive test scenarios:")
    test_cases_data = [
        ["T1", "Add valid purchase", "Stock = 10, buy 2", "Stock = 8, order recorded", "PASS"],
        ["T2", "Insufficient stock", "Stock = 2, buy 5", "OutOfStockException thrown", "PASS"],
        ["T3", "Non-negative inventory", "Stock = 3, buy 10", "Stock remains 3 (never negative)", "PASS"],
        ["T4", "Concurrent Race Test", "50 threads, Stock = 10", "10 SUCCESS, 40 BLOCKED, Stock = 0", "PASS"],
        ["T5", "Invalid quantity", "Quantity = 0 or -3", "IllegalArgumentException thrown", "PASS"],
        ["T6", "Percentage discount", "Amount = ₹2000, SAVE10", "₹200.00 discount applied", "PASS"],
        ["T7", "Flat discount", "Amount = ₹1500, FLAT100", "₹100.00 flat discount applied", "PASS"],
        ["T8", "Bank offer discount", "Amount = ₹3000, BANK15", "₹450.00 card discount applied", "PASS"],
        ["T9", "Invalid coupon", "Code = \"INVALID\"", "₹0.00 discount (no failure)", "PASS"],
        ["T10", "Successful order audit", "Valid purchase", "Order status = SUCCESS", "PASS"],
        ["T11", "Failed order audit", "Stock depleted", "Order status = OUT_OF_STOCK", "PASS"],
        ["T12", "Revenue consistency", "10 success + 40 failed", "Revenue sums only success orders", "PASS"],
        ["T13", "CSV persistence", "Save order & product", "RFC-4180 CSV written cleanly", "PASS"],
        ["T14", "Restart inventory reload", "Mutate stock & reload", "Persisted stock matches reload", "PASS"],
        ["T15", "CSV report export", "Export analytics audit", "CSV file generated on disk", "PASS"]
    ]
    add_table_data(["ID", "Test Scenario", "Input", "Expected Result", "Status"], test_cases_data, [0.6, 1.8, 1.5, 1.9, 0.6])

    # =========================================================================
    # CHAPTER 11: RESULTS AND DISCUSSION
    # =========================================================================
    add_h1("CHAPTER 11: RESULTS AND DISCUSSION")
    add_h2("11.1 Results")
    add_p(
        "The program works correctly for all the operations listed above. Under extreme concurrency stress testing (50 concurrent shopper threads "
        "simultaneously competing for 10 physical stock units), the application achieved 100% data consistency: exactly 10 units were sold, exactly 40 out-of-stock "
        "attempts were blocked, and available inventory dropped strictly to 0 without ever going negative or double-selling."
    )

    add_h2("11.2 Challenges Faced")
    challenges_data = [
        ["Race conditions in inventory deduction", "Encapsulated availability check & decrement in synchronized (inventoryLock)."],
        ["Worker threads freezing Swing GUI", "Decoupled simulation threads from EDT using SwingUtilities.invokeLater."],
        ["Negative payable totals with coupons", "Enforced .min(totalAmount) in DiscountStrategyBase template method."],
        ["CSV data corruption under concurrent writes", "Applied synchronized file appending in FileStorageService."]
    ]
    add_table_data(["Problem", "Solution in Java"], challenges_data, [2.5, 3.9])

    add_h2("11.3 Advantages")
    add_bullet("• ", "Guaranteed thread safety and zero overselling under heavy concurrency.")
    add_bullet("• ", "Highly extensible architecture using the Strategy Pattern for discounts.")
    add_bullet("• ", "Modern, responsive desktop GUI with real-time activity streaming.")
    add_bullet("• ", "Pure Java file-based persistence with zero third-party database dependencies.")

    add_h2("11.4 Limitations")
    add_bullet("• ", "Runs on a single JVM without distributed network sockets or external database clusters.")
    add_bullet("• ", "Maximum tested concurrency within default JVM stack memory is 500 simultaneous threads.")

    add_h2("11.5 Future Scope")
    add_bullet("• ", "Database connectivity using JDBC and SQLite / PostgreSQL.")
    add_bullet("• ", "Network socket client-server architecture enabling multiple client laptops.")
    add_bullet("• ", "Priority customer queues (e.g., VIP / Prime shoppers).")
    add_bullet("• ", "Dynamic real-time charting using JFreeChart.")

    # =========================================================================
    # CHAPTER 12: CONCLUSION
    # =========================================================================
    add_h1("CHAPTER 12: CONCLUSION")
    add_p(
        "The FLASHDEAL PRO: E-Commerce Flash Sale Simulator project was successfully designed, developed, and tested. The project comprehensively "
        "demonstrates the practical application of core Object-Oriented Programming principles—encapsulation, inheritance, polymorphism, abstraction, "
        "interfaces, and custom checked exceptions—alongside advanced Java concurrency primitives (synchronized monitors, CountDownLatch, ExecutorService). "
        "Through this project, we gained invaluable practical experience in multithreaded mutual exclusion, race condition elimination, and thread-safe file persistence."
    )

    # =========================================================================
    # CHAPTER 13: REFERENCES
    # =========================================================================
    add_h1("CHAPTER 13: REFERENCES")
    refs = [
        "Herbert Schildt, Java: The Complete Reference, 12th Edition, McGraw Hill Education.",
        "E. Balagurusamy, Programming with Java: A Primer, 6th Edition, McGraw Hill.",
        "Brian Goetz et al., Java Concurrency in Practice, Addison-Wesley Professional.",
        "Oracle Official Java Documentation: Multithreading & Concurrency Utilities, https://docs.oracle.com/en/java/",
        "Object Oriented Programming (Java) Course Guidelines, Department of CSE, MNR University."
    ]
    for idx, ref in enumerate(refs, 1):
        add_p(f"{idx}. {ref}", space_after=3)

    doc.add_page_break()

    # =========================================================================
    # APPENDICES
    # =========================================================================
    add_h1("APPENDIX A: COMPLETE SOURCE CODE & REPOSITORY")
    add_p("The complete, commented source code of every class, unit tests, UML specifications, and compilation scripts are available in the repository:")
    add_bullet("• GitHub Repository: ", "https://github.com/shanmukap019-png/OOP_MiniProject_ECommerceFlashSale_Team7")
    add_bullet("• Live Web Demonstration: ", "https://flashdeal-pro-simulator.vercel.app")

    add_p("", space_after=15)
    add_h1("APPENDIX B: TEAM CONTRIBUTION")
    contrib_data = [
        ["35", "Shanmuka Priya Katta", "Product, ProductRepository, ProductService, ProductPanel, MainFrame, Main", "Chapters 1, 2, 3, 4, 8.1, Appendix A"],
        ["31", "Butul Fatima", "Customer, CustomerPurchaseTask, FlashSaleCoordinator, CustomerRepository", "Chapters 5.1, 5.2, 7 (Algorithms)"],
        ["33", "Soundarya", "Inventory, InventoryService, OutOfStockException, FlashSaleService", "Chapters 6 (Java Concepts), 8.2"],
        ["34", "Sathwik K", "DiscountStrategy, DiscountStrategyBase, PercentageDiscount, FlatDiscount, BankOfferDiscount, Order, OrderRepository, ReportsPanel", "Chapters 5.3, 8.3, 9, 10, 11, 12, 13, Appendix B"]
    ]
    add_table_data(["Roll Number", "Name", "Classes / Modules Developed", "Report Chapters Written"], contrib_data, [1.0, 1.8, 2.2, 1.4])

    # Save to report/PROJECT_REPORT.docx
    output_path = "report/PROJECT_REPORT.docx"
    doc.save(output_path)
    print(f"SUCCESS: Report saved to {output_path}")

if __name__ == "__main__":
    create_report()
