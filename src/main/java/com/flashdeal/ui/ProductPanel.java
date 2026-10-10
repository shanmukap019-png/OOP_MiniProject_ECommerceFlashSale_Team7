package com.flashdeal.ui;

import com.flashdeal.model.Product;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.service.ProductService;
import com.flashdeal.ui.components.BadgeRenderer;
import com.flashdeal.ui.components.CustomTable;
import com.flashdeal.ui.theme.UITheme;
import com.flashdeal.util.CurrencyUtil;
import com.flashdeal.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SCREEN 2: Product Catalog Panel showing all flash sale products,
 * category filtering, instant search, and stock adjustment dialogs.
 */
public class ProductPanel extends JPanel {
    private final FlashSaleService flashSaleService;
    private final ProductService productService;

    private JTextField searchField;
    private JComboBox<String> categoryFilterCombo;
    private DefaultTableModel tableModel;
    private CustomTable productTable;
    private List<Product> displayedProducts;

    public ProductPanel(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
        this.productService = flashSaleService.getProductService();
        this.displayedProducts = new ArrayList<>();

        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.COLOR_BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        refreshProducts();
    }

    private void initUI() {
        // TOP: Header & Action Bar
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(UITheme.COLOR_BG);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_BG);

        JLabel titleLabel = new JLabel("Flash Sale Product Catalog");
        titleLabel.setFont(UITheme.FONT_HEADER_LARGE);
        titleLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        JLabel subLabel = new JLabel("Manage products, adjust flash sale stock levels, and monitor live inventory statuses");
        subLabel.setFont(UITheme.FONT_BODY);
        subLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        JPanel titleText = new JPanel();
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.setBackground(UITheme.COLOR_BG);
        titleText.add(titleLabel);
        titleText.add(Box.createVerticalStrut(4));
        titleText.add(subLabel);

        // Action Buttons
        JPanel actionBtnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtnGroup.setBackground(UITheme.COLOR_BG);

        JButton updateStockBtn = UITheme.createSecondaryButton("\u2699 Adjust Stock");
        updateStockBtn.addActionListener(e -> showAdjustStockDialog());

        JButton addProductBtn = UITheme.createPrimaryButton("+ Add New Product");
        addProductBtn.addActionListener(e -> showAddProductDialog());

        actionBtnGroup.add(updateStockBtn);
        actionBtnGroup.add(addProductBtn);

        headerPanel.add(titleText, BorderLayout.WEST);
        headerPanel.add(actionBtnGroup, BorderLayout.EAST);
        topPanel.add(headerPanel);
        topPanel.add(Box.createVerticalStrut(16));

        // FILTER BAR
        JPanel filterBar = new JPanel(new BorderLayout(12, 0));
        filterBar.setBackground(UITheme.COLOR_SURFACE);
        filterBar.setBorder(UITheme.createCardBorder());

        // Search Input
        JPanel searchContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchContainer.setBackground(UITheme.COLOR_SURFACE);

