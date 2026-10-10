package com.flashdeal.ui.components;

import com.flashdeal.model.OrderStatus;
import com.flashdeal.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer displaying OrderStatus as styled rounded color badges.
 */
public class BadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(UITheme.FONT_BODY_BOLD);
        label.setBorder(new EmptyBorder(4, 10, 4, 10));

        if (value instanceof OrderStatus) {
            OrderStatus status = (OrderStatus) value;
            switch (status) {
                case SUCCESS:
                    label.setForeground(new Color(6, 95, 70)); // Dark Emerald
                    label.setBackground(UITheme.COLOR_SUCCESS_BG);
                    label.setText("SUCCESS");
                    break;
                case OUT_OF_STOCK:
                    label.setForeground(new Color(153, 27, 27)); // Dark Red
                    label.setBackground(UITheme.COLOR_DANGER_BG);
                    label.setText("OUT OF STOCK");
                    break;
                case FAILED:
                default:
                    label.setForeground(new Color(146, 64, 14)); // Dark Amber
                    label.setBackground(UITheme.COLOR_WARNING_BG);
                    label.setText("FAILED");
                    break;
            }
            label.setOpaque(true);
        } else if (value != null) {
            String str = value.toString();
            if ("IN_STOCK".equalsIgnoreCase(str)) {
                label.setForeground(new Color(6, 95, 70));
                label.setBackground(UITheme.COLOR_SUCCESS_BG);
                label.setText("In Stock");
            } else if ("LOW_STOCK".equalsIgnoreCase(str)) {
                label.setForeground(new Color(146, 64, 14));
                label.setBackground(UITheme.COLOR_WARNING_BG);
                label.setText("Low Stock");
            } else if ("OUT_OF_STOCK".equalsIgnoreCase(str)) {
                label.setForeground(new Color(153, 27, 27));
                label.setBackground(UITheme.COLOR_DANGER_BG);
                label.setText("Out of Stock");
            }
            label.setOpaque(true);
        }

        return label;
    }
}
