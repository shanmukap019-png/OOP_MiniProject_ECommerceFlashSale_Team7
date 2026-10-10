package com.flashdeal.ui;

import com.flashdeal.persistence.CustomerRepository;
import com.flashdeal.service.FlashSaleService;
import com.flashdeal.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Main application window for FLASHDEAL PRO.
 * Provides a contemporary administrative dashboard layout with sidebar navigation,
 * top branding bar, and tabbed view switching.
 */
public class MainFrame extends JFrame {
    private final FlashSaleService flashSaleService;
    private final CustomerRepository customerRepository;

    private JPanel contentContainer;
    private CardLayout cardLayout;
    private Map<String, JButton> navButtons;

    // View Panels
    private DashboardPanel dashboardPanel;
    private ProductPanel productPanel;
    private FlashSalePanel flashSalePanel;
    private OrderHistoryPanel orderHistoryPanel;
    private ReportsPanel reportsPanel;

    private static final String VIEW_DASHBOARD = "DASHBOARD";
    private static final String VIEW_PRODUCTS = "PRODUCTS";
    private static final String VIEW_SIMULATOR = "SIMULATOR";
    private static final String VIEW_ORDERS = "ORDERS";
    private static final String VIEW_REPORTS = "REPORTS";

    public MainFrame(FlashSaleService flashSaleService, CustomerRepository customerRepository) {
        this.flashSaleService = flashSaleService;
        this.customerRepository = customerRepository;
        this.navButtons = new HashMap<>();

        initFrame();
        initNavigationAndViews();
    }

