package com.flashdeal.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility generating a high-resolution, professional UML class diagram PNG image
 * using standard Java 2D Graphics. Saves directly to docs/uml/class-diagram.png.
 */
public class UmlDiagramRenderer {

    public static void main(String[] args) {
        generateDiagram(new File("docs/uml/class-diagram.png"));
    }

    public static void generateDiagram(File outputFile) {
        int width = 2100;
        int height = 1350;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // Enable high quality antialiasing and text rendering
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Canvas Background
        g.setColor(new Color(248, 250, 252));
        g.fillRect(0, 0, width, height);

        // Header Banner
        g.setColor(new Color(15, 23, 42));
        g.fillRect(0, 0, width, 90);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g.drawString("FLASHDEAL PRO - Object-Oriented Architecture & UML Class Diagram", 40, 42);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g.setColor(new Color(148, 163, 184));
        g.drawString("MNR University | Dept: CSE / AIML | Project #7: E-Commerce Flash Sale Simulator | Student Roll: 35", 40, 72);

        // Draw Package Containers and Classes
        // ROW 1: MODEL & INVENTORY & DISCOUNT
        drawPackageBox(g, 40, 110, 480, 580, "com.flashdeal.model", new Color(224, 231, 255));
        drawClassCard(g, 60, 145, 440, 170, "Product", "class",
                new String[]{"- productId: String", "- name: String", "- flashSalePrice: BigDecimal", "- availableStock: int", "- status: String"},
                new String[]{"+ calculateDiscount(): void", "+ updateStatus(): void", "+ isAvailable(qty): boolean"});

        drawClassCard(g, 60, 335, 440, 150, "Order", "class",
                new String[]{"- orderId: String", "- customerName: String", "- quantity: int", "- finalAmount: BigDecimal", "- status: OrderStatus"},
                new String[]{"+ getFormattedFinalAmount(): String", "+ getFormattedTimestamp(): String"});

        drawClassCard(g, 60, 505, 210, 160, "OrderStatus", "enum",
                new String[]{"SUCCESS", "FAILED", "OUT_OF_STOCK"},
                new String[]{"+ fromString(): OrderStatus"});

        drawClassCard(g, 290, 505, 210, 160, "Customer", "Runnable",
                new String[]{"- customerId: String", "- name: String", "- startLatch: Latch"},
                new String[]{"+ run(): void"});

        // COLUMN 2: INVENTORY & CONCURRENCY
        drawPackageBox(g, 550, 110, 480, 580, "com.flashdeal.inventory", new Color(254, 243, 199));
        drawClassCard(g, 570, 145, 440, 180, "Inventory", "Thread-Safe",
                new String[]{"- stockMap: Map<String, Integer>", "- inventoryLock: Object"},
                new String[]{"+ deductStock(id, qty): int throws OutOfStockException", "+ setStock(id, stock): void", "+ getStock(id): int", "+ hasAvailableStock(id, qty): boolean"});

        drawClassCard(g, 570, 345, 440, 160, "InventoryService", "class",
                new String[]{"- inventory: Inventory", "- listeners: List<StockChangeListener>"},
                new String[]{"+ deductStockAtomically(id, qty): int", "+ setStock(id, stock): void", "+ addStockListener(l): void"});

        drawClassCard(g, 570, 525, 440, 140, "OutOfStockException", "Exception",
                new String[]{"- productId: String", "- requestedQuantity: int", "- availableQuantity: int"},
                new String[]{"+ getAvailableQuantity(): int", "+ getRequestedQuantity(): int"});

        // COLUMN 3: DISCOUNT STRATEGY (Polymorphism)
        drawPackageBox(g, 1060, 110, 480, 580, "com.flashdeal.discount (Strategy Pattern)", new Color(209, 250, 229));
        drawClassCard(g, 1080, 145, 440, 140, "DiscountStrategy", "interface",
                new String[]{"<<interface>>"},
                new String[]{"+ calculateDiscount(total, qty): BigDecimal", "+ isEligible(total, qty): boolean", "+ getCouponCode(): String"});

        drawClassCard(g, 1080, 305, 440, 150, "DiscountStrategyBase", "abstract class",
                new String[]{"# couponCode: String", "# minOrderAmount: BigDecimal", "# maxDiscountAmount: BigDecimal"},
                new String[]{"+ calculateDiscount(total, qty): BigDecimal", "#{abstract} computeRawDiscount(total, qty)"});

        drawClassCard(g, 1080, 475, 140, 190, "PercentageDiscount", "Strategy",
                new String[]{"- percentage: double"},
                new String[]{"# computeRawDiscount()", "SAVE10 (10% off)"});

        drawClassCard(g, 1230, 475, 140, 190, "FlatDiscount", "Strategy",
                new String[]{"- flatAmount: BigDecimal"},
                new String[]{"# computeRawDiscount()", "FLAT100 (\u20B9100 off)"});

        drawClassCard(g, 1380, 475, 140, 190, "BankOfferDiscount", "Strategy",
                new String[]{"- partnerBank: String", "- bankDiscount: double"},
                new String[]{"# computeRawDiscount()", "BANK15 (15% card off)"});

        // COLUMN 4: CONCURRENCY
        drawPackageBox(g, 1570, 110, 490, 580, "com.flashdeal.concurrency", new Color(254, 226, 226));
        drawClassCard(g, 1590, 145, 450, 170, "FlashSaleCoordinator", "Orchestrator",
                new String[]{"- flashSaleService: FlashSaleService", "- threadPool: ExecutorService", "- isSimulationRunning: AtomicBoolean"},
                new String[]{"+ startSimulation(prod, customers, qty, coupon, listener): void", "+ isRunning(): boolean"});

        drawClassCard(g, 1590, 335, 450, 170, "CustomerPurchaseTask", "Runnable",
                new String[]{"- customer: Customer", "- product: Product", "- quantity: int", "- startLatch: CountDownLatch"},
                new String[]{"+ run(): void (waits on startLatch barrier)"});

        drawClassCard(g, 1590, 525, 450, 140, "FlashSaleListener", "interface",
                new String[]{"<<callback observer>>"},
                new String[]{"+ onPurchaseSuccess(order, stock): void", "+ onPurchaseFailed(c, p, qty, reason): void", "+ onSimulationComplete(...): void"});

        // ROW 2: SERVICES & PERSISTENCE & UI
        drawPackageBox(g, 40, 720, 760, 580, "com.flashdeal.service", new Color(241, 245, 249));
        drawClassCard(g, 60, 755, 720, 190, "FlashSaleService", "Central Transaction Controller",
                new String[]{"- inventoryService: InventoryService", "- productService: ProductService", "- orderService: OrderService",
                        "- discountService: DiscountService", "- salesReportService: SalesReportService"},
                new String[]{"+ processPurchase(Customer, Product, qty, couponCode): Order [ATOMIC & SYNCHRONIZED]",
                        "+ setSaleListener(FlashSaleListener): void"});

        drawClassCard(g, 60, 965, 340, 160, "ProductService", "class",
                new String[]{"- productRepository: ProductRepository", "- inventoryService: InventoryService"},
                new String[]{"+ getAllProducts(): List<Product>", "+ updateStock(id, stock): void", "+ searchProducts(q, cat): List<Product>"});

        drawClassCard(g, 430, 965, 350, 160, "SalesReportService", "Analytics",
                new String[]{"- orderRepository: OrderRepository", "- productRepository: ProductRepository"},
                new String[]{"+ getTotalRevenue(): BigDecimal", "+ getProductMetrics(): Map<String, SaleMetric>", "+ exportReportToCsv(file): File"});

        drawClassCard(g, 60, 1145, 340, 130, "OrderService", "class",
                new String[]{"- orderRepository: OrderRepository"},
                new String[]{"+ recordOrder(order): void", "+ filterOrders(prod, status): List<Order>"});

        drawClassCard(g, 430, 1145, 350, 130, "DiscountService", "Registry",
                new String[]{"- couponRegistry: Map<String, DiscountStrategy>"},
                new String[]{"+ computeDiscount(code, amount, qty): BigDecimal", "+ getStrategy(code): DiscountStrategy"});

        // PERSISTENCE
        drawPackageBox(g, 830, 720, 600, 580, "com.flashdeal.persistence (CSV Storage)", new Color(254, 243, 199));
        drawClassCard(g, 850, 755, 560, 170, "FileStorageService", "File I/O Engine",
                new String[]{"- dataDirectory: Path (data/)", "- fileLock: Object"},
                new String[]{"+ readLines(fileName): List<String>", "+ writeLines(fileName, header, lines): void",
                        "+ appendLine(fileName, header, line): void", "+ escapeCsvField(str): String"});

        drawClassCard(g, 850, 945, 270, 180, "ProductRepository", "products.csv",
                new String[]{"- productMap: Map<String, Product>", "- productList: List<Product>"},
                new String[]{"+ getAllProducts(): List", "+ updateStock(id, stock): void", "+ saveOrUpdate(prod): void"});

        drawClassCard(g, 1140, 945, 270, 180, "OrderRepository", "orders.csv",
                new String[]{"- orderList: List<Order>", "- orderMap: Map<String, Order>"},
                new String[]{"+ save(order): void", "+ filterOrders(p, s): List (Iterator)", "+ calculateSuccessfulRevenue(): BigDecimal"});

        drawClassCard(g, 850, 1145, 560, 130, "CustomerRepository", "customers.csv",
                new String[]{"- customerList: List<Customer> (50 Realistic Profiles)"},
                new String[]{"+ getAllCustomers(): List<Customer>", "+ getBatch(count): List<Customer>"});

        // UI PACKAGE
        drawPackageBox(g, 1460, 720, 600, 580, "com.flashdeal.ui (Java Swing)", new Color(224, 231, 255));
        drawClassCard(g, 1480, 755, 560, 150, "MainFrame", "JFrame",
                new String[]{"- cardLayout: CardLayout", "- contentContainer: JPanel", "- navButtons: Map"},
                new String[]{"+ showView(viewKey): void", "+ initNavigationAndViews(): void"});

        drawClassCard(g, 1480, 925, 270, 170, "DashboardPanel", "JPanel",
                new String[]{"- cardProducts: MetricCard", "- cardRevenue: MetricCard"},
                new String[]{"+ refreshData(): void"});

        drawClassCard(g, 1770, 925, 270, 170, "FlashSalePanel", "JPanel",
                new String[]{"- coordinator: Coordinator", "- activityLogArea: JTextArea"},
                new String[]{"+ handleStartSimulation(): void"});

        drawClassCard(g, 1480, 1115, 270, 160, "ProductPanel", "JPanel",
                new String[]{"- productTable: CustomTable"},
                new String[]{"+ showAdjustStockDialog()"});

        drawClassCard(g, 1770, 1115, 270, 160, "ReportsPanel", "JPanel",
                new String[]{"- breakdownTable: CustomTable"},
                new String[]{"+ handleExportCsv(): void"});

        g.dispose();

        try {
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            ImageIO.write(image, "PNG", outputFile);
            System.out.println("[SUCCESS] UML class diagram rendered to: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to write UML diagram image: " + e.getMessage());
        }
    }

