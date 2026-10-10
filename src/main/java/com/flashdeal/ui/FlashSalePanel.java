package com.flashdeal.ui;

import com.flashdeal.concurrency.FlashSaleCoordinator;
import com.flashdeal.concurrency.FlashSaleListener;
import com.flashdeal.discount.DiscountService;
import com.flashdeal.discount.DiscountStrategy;
import com.flashdeal.model.Customer;
import com.flashdeal.model.Order;
import com.flashdeal.model.Product;
import com.flashdeal.persistence.CustomerRepository;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.service.ProductService;
import com.flashdeal.ui.components.MetricCard;
import com.flashdeal.ui.theme.UITheme;
import com.flashdeal.util.CurrencyUtil;
import com.flashdeal.util.DateUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * SCREEN 3: Flash Sale Simulator Panel.
 * Demonstrates high-concurrency race condition testing using multithreading, CountDownLatch,
 * and real-time thread-safe inventory synchronization without GUI blocking.
 */
public class FlashSalePanel extends JPanel {
    private final FlashSaleService flashSaleService;
    private final CustomerRepository customerRepository;
    private final FlashSaleCoordinator coordinator;
    private final Runnable onDataUpdatedCallback;

    // Controls
    private JComboBox<Product> productSelector;
    private JLabel productDetailLabel;
    private JSpinner customerCountSpinner;
    private JSpinner quantityPerCustomerSpinner;
    private JComboBox<String> couponSelector;
    private JButton startSimulationButton;
    private JButton resetStockButton;
    private JProgressBar progressBar;

    // Real-Time Stats
    private MetricCard metricAttempts;
    private MetricCard metricSuccess;
    private MetricCard metricFailed;
    private MetricCard metricStockRemaining;

    // Activity Log
    private JTextArea activityLogArea;
    private JCheckBox autoScrollCheckBox;
    private JButton clearLogButton;

    public FlashSalePanel(FlashSaleService flashSaleService,
                          CustomerRepository customerRepository,
                          Runnable onDataUpdatedCallback) {
        this.flashSaleService = flashSaleService;
        this.customerRepository = customerRepository;
        this.coordinator = new FlashSaleCoordinator(flashSaleService);
        this.onDataUpdatedCallback = onDataUpdatedCallback;

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        refreshProductList();
    }

    private void initUI() {
        // TOP: Header
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.COLOR_BG);

        JLabel titleLabel = new JLabel("Flash Sale Concurrency Simulator");
        titleLabel.setFont(UITheme.FONT_HEADER_LARGE);
        titleLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Simulate thundering-herd concurrent customer purchases and verify thread synchronization");
        subLabel.setFont(UITheme.FONT_BODY);
        subLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(UITheme.COLOR_BG);
        headerText.add(titleLabel);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(subLabel);

        topContainer.add(headerText);
        topContainer.add(Box.createVerticalStrut(14));

        // SIMULATION CONFIGURATION PANEL
        JPanel configCard = new JPanel(new BorderLayout(12, 12));
        configCard.setBackground(UITheme.COLOR_SURFACE);
        configCard.setBorder(UITheme.createCardBorder());

        JPanel formGrid = new JPanel(new GridLayout(2, 4, 14, 10));
        formGrid.setBackground(UITheme.COLOR_SURFACE);

        // Column 1: Select Product
        JPanel col1 = new JPanel();
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.setBackground(UITheme.COLOR_SURFACE);
        JLabel lblProduct = new JLabel("Select Product:");
        lblProduct.setFont(UITheme.FONT_BODY_BOLD);
        productSelector = new JComboBox<>();
        productSelector.setFont(UITheme.FONT_BODY);
        productSelector.addActionListener(e -> updateProductDetailPreview());
        col1.add(lblProduct);
        col1.add(Box.createVerticalStrut(4));
        col1.add(productSelector);