    private void initFrame() {
        setTitle("FLASHDEAL PRO - E-Commerce Flash Sale Simulator | MNR University Project #7");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 840);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null); // Center on screen
    }

    private void initNavigationAndViews() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.COLOR_BG);

        // 1. TOP BRANDING BAR
        JPanel topBar = createTopBar();
        rootPanel.add(topBar, BorderLayout.NORTH);

        // 2. LEFT SIDEBAR NAVIGATION
        JPanel sidebar = createSidebar();
        rootPanel.add(sidebar, BorderLayout.WEST);

        // 3. CENTER CONTENT CONTAINER (CardLayout)
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(UITheme.COLOR_BG);

        // Instantiate view panels
        dashboardPanel = new DashboardPanel(flashSaleService, () -> showView(VIEW_SIMULATOR));
        productPanel = new ProductPanel(flashSaleService);
        flashSalePanel = new FlashSalePanel(flashSaleService, customerRepository, this::onGlobalDataChanged);
        orderHistoryPanel = new OrderHistoryPanel(flashSaleService);
        reportsPanel = new ReportsPanel(flashSaleService);

        contentContainer.add(dashboardPanel, VIEW_DASHBOARD);
        contentContainer.add(productPanel, VIEW_PRODUCTS);
        contentContainer.add(flashSalePanel, VIEW_SIMULATOR);
        contentContainer.add(orderHistoryPanel, VIEW_ORDERS);
        contentContainer.add(reportsPanel, VIEW_REPORTS);

        rootPanel.add(contentContainer, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Default view
        showView(VIEW_DASHBOARD);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.COLOR_PRIMARY_DARK);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.COLOR_BORDER),
                new EmptyBorder(12, 20, 12, 20)
        ));

        // Left Branding
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        brandPanel.setBackground(UITheme.COLOR_PRIMARY_DARK);

        JLabel logoTag = new JLabel("\u26A1 FLASHDEAL PRO");
        logoTag.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoTag.setForeground(Color.WHITE);

        JLabel themeBadge = new JLabel(" BIG SALE DAY ");
        themeBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        themeBadge.setOpaque(true);
        themeBadge.setBackground(UITheme.COLOR_ACCENT);
        themeBadge.setForeground(Color.WHITE);
        themeBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        JLabel subBrand = new JLabel("E-Commerce Flash Sale Concurrency Simulator");
        subBrand.setFont(UITheme.FONT_BODY);
        subBrand.setForeground(new Color(148, 163, 184)); // Slate 400

        brandPanel.add(logoTag);
        brandPanel.add(themeBadge);
        brandPanel.add(subBrand);

        // Right Info (Academic credentials & live status)
        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        metaPanel.setBackground(UITheme.COLOR_PRIMARY_DARK);

        JLabel rollLabel = new JLabel("MNR University | Roll: 35 | Project #7");
        rollLabel.setFont(UITheme.FONT_BODY_BOLD);
        rollLabel.setForeground(new Color(226, 232, 240));

        JLabel statusPill = new JLabel("  \u25CF MULTITHREADING ACTIVE  ");
        statusPill.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusPill.setOpaque(true);
        statusPill.setBackground(new Color(6, 78, 59)); // Dark Emerald
        statusPill.setForeground(new Color(110, 231, 183)); // Light Emerald
        statusPill.setBorder(new EmptyBorder(3, 8, 3, 8));

        metaPanel.add(rollLabel);
        metaPanel.add(statusPill);

        topBar.add(brandPanel, BorderLayout.WEST);
        topBar.add(metaPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(UITheme.COLOR_PRIMARY);
        sidebar.setBorder(new EmptyBorder(20, 12, 20, 12));

        JLabel navHeading = new JLabel("NAVIGATION");
        navHeading.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navHeading.setForeground(new Color(148, 163, 184));
        navHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navHeading);
        sidebar.add(Box.createVerticalStrut(12));

        // Navigation Items
        sidebar.add(createNavButton(VIEW_DASHBOARD, "\uD83D\uDCCA  Dashboard"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton(VIEW_PRODUCTS, "\uD83D\uDCE6  Product Catalog"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton(VIEW_SIMULATOR, "\u26A1  Flash Sale Simulator"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton(VIEW_ORDERS, "\uD83D\uDCDD  Order History"));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(createNavButton(VIEW_REPORTS, "\uD83D\uDCC8  Sales Analytics"));

        sidebar.add(Box.createVerticalGlue());

        // Academic details box in sidebar footer
        JPanel footerBox = new JPanel();
        footerBox.setLayout(new BoxLayout(footerBox, BoxLayout.Y_AXIS));
        footerBox.setBackground(UITheme.COLOR_PRIMARY_DARK);
        footerBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        footerBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerBox.setMaximumSize(new Dimension(216, 120));

        JLabel f1 = new JLabel("OOP Mini-Project");
        f1.setFont(UITheme.FONT_BODY_BOLD);
        f1.setForeground(Color.WHITE);

        JLabel f2 = new JLabel("Team 7 | 5 Members");
        f2.setFont(UITheme.FONT_SMALL);
        f2.setForeground(new Color(148, 163, 184));

        JLabel f3 = new JLabel("Dept: CSE / AIML");
        f3.setFont(UITheme.FONT_SMALL);
        f3.setForeground(new Color(148, 163, 184));

        JLabel f4 = new JLabel("JDK 17+ | Java Swing");
        f4.setFont(UITheme.FONT_SMALL);
        f4.setForeground(new Color(148, 163, 184));

        footerBox.add(f1);
        footerBox.add(Box.createVerticalStrut(4));
        footerBox.add(f2);
        footerBox.add(f3);
        footerBox.add(f4);

        sidebar.add(footerBox);
        return sidebar;
    }

    private JButton createNavButton(String viewKey, String label) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setForeground(new Color(226, 232, 240));
        btn.setBackground(UITheme.COLOR_PRIMARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(216, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> showView(viewKey));
        navButtons.put(viewKey, btn);
        return btn;
    }

    public void showView(String viewKey) {
        cardLayout.show(contentContainer, viewKey);

        // Highlight active nav button
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(viewKey)) {
                btn.setBackground(UITheme.COLOR_ACCENT);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(UITheme.COLOR_PRIMARY);
                btn.setForeground(new Color(226, 232, 240));
            }
        }

        // Trigger view refresh
        if (VIEW_DASHBOARD.equals(viewKey)) {
            dashboardPanel.refreshData();
        } else if (VIEW_PRODUCTS.equals(viewKey)) {
            productPanel.refreshProducts();
        } else if (VIEW_SIMULATOR.equals(viewKey)) {
            flashSalePanel.refreshProductList();
        } else if (VIEW_ORDERS.equals(viewKey)) {
            orderHistoryPanel.refreshOrders();
        } else if (VIEW_REPORTS.equals(viewKey)) {
            reportsPanel.refreshReport();
        }
    }

    private void onGlobalDataChanged() {
        dashboardPanel.refreshData();
        productPanel.refreshProducts();
        orderHistoryPanel.refreshOrders();
        reportsPanel.refreshReport();
    }
}
