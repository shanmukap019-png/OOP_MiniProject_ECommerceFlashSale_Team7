package com.flashdeal.ui;

import com.flashdeal.model.Product;
import com.flashdeal.model.SaleMetric;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.service.SalesReportService;
import com.flashdeal.ui.components.CustomTable;
import com.flashdeal.ui.components.MetricCard;
import com.flashdeal.ui.theme.UITheme;
import com.flashdeal.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * SCREEN 5: Sales Report & Analytics Panel.
 * Provides deep performance metrics, product-wise breakdown, and CSV report export.
 */
public class ReportsPanel extends JPanel {
    private final FlashSaleService flashSaleService;
    private final SalesReportService salesReportService;

    private MetricCard cardRevenue;
    private MetricCard cardUnitsSold;
    private MetricCard cardDiscounts;
    private MetricCard cardSuccessRate;
    private JLabel popularProductLabel;

    private DefaultTableModel breakdownModel;
    private CustomTable breakdownTable;

    public ReportsPanel(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
        this.salesReportService = flashSaleService.getSalesReportService();

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        refreshReport();
    }

    private void initUI() {
        // TOP: Header & Action Bar
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.COLOR_BG);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_BG);

        JLabel titleLabel = new JLabel("Flash Sale Performance & Sales Analytics");
        titleLabel.setFont(UITheme.FONT_HEADER_LARGE);
        titleLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Audit revenue generated, units sold, discount absorption, and export CSV reports");
        subLabel.setFont(UITheme.FONT_BODY);
        subLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        JPanel titleText = new JPanel();
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.setBackground(UITheme.COLOR_BG);
        titleText.add(titleLabel);
        titleText.add(Box.createVerticalStrut(4));
        titleText.add(subLabel);

        // Action Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setBackground(UITheme.COLOR_BG);

        JButton refreshBtn = UITheme.createSecondaryButton("\u21BB Refresh Analytics");
        refreshBtn.addActionListener(e -> refreshReport());

        JButton exportCsvBtn = UITheme.createPrimaryButton("\uD83D\uDCE5 Export Sales Report (CSV)");
        exportCsvBtn.addActionListener(e -> handleExportCsv());

        btnBar.add(refreshBtn);
        btnBar.add(exportCsvBtn);

        headerPanel.add(titleText, BorderLayout.WEST);
        headerPanel.add(btnBar, BorderLayout.EAST);
        topContainer.add(headerPanel);
        topContainer.add(Box.createVerticalStrut(14));

        // Metric Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        metricsGrid.setBackground(UITheme.COLOR_BG);

        cardRevenue = new MetricCard("Total Revenue", "\u20B9 0.00", "Successful orders only", UITheme.COLOR_ACCENT);
        cardUnitsSold = new MetricCard("Total Units Sold", "0", "Fulfilled inventory items", UITheme.COLOR_SUCCESS);
        cardDiscounts = new MetricCard("Total Discounts", "\u20B9 0.00", "Coupons & flash offers absorbed", UITheme.COLOR_INDIGO);
        cardSuccessRate = new MetricCard("Overall Success Rate", "0.0%", "Fulfilled vs total attempts", UITheme.COLOR_WARNING);

        metricsGrid.add(cardRevenue);
        metricsGrid.add(cardUnitsSold);
        metricsGrid.add(cardDiscounts);
        metricsGrid.add(cardSuccessRate);
        topContainer.add(metricsGrid);
        topContainer.add(Box.createVerticalStrut(14));

        // Highlight bar: Most popular product
        JPanel highlightCard = new JPanel(new BorderLayout());
        highlightCard.setBackground(UITheme.COLOR_SURFACE);
        highlightCard.setBorder(UITheme.createCardBorder());

        popularProductLabel = new JLabel("\uD83C\uDFC6 Most Popular Product: Calculating...");
        popularProductLabel.setFont(UITheme.FONT_SUBHEADER);
        popularProductLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        highlightCard.add(popularProductLabel, BorderLayout.CENTER);

        topContainer.add(highlightCard);
        add(topContainer, BorderLayout.NORTH);

        // CENTER: Product-wise Breakdown Table
        JPanel tableCard = new JPanel(new BorderLayout(0, 10));
        tableCard.setBackground(UITheme.COLOR_SURFACE);
        tableCard.setBorder(UITheme.createCardBorder());

        JLabel tableTitle = new JLabel("Product-Wise Performance & Inventory Breakdown");
        tableTitle.setFont(UITheme.FONT_SUBHEADER);
        tableTitle.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {
                "Product ID", "Product Name", "Total Attempts", "Successful",
                "Rejected / OOS", "Units Sold", "Remaining Stock", "Revenue", "Discounts", "Success Rate"
        };

        breakdownModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        breakdownTable = new CustomTable(breakdownModel);
        breakdownTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        breakdownTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        breakdownTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        breakdownTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        breakdownTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        breakdownTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        breakdownTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        breakdownTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        breakdownTable.getColumnModel().getColumn(8).setPreferredWidth(95);
        breakdownTable.getColumnModel().getColumn(9).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(breakdownTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshReport() {
        cardRevenue.setValue(CurrencyUtil.formatRupee(salesReportService.getTotalRevenue()));
        cardUnitsSold.setValue(String.valueOf(salesReportService.getTotalUnitsSold()));
        cardDiscounts.setValue(CurrencyUtil.formatRupee(salesReportService.getTotalDiscountsGranted()));
        cardSuccessRate.setValue(String.format("%.1f%%", salesReportService.getOverallSuccessRate()));

        Product popular = salesReportService.getMostPopularProduct();
        if (popular != null) {
            popularProductLabel.setText(String.format("\uD83C\uDFC6 Most Popular Product: %s (%s) - Flash Price: %s",
                    popular.getName(), popular.getProductId(), popular.getFormattedFlashSalePrice()));
        } else {
            popularProductLabel.setText("\uD83C\uDFC6 Most Popular Product: No sales recorded yet.");
        }

        breakdownModel.setRowCount(0);
        Map<String, SaleMetric> metrics = salesReportService.getProductMetrics();
        for (SaleMetric m : metrics.values()) {
            int remainingStock = flashSaleService.getInventoryService().getAvailableStock(m.getProductId());
            breakdownModel.addRow(new Object[]{
                    m.getProductId(),
                    m.getProductName(),
                    m.getTotalAttempts(),
                    m.getSuccessfulOrders(),
                    m.getFailedOrders(),
                    m.getUnitsSold(),
                    remainingStock + " units",
                    m.getFormattedRevenue(),
                    m.getFormattedDiscounts(),
                    String.format("%.1f%%", m.getSuccessRate())
            });
        }
    }

    private void handleExportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Sales Report to CSV");
        fileChooser.setSelectedFile(new File("data/sales_report_" + System.currentTimeMillis() + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                File exported = salesReportService.exportReportToCsv(fileToSave);
                JOptionPane.showMessageDialog(this,
                        "Sales report exported successfully to:\n" + exported.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export CSV report: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
