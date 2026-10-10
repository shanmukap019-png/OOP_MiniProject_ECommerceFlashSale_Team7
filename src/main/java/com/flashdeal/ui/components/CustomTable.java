package com.flashdeal.ui.components;

import com.flashdeal.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * Modern styled JTable with clean headers, comfortable row height, and alternating row colors.
 */
public class CustomTable extends JTable {

    public CustomTable(TableModel model) {
        super(model);
        setRowHeight(38);
        setFont(UITheme.FONT_BODY);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionBackground(new Color(241, 245, 249));
        setSelectionForeground(UITheme.COLOR_TEXT_PRIMARY);

        // Header styling
        JTableHeader header = getTableHeader();
        header.setFont(UITheme.FONT_BODY_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(UITheme.COLOR_TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);

        // Center / Left padding renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });
    }
}