    private static void drawPackageBox(Graphics2D g, int x, int y, int w, int h, String title, Color bg) {
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 14, 14);
        g.setColor(new Color(203, 213, 225));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, w, h, 14, 14);

        g.setColor(new Color(30, 41, 59));
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g.drawString("PACKAGE: " + title, x + 14, y + 24);
    }

    private static void drawClassCard(Graphics2D g, int x, int y, int w, int h, String className,
                                      String stereotype, String[] fields, String[] methods) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(226, 232, 240));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x, y, w, h, 8, 8);

        // Header
        g.setColor(new Color(241, 245, 249));
        g.fillRoundRect(x, y, w, 32, 8, 8);
        g.fillRect(x, y + 20, w, 12);

        g.setColor(new Color(100, 116, 139));
        g.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        g.drawString("<<" + stereotype + ">>", x + 10, y + 14);

        g.setColor(new Color(15, 23, 42));
        g.setFont(new Font("Segoe UI", Font.BOLD, 13));
        g.drawString(className, x + 10, y + 28);

        // Separator
        g.setColor(new Color(226, 232, 240));
        g.drawLine(x, y + 33, x + w, y + 33);

        // Fields
        int cy = y + 48;
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        g.setColor(new Color(51, 65, 85));
        for (String f : fields) {
            if (cy > y + h - 10) break;
            g.drawString(f, x + 10, cy);
            cy += 14;
        }

        // Method separator
        if (methods != null && methods.length > 0) {
            cy += 4;
            g.setColor(new Color(226, 232, 240));
            g.drawLine(x, cy - 8, x + w, cy - 8);

            g.setColor(new Color(30, 41, 59));
            for (String m : methods) {
                if (cy > y + h - 10) break;
                g.drawString(m, x + 10, cy);
                cy += 14;
            }
        }
    }
}