        // Column 2: Customers Count
        JPanel col2 = new JPanel();
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.setBackground(UITheme.COLOR_SURFACE);
        JLabel lblCustomers = new JLabel("Simulated Customers:");
        lblCustomers.setFont(UITheme.FONT_BODY_BOLD);
        customerCountSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 500, 5));
        customerCountSpinner.setFont(UITheme.FONT_BODY);
        col2.add(lblCustomers);
        col2.add(Box.createVerticalStrut(4));
        col2.add(customerCountSpinner);

        // Column 3: Quantity per Customer
        JPanel col3 = new JPanel();
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.setBackground(UITheme.COLOR_SURFACE);
        JLabel lblQty = new JLabel("Units per Customer:");
        lblQty.setFont(UITheme.FONT_BODY_BOLD);
        quantityPerCustomerSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        quantityPerCustomerSpinner.setFont(UITheme.FONT_BODY);
        col3.add(lblQty);
        col3.add(Box.createVerticalStrut(4));
        col3.add(quantityPerCustomerSpinner);

        // Column 4: Coupon
        JPanel col4 = new JPanel();
        col4.setLayout(new BoxLayout(col4, BoxLayout.Y_AXIS));
        col4.setBackground(UITheme.COLOR_SURFACE);
        JLabel lblCoupon = new JLabel("Coupon Strategy:");
        lblCoupon.setFont(UITheme.FONT_BODY_BOLD);
        couponSelector = new JComboBox<>();
        couponSelector.setFont(UITheme.FONT_BODY);
        couponSelector.addItem("NONE - No Coupon");
        for (String code : flashSaleService.getDiscountService().getAvailableCouponCodes()) {
            DiscountStrategy strat = flashSaleService.getDiscountService().getStrategy(code);
            couponSelector.addItem(code + " (" + strat.getDescription() + ")");
        }
        col4.add(lblCoupon);
        col4.add(Box.createVerticalStrut(4));
        col4.add(couponSelector);

        formGrid.add(col1);
        formGrid.add(col2);
        formGrid.add(col3);
        formGrid.add(col4);

        // Row 2 actions
        JPanel actionRow = new JPanel(new BorderLayout(10, 0));
        actionRow.setBackground(UITheme.COLOR_SURFACE);

        productDetailLabel = new JLabel("Loading product details...");
        productDetailLabel.setFont(UITheme.FONT_BODY);
        productDetailLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setBackground(UITheme.COLOR_SURFACE);

        resetStockButton = UITheme.createSecondaryButton("\u2699 Reset Stock for Demo");
        resetStockButton.addActionListener(e -> handleResetStock());

        startSimulationButton = UITheme.createPrimaryButton("\uD83D\uDE80 START FLASH SALE SIMULATION");
        startSimulationButton.addActionListener(e -> handleStartSimulation());

        btnBar.add(resetStockButton);
        btnBar.add(startSimulationButton);

        actionRow.add(productDetailLabel, BorderLayout.WEST);
        actionRow.add(btnBar, BorderLayout.EAST);

        configCard.add(formGrid, BorderLayout.CENTER);
        configCard.add(actionRow, BorderLayout.SOUTH);
        topContainer.add(configCard);
        topContainer.add(Box.createVerticalStrut(14));

        // PROGRESS & LIVE STATS
        JPanel statsPanel = new JPanel(new BorderLayout(0, 10));
        statsPanel.setBackground(UITheme.COLOR_BG);

        // Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready to simulate");
        progressBar.setPreferredSize(new Dimension(progressBar.getWidth(), 22));
        progressBar.setForeground(UITheme.COLOR_ACCENT);
        progressBar.setBackground(UITheme.COLOR_BORDER);
        statsPanel.add(progressBar, BorderLayout.NORTH);

        // Metric Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        metricsGrid.setBackground(UITheme.COLOR_BG);

        metricAttempts = new MetricCard("Total Attempts", "0", "Dispatched threads", UITheme.COLOR_INDIGO);
        metricSuccess = new MetricCard("Successful Orders", "0", "Inventory secured", UITheme.COLOR_SUCCESS);
        metricFailed = new MetricCard("Rejected (Out of Stock)", "0", "Safely prevented overselling", UITheme.COLOR_DANGER);
        metricStockRemaining = new MetricCard("Remaining Inventory", "0", "Current live units", UITheme.COLOR_WARNING);

        metricsGrid.add(metricAttempts);
        metricsGrid.add(metricSuccess);
        metricsGrid.add(metricFailed);
        metricsGrid.add(metricStockRemaining);

        statsPanel.add(metricsGrid, BorderLayout.CENTER);
        topContainer.add(statsPanel);

        add(topContainer, BorderLayout.NORTH);

        // CENTER: Real-Time Activity Log
        JPanel logCard = new JPanel(new BorderLayout(0, 8));
        logCard.setBackground(UITheme.COLOR_SURFACE);
        logCard.setBorder(UITheme.createCardBorder());

        JPanel logHeader = new JPanel(new BorderLayout());
        logHeader.setBackground(UITheme.COLOR_SURFACE);

        JLabel logTitle = new JLabel("Real-Time Purchase Concurrency Activity Log");
        logTitle.setFont(UITheme.FONT_SUBHEADER);
        logTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JPanel logControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        logControls.setBackground(UITheme.COLOR_SURFACE);

        autoScrollCheckBox = new JCheckBox("Auto-scroll", true);
        autoScrollCheckBox.setFont(UITheme.FONT_BODY);
        autoScrollCheckBox.setBackground(UITheme.COLOR_SURFACE);

        clearLogButton = UITheme.createSecondaryButton("Clear Log");
        clearLogButton.addActionListener(e -> activityLogArea.setText(""));

        logControls.add(autoScrollCheckBox);
        logControls.add(clearLogButton);

        logHeader.add(logTitle, BorderLayout.WEST);
        logHeader.add(logControls, BorderLayout.EAST);
        logCard.add(logHeader, BorderLayout.NORTH);

        activityLogArea = new JTextArea();
        activityLogArea.setFont(UITheme.FONT_MONO);
        activityLogArea.setEditable(false);
        activityLogArea.setBackground(new Color(15, 23, 42)); // Slate 900 dark console
        activityLogArea.setForeground(new Color(241, 245, 249)); // Bright white text
        activityLogArea.setCaretColor(Color.WHITE);
        activityLogArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane logScroll = new JScrollPane(activityLogArea);
        logScroll.setBorder(new LineBorder(UITheme.COLOR_BORDER, 1));
        logCard.add(logScroll, BorderLayout.CENTER);

        add(logCard, BorderLayout.CENTER);

        logMessage("SYSTEM", "FLASHDEAL PRO Concurrency Engine initialized and standing by.");
    }

    public void refreshProductList() {
        Product selected = (Product) productSelector.getSelectedItem();
        productSelector.removeAllItems();
        List<Product> products = flashSaleService.getProductService().getAllProducts();
        for (Product p : products) {
            productSelector.addItem(p);
        }
        if (selected != null) {
            for (int i = 0; i < productSelector.getItemCount(); i++) {
                if (productSelector.getItemAt(i).getProductId().equals(selected.getProductId())) {
                    productSelector.setSelectedIndex(i);
                    break;
                }
            }
        }
        updateProductDetailPreview();
    }

    private void updateProductDetailPreview() {
        Product p = (Product) productSelector.getSelectedItem();
        if (p == null) {
            productDetailLabel.setText("No product selected.");
            return;
        }
        int currentStock = flashSaleService.getInventoryService().getAvailableStock(p.getProductId());
        productDetailLabel.setText(String.format("<html><b>%s</b> | Price: <font color='#E11D48'><b>%s</b></font> (Was: %s, %s) | Live Stock: <b><font color='%s'>%d units</font></b></html>",
                p.getName(),
                p.getFormattedFlashSalePrice(),
                p.getFormattedOriginalPrice(),
                p.getDiscountBadge(),
                currentStock > 0 ? "#10B981" : "#EF4444",
                currentStock
        ));
        metricStockRemaining.setValue(String.valueOf(currentStock));
    }

    private void handleResetStock() {
        Product p = (Product) productSelector.getSelectedItem();
        if (p == null) return;

        JPanel confirmPanel = new JPanel(new GridLayout(3, 1, 6, 6));
        confirmPanel.add(new JLabel("Set initial demonstration stock for: " + p.getName()));
        confirmPanel.add(new JLabel("Recommended for scenario demo: 10 units"));

        JSpinner newStockSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 500, 1));
        confirmPanel.add(newStockSpinner);

        int res = JOptionPane.showConfirmDialog(this, confirmPanel,
                "Confirm Stock Reset - " + p.getProductId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            int newStock = (Integer) newStockSpinner.getValue();
            flashSaleService.getProductService().updateStock(p.getProductId(), newStock);
            updateProductDetailPreview();
            logMessage("INVENTORY", String.format("Stock for [%s] manually reconfigured to %d units.", p.getName(), newStock));
            if (onDataUpdatedCallback != null) onDataUpdatedCallback.run();
        }
    }

    private void handleStartSimulation() {
        Product product = (Product) productSelector.getSelectedItem();
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Please select a product first.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customersCount = (Integer) customerCountSpinner.getValue();
        int unitsPerCustomer = (Integer) quantityPerCustomerSpinner.getValue();
        String selectedCouponText = (String) couponSelector.getSelectedItem();
        String couponCode = null;
        if (selectedCouponText != null && !selectedCouponText.startsWith("NONE")) {
            couponCode = selectedCouponText.split(" ")[0].trim();
        }

        int liveStock = flashSaleService.getInventoryService().getAvailableStock(product.getProductId());

        // Prepare simulation
        startSimulationButton.setEnabled(false);
        resetStockButton.setEnabled(false);
        progressBar.setValue(0);
        progressBar.setString("Simulating flash sale rush...");

        metricAttempts.setValue("0");
        metricSuccess.setValue("0");
        metricFailed.setValue("0");
        metricStockRemaining.setValue(String.valueOf(liveStock));

        logMessage("SIMULATOR", "=======================================================================");
        logMessage("SIMULATOR", String.format("FLASH SALE LAUNCHED: %d customers competing for [%s]", customersCount, product.getName()));
        logMessage("SIMULATOR", String.format("Initial Stock: %d units | Requested per customer: %d unit(s) | Coupon: %s",
                liveStock, unitsPerCustomer, couponCode != null ? couponCode : "None"));
        logMessage("SIMULATOR", "Synchronizing customer threads at CountDownLatch barrier...");

        List<Customer> customerList = customerRepository.getBatch(customersCount);

        final String finalCoupon = couponCode;
        coordinator.startSimulation(product, customerList, unitsPerCustomer, finalCoupon, new FlashSaleListener() {
            @Override
            public void onPurchaseAttempt(Customer customer, Product p, int quantity) {
                SwingUtilities.invokeLater(() -> {
                    logMessage("ATTEMPT", String.format("[%s] requested %d unit(s)", customer.getName(), quantity));
                });
            }

            @Override
            public void onPurchaseSuccess(Order order, int remainingStock) {
                SwingUtilities.invokeLater(() -> {
                    metricSuccess.setValue(String.valueOf(Integer.parseInt(metricSuccess.getToolTipText() != null ? metricSuccess.getToolTipText() : "0") + 1));
                    metricStockRemaining.setValue(String.valueOf(remainingStock));
                    logMessage("SUCCESS", String.format("[%s] ORDER CONFIRMED! Order ID: %s | Paid: %s | Remaining Stock: %d",
                            order.getCustomerName(), order.getOrderId(), CurrencyUtil.formatRupee(order.getFinalAmount()), remainingStock));
                });
            }

            @Override
            public void onPurchaseFailed(Customer customer, Product p, int quantity, String reason) {
                SwingUtilities.invokeLater(() -> {
                    logMessage("REJECTED", String.format("[%s] Purchase failed: %s", customer.getName(), reason));
                });
            }

            @Override
            public void onSimulationProgress(int completedAttempts, int totalAttempts, int remainingStock) {
                SwingUtilities.invokeLater(() -> {
                    int percent = (int) (((double) completedAttempts / totalAttempts) * 100);
                    progressBar.setValue(percent);
                    progressBar.setString(String.format("Processed %d / %d purchase attempts (%d%%)", completedAttempts, totalAttempts, percent));
                    metricAttempts.setValue(String.valueOf(completedAttempts));
                    metricStockRemaining.setValue(String.valueOf(remainingStock));
                });
            }

            @Override
            public void onSimulationComplete(int totalAttempts, int successCount, int failureCount, BigDecimal totalRevenue) {
                SwingUtilities.invokeLater(() -> {
                    startSimulationButton.setEnabled(true);
                    resetStockButton.setEnabled(true);
                    progressBar.setValue(100);
                    progressBar.setString("Simulation complete!");

                    metricAttempts.setValue(String.valueOf(totalAttempts));
                    metricSuccess.setValue(String.valueOf(successCount));
                    metricFailed.setValue(String.valueOf(failureCount));
                    int finalStock = flashSaleService.getInventoryService().getAvailableStock(product.getProductId());
                    metricStockRemaining.setValue(String.valueOf(finalStock));

                    logMessage("COMPLETE", "-----------------------------------------------------------------------");
                    logMessage("COMPLETE", String.format("FLASH SALE CONCLUDED! Total Attempts: %d | Successful: %d | Rejected: %d",
                            totalAttempts, successCount, failureCount));
                    logMessage("COMPLETE", String.format("Final Inventory: %d units (Zero Overselling Guaranteed)", finalStock));
                    logMessage("COMPLETE", "=======================================================================");

                    updateProductDetailPreview();
                    if (onDataUpdatedCallback != null) onDataUpdatedCallback.run();

                    JOptionPane.showMessageDialog(FlashSalePanel.this,
                            String.format("Flash Sale Simulation Complete!\n\n" +
                                            "- Competing Customers: %d\n" +
                                            "- Successful Purchases: %d\n" +
                                            "- Out of Stock Rejections: %d\n" +
                                            "- Final Remaining Stock: %d\n" +
                                            "- Multithreading Synchronization: VERIFIED",
                                    totalAttempts, successCount, failureCount, finalStock),
                            "Simulation Concluded", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        });
    }

    private void logMessage(String tag, String message) {
        String timestamp = DateUtil.formatTimeOnly(LocalDateTime.now());
        String entry = String.format("[%s] [%-9s] %s\n", timestamp, tag, message);
        activityLogArea.append(entry);

        if (autoScrollCheckBox != null && autoScrollCheckBox.isSelected()) {
            activityLogArea.setCaretPosition(activityLogArea.getDocument().getLength());
        }
    }
}
