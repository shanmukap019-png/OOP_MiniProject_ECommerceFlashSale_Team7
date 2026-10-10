package com.flashdeal.ui;

import com.flashdeal.model.Order;
import com.flashdeal.model.OrderStatus;
import com.flashdeal.model.Product;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.service.OrderService;
import com.flashdeal.ui.components.BadgeRenderer;
import com.flashdeal.ui.components.CustomTable;
import com.flashdeal.ui.theme.UITheme;
import com.flashdeal.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * SCREEN 4: Order History Panel displaying all transaction attempts,
 * filtering by product/status, customer search, and revenue summaries.
 */
public class OrderHistoryPanel extends JPanel {
    private final FlashSaleService flashSaleService;
    private final OrderService orderService;

    private JComboBox<String> productFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JTextField customerSearchField;
    private JLabel summaryLabel;

    private DefaultTableModel ordersTableModel;
    private CustomTable ordersTable;

    public OrderHistoryPanel(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
        this.orderService = flashSaleService.getOrderService();

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        refreshOrders();
    }

    private void initUI() {
        // TOP: Header & Filters
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBackground(UITheme.COLOR_BG);

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_BG);

        JLabel titleLabel = new JLabel("Flash Sale Order History & Audit Trail");
        titleLabel.setFont(UITheme.FONT_HEADER_LARGE);
        titleLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Audit all successful purchases and blocked overselling attempts across flash sales");
        subLabel.setFont(UITheme.FONT_BODY);
        subLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        JPanel titleText = new JPanel();
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.setBackground(UITheme.COLOR_BG);
        titleText.add(titleLabel);
        titleText.add(Box.createVerticalStrut(4));
        titleText.add(subLabel);

        JButton refreshBtn = UITheme.createSecondaryButton("\u21BB Refresh Orders");
        refreshBtn.addActionListener(e -> refreshOrders());

        headerPanel.add(titleText, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        topContainer.add(headerPanel);
        topContainer.add(Box.createVerticalStrut(14));

        // FILTER BAR
        JPanel filterCard = new JPanel(new BorderLayout(10, 8));
        filterCard.setBackground(UITheme.COLOR_SURFACE);
        filterCard.setBorder(UITheme.createCardBorder());

        JPanel filterInputs = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        filterInputs.setBackground(UITheme.COLOR_SURFACE);

        // Product Filter
        JLabel lblProd = new JLabel("Product:");
        lblProd.setFont(UITheme.FONT_BODY_BOLD);
        productFilterCombo = new JComboBox<>(new String[]{"ALL"});
        productFilterCombo.setFont(UITheme.FONT_BODY);
        productFilterCombo.addActionListener(e -> applyFilters());

        // Status Filter
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(UITheme.FONT_BODY_BOLD);
        statusFilterCombo = new JComboBox<>(new String[]{"ALL", "SUCCESS", "OUT_OF_STOCK", "FAILED"});
        statusFilterCombo.setFont(UITheme.FONT_BODY);
        statusFilterCombo.addActionListener(e -> applyFilters());

        // Search Field
        JLabel lblSearch = new JLabel("\uD83D\uDD0D Search:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);
        customerSearchField = new JTextField(14);
        customerSearchField.setFont(UITheme.FONT_BODY);
        customerSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            productFilterCombo.setSelectedIndex(0);
            statusFilterCombo.setSelectedIndex(0);
            customerSearchField.setText("");
            applyFilters();
        });

        filterInputs.add(lblProd);
        filterInputs.add(productFilterCombo);
        filterInputs.add(lblStatus);
        filterInputs.add(statusFilterCombo);
        filterInputs.add(lblSearch);
        filterInputs.add(customerSearchField);
        filterInputs.add(resetBtn);

        summaryLabel = new JLabel("Showing 0 orders");
        summaryLabel.setFont(UITheme.FONT_BODY_BOLD);
        summaryLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        filterCard.add(filterInputs, BorderLayout.WEST);
        filterCard.add(summaryLabel, BorderLayout.EAST);
        topContainer.add(filterCard);

        add(topContainer, BorderLayout.NORTH);

        // CENTER: Orders Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UITheme.COLOR_SURFACE);
        tablePanel.setBorder(UITheme.createCardBorder());

        String[] cols = {
                "Order ID", "Customer", "Product", "Qty", "Original Amt",
                "Discount", "Final Paid", "Timestamp", "Status", "Notes / Reason"
        };

        ordersTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ordersTable = new CustomTable(ordersTableModel);
        ordersTable.getColumnModel().getColumn(8).setCellRenderer(new BadgeRenderer());
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(130);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(210);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(45);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(95);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(140);
        ordersTable.getColumnModel().getColumn(8).setPreferredWidth(110);
        ordersTable.getColumnModel().getColumn(9).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    public void refreshOrders() {
        // Refresh product combo options
        String currentProd = (String) productFilterCombo.getSelectedItem();
        productFilterCombo.removeAllItems();
        productFilterCombo.addItem("ALL");
        for (Product p : flashSaleService.getProductService().getAllProducts()) {
            productFilterCombo.addItem(p.getProductId() + " - " + p.getName());
        }
        if (currentProd != null) {
            productFilterCombo.setSelectedItem(currentProd);
        }

        applyFilters();
    }

    private void applyFilters() {
        String selectedProd = (String) productFilterCombo.getSelectedItem();
        String prodId = null;
        if (selectedProd != null && !selectedProd.equals("ALL")) {
            prodId = selectedProd.split(" - ")[0].trim();
        }

        String selectedStatusStr = (String) statusFilterCombo.getSelectedItem();
        OrderStatus statusFilter = null;
        if (selectedStatusStr != null && !selectedStatusStr.equals("ALL")) {
            statusFilter = OrderStatus.fromString(selectedStatusStr);
        }

        String query = customerSearchField.getText().trim().toLowerCase();

        // Query filtered orders using OrderRepository (which uses Iterator traversal!)
        List<Order> filtered = orderService.filterOrders(prodId, statusFilter);
        ordersTableModel.setRowCount(0);

        int displayedCount = 0;
        BigDecimal displayedRevenue = BigDecimal.ZERO;

        for (Order o : filtered) {
            boolean matchesSearch = query.isEmpty() ||
                    o.getCustomerName().toLowerCase().contains(query) ||
                    o.getOrderId().toLowerCase().contains(query) ||
                    o.getProductName().toLowerCase().contains(query);

            if (matchesSearch) {
                displayedCount++;
                if (o.getStatus() == OrderStatus.SUCCESS) {
                    displayedRevenue = displayedRevenue.add(o.getFinalAmount());
                }
                ordersTableModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getCustomerName(),
                        o.getProductName(),
                        o.getQuantity(),
                        CurrencyUtil.formatRupee(o.getOriginalAmount()),
                        CurrencyUtil.formatRupee(o.getDiscountAmount()),
                        CurrencyUtil.formatRupee(o.getFinalAmount()),
                        o.getFormattedTimestamp(),
                        o.getStatus(),
                        o.getFailureReason().isEmpty() ? "Fulfilled" : o.getFailureReason()
                });
            }
        }

        summaryLabel.setText(String.format("Showing %d orders | Filtered Revenue: %s",
                displayedCount, CurrencyUtil.formatRupee(displayedRevenue)));
    }
}
