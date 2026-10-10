package com.flashdeal.ui;

import com.flashdeal.model.Order;
import com.flashdeal.model.Product;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.service.ProductService;
import com.flashdeal.service.SalesReportService;
import com.flashdeal.ui.components.BadgeRenderer;
import com.flashdeal.ui.components.CustomTable;
import com.flashdeal.ui.components.MetricCard;
import com.flashdeal.ui.theme.UITheme;
import com.flashdeal.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * SCREEN 1: Main Dashboard Panel displaying high-level KPIs, flash sale status,
 * and recent real-time activity.
 */
public class DashboardPanel extends JPanel {
    private final FlashSaleService flashSaleService;
    private final Runnable navigateToSimulatorAction;

    private MetricCard cardProducts;
    private MetricCard cardStock;
    private MetricCard cardSuccess;
    private MetricCard cardFailed;
    private MetricCard cardRevenue;
    private MetricCard cardSuccessRate;

    private JLabel statusBannerTitle;
    private JLabel statusBannerDetails;
    private DefaultTableModel recentOrdersModel;

    public DashboardPanel(FlashSaleService flashSaleService, Runnable navigateToSimulatorAction) {
        this.flashSaleService = flashSaleService;
        this.navigateToSimulatorAction = navigateToSimulatorAction;

        setLayout(new BorderLayout(20, 20));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        refreshData();
    }

    private void initUI() {
        // TOP: Header & Status Banner
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.COLOR_BG);

        // Header Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UITheme.COLOR_BG);
        JLabel pageTitle = new JLabel("Flash Sale Executive Dashboard");
        pageTitle.setFont(UITheme.FONT_HEADER_LARGE);
        pageTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel pageSub = new JLabel("Real-time monitoring of concurrent inventory, revenue, and flash sale metrics");
        pageSub.setFont(UITheme.FONT_BODY);
        pageSub.setForeground(UITheme.COLOR_TEXT_MUTED);

        JPanel titleText = new JPanel();
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.setBackground(UITheme.COLOR_BG);
        titleText.add(pageTitle);
        titleText.add(Box.createVerticalStrut(4));
        titleText.add(pageSub);

        JButton launchBtn = UITheme.createPrimaryButton("Launch Flash Sale Simulator \u2192");
        launchBtn.addActionListener(e -> {
            if (navigateToSimulatorAction != null) {
                navigateToSimulatorAction.run();
            }
        });

        titlePanel.add(titleText, BorderLayout.WEST);
        titlePanel.add(launchBtn, BorderLayout.EAST);
        topContainer.add(titlePanel);
        topContainer.add(Box.createVerticalStrut(16));

        // Big Sale Day Event Banner
        JPanel bannerPanel = new JPanel(new BorderLayout(15, 10));
        bannerPanel.setBackground(UITheme.COLOR_PRIMARY);
        bannerPanel.setBorder(new CompoundBorder(new LineBorder(UITheme.COLOR_PRIMARY_DARK, 1, true), new EmptyBorder(16, 20, 16, 20)));

        JPanel bannerText = new JPanel();
        bannerText.setLayout(new BoxLayout(bannerText, BoxLayout.Y_AXIS));
        bannerText.setBackground(UITheme.COLOR_PRIMARY);

        statusBannerTitle = new JLabel("\u26A1 BIG SALE DAY - ACTIVE FLASH SALE CAMPAIGN");
        statusBannerTitle.setFont(UITheme.FONT_HEADER_MEDIUM);
        statusBannerTitle.setForeground(Color.WHITE);

        statusBannerDetails = new JLabel("High-concurrency thread-safe simulator ready. Zero inventory leakage guaranteed.");
        statusBannerDetails.setFont(UITheme.FONT_BODY);
        statusBannerDetails.setForeground(UITheme.COLOR_TEXT_LIGHT);

        bannerText.add(statusBannerTitle);
        bannerText.add(Box.createVerticalStrut(4));
        bannerText.add(statusBannerDetails);