        JLabel searchLabel = new JLabel("\uD83D\uDD0D Search:");
        searchLabel.setFont(UITheme.FONT_BODY_BOLD);
        searchField = new JTextField(20);
        searchField.setFont(UITheme.FONT_BODY);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });

        searchContainer.add(searchLabel);
        searchContainer.add(searchField);

        // Category Combo
        JPanel catContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        catContainer.setBackground(UITheme.COLOR_SURFACE);

        JLabel catLabel = new JLabel("Category:");
        catLabel.setFont(UITheme.FONT_BODY_BOLD);
        categoryFilterCombo = new JComboBox<>(new String[]{"ALL"});
        categoryFilterCombo.setFont(UITheme.FONT_BODY);
        categoryFilterCombo.addActionListener(e -> filterData());

        JButton resetFilters = UITheme.createSecondaryButton("Reset Filters");
        resetFilters.addActionListener(e -> {
            searchField.setText("");
            categoryFilterCombo.setSelectedIndex(0);
            refreshProducts();
        });

        catContainer.add(catLabel);
        catContainer.add(categoryFilterCombo);
        catContainer.add(resetFilters);

        filterBar.add(searchContainer, BorderLayout.WEST);
        filterBar.add(catContainer, BorderLayout.EAST);
        topPanel.add(filterBar);

        add(topPanel, BorderLayout.NORTH);

        // CENTER: Products Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UITheme.COLOR_SURFACE);
        tablePanel.setBorder(UITheme.createCardBorder());

        String[] cols = {
                "Product ID", "Product Name", "Category", "Original Price",
                "Flash Sale Price", "Discount", "Available Stock", "Status"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new CustomTable(tableModel);
        productTable.getColumnModel().getColumn(7).setCellRenderer(new BadgeRenderer());
        productTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(7).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    public void refreshProducts() {
        List<Product> all = productService.getAllProducts();
        displayedProducts = all;

        // Update categories
        Set<String> categories = new HashSet<>();
        categories.add("ALL");
        for (Product p : all) {
            if (p.getCategory() != null) categories.add(p.getCategory());
        }

        String currentSelect = (String) categoryFilterCombo.getSelectedItem();
        categoryFilterCombo.removeAllItems();
        for (String cat : categories) {
            categoryFilterCombo.addItem(cat);
        }
        if (currentSelect != null && categories.contains(currentSelect)) {
            categoryFilterCombo.setSelectedItem(currentSelect);
        }

        filterData();
    }

    private void filterData() {
        String query = searchField.getText();
        String selectedCategory = (String) categoryFilterCombo.getSelectedItem();
        if (selectedCategory == null) selectedCategory = "ALL";

        displayedProducts = productService.searchProducts(query, selectedCategory);
        tableModel.setRowCount(0);

        for (Product p : displayedProducts) {
            tableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    p.getFormattedOriginalPrice(),
                    p.getFormattedFlashSalePrice(),
                    p.getDiscountBadge(),
                    p.getAvailableStock() + " units",
                    p.getStatus()
            });
        }
    }

    private void showAdjustStockDialog() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product selectedProduct = displayedProducts.get(selectedRow);

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Product:"));
        panel.add(new JLabel("<html><b>" + selectedProduct.getName() + "</b></html>"));
        panel.add(new JLabel("Current Stock:"));
        panel.add(new JLabel(selectedProduct.getAvailableStock() + " units"));
        panel.add(new JLabel("New Stock Quantity:"));

        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(selectedProduct.getAvailableStock(), 0, 10000, 1));
        panel.add(stockSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Adjust Inventory - " + selectedProduct.getProductId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int newStock = (Integer) stockSpinner.getValue();
            productService.updateStock(selectedProduct.getProductId(), newStock);
            refreshProducts();
            JOptionPane.showMessageDialog(this,
                    "Stock updated successfully to " + newStock + " units.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAddProductDialog() {
        JTextField idField = new JTextField("PROD-" + (productService.getAllProducts().size() + 101));
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField catField = new JTextField();
        JTextField origPriceField = new JTextField();
        JTextField salePriceField = new JTextField();
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(20, 0, 1000, 1));

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.add(new JLabel("Product ID:")); form.add(idField);
        form.add(new JLabel("Product Name:")); form.add(nameField);
        form.add(new JLabel("Description:")); form.add(descField);
        form.add(new JLabel("Category:")); form.add(catField);
        form.add(new JLabel("Original Price (\u20B9):")); form.add(origPriceField);
        form.add(new JLabel("Flash Sale Price (\u20B9):")); form.add(salePriceField);
        form.add(new JLabel("Initial Stock:")); form.add(stockSpinner);

        int res = JOptionPane.showConfirmDialog(this, form, "Add New Flash Sale Product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();
                String desc = descField.getText().trim();
                String cat = catField.getText().trim();
                BigDecimal origPrice = CurrencyUtil.parseAmount(origPriceField.getText());
                BigDecimal salePrice = CurrencyUtil.parseAmount(salePriceField.getText());
                int stock = (Integer) stockSpinner.getValue();

                if (!ValidationUtil.isNotEmpty(id) || !ValidationUtil.isNotEmpty(name)) {
                    throw new IllegalArgumentException("Product ID and Name cannot be empty.");
                }
                if (origPrice.compareTo(BigDecimal.ZERO) <= 0 || salePrice.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Prices must be greater than zero.");
                }
                if (salePrice.compareTo(origPrice) > 0) {
                    throw new IllegalArgumentException("Flash sale price cannot exceed original price.");
                }

                productService.addProduct(id, name, desc, cat, origPrice, salePrice, stock);
                refreshProducts();
                JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to add product: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