        bannerPanel.add(bannerText, BorderLayout.CENTER);
        topContainer.add(bannerPanel);
        topContainer.add(Box.createVerticalStrut(16));

        // KPI Metric Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 14, 14));
        metricsGrid.setBackground(UITheme.COLOR_BG);

        cardProducts = new MetricCard("Total Products", "8", "Active catalog items", UITheme.COLOR_INDIGO);
        cardStock = new MetricCard("Total Units in Stock", "0", "Aggregated live inventory", UITheme.COLOR_SUCCESS);
        cardSuccess = new MetricCard("Successful Orders", "0", "Fulfilled transactions", UITheme.COLOR_SUCCESS);
        cardFailed = new MetricCard("Failed / Out of Stock", "0", "Blocked race conditions", UITheme.COLOR_DANGER);
        cardRevenue = new MetricCard("Total Revenue", "\u20B9 0.00", "Successful sales revenue", UITheme.COLOR_ACCENT);
        cardSuccessRate = new MetricCard("Success Rate", "0.0%", "Fulfilled vs total attempts", UITheme.COLOR_WARNING);

        metricsGrid.add(cardProducts);
        metricsGrid.add(cardStock);
        metricsGrid.add(cardRevenue);
        metricsGrid.add(cardSuccess);
        metricsGrid.add(cardFailed);
        metricsGrid.add(cardSuccessRate);

        topContainer.add(metricsGrid);
        add(topContainer, BorderLayout.NORTH);

        // CENTER: Recent Activity Table
        JPanel recentPanel = new JPanel(new BorderLayout(0, 10));
        recentPanel.setBackground(UITheme.COLOR_SURFACE);
        recentPanel.setBorder(UITheme.createCardBorder());

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setBackground(UITheme.COLOR_SURFACE);

        JLabel recentTitle = new JLabel("Recent Purchase Transactions");
        recentTitle.setFont(UITheme.FONT_SUBHEADER);
        recentTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JButton refreshBtn = UITheme.createSecondaryButton("\u21BB Refresh");
        refreshBtn.addActionListener(e -> refreshData());

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(refreshBtn, BorderLayout.EAST);
        recentPanel.add(recentHeader, BorderLayout.NORTH);

        String[] cols = {"Order ID", "Customer", "Product", "Qty", "Final Amount", "Timestamp", "Status"};
        recentOrdersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        CustomTable table = new CustomTable(recentOrdersModel);
        table.getColumnModel().getColumn(6).setCellRenderer(new BadgeRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        add(recentPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        ProductService ps = flashSaleService.getProductService();
        SalesReportService rs = flashSaleService.getSalesReportService();

        List<Product> products = ps.getAllProducts();
        int totalStock = 0;
        for (Product p : products) {
            totalStock += p.getAvailableStock();
        }

        int successOrders = rs.getSuccessfulPurchasesCount();
        int failedOrders = rs.getFailedPurchasesCount();
        double successRate = rs.getOverallSuccessRate();

        cardProducts.setValue(String.valueOf(products.size()));
        cardStock.setValue(String.valueOf(totalStock));
        cardSuccess.setValue(String.valueOf(successOrders));
        cardFailed.setValue(String.valueOf(failedOrders));
        cardRevenue.setValue(rs.getTotalRevenue() != null ? CurrencyUtil.formatRupee(rs.getTotalRevenue()) : "\u20B9 0.00");
        cardSuccessRate.setValue(String.format("%.1f%%", successRate));

        // Update Recent Orders table
        recentOrdersModel.setRowCount(0);
        List<Order> orders = flashSaleService.getOrderService().getAllOrders();
        int limit = Math.min(orders.size(), 12);
        for (int i = 0; i < limit; i++) {
            Order o = orders.get(i);
            recentOrdersModel.addRow(new Object[]{
                    o.getOrderId(),
                    o.getCustomerName(),
                    o.getProductName(),
                    o.getQuantity(),
                    CurrencyUtil.formatRupee(o.getFinalAmount()),
                    o.getFormattedTimestamp(),
                    o.getStatus()
            });
        }
    }
}
