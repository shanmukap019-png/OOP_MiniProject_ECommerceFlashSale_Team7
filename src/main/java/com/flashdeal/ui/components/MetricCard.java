package com.flashdeal.ui.components;

import com.flashdeal.ui.theme.UITheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Modern KPI Metric Card displaying title, primary big value, and supporting subtitle.
 */
public class MetricCard extends JPanel {
    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;
    private final Color accentColor;

    public MetricCard(String title, String initialValue, String subtitle, Color accentColor) {
        this.accentColor = accentColor;
        setLayout(new BorderLayout(8, 6));
        setBackground(UITheme.COLOR_SURFACE);
        setBorder(new CompoundBorder(new LineBorder(UITheme.COLOR_BORDER, 1, true), new EmptyBorder(14, 16, 14, 16)));

        // Top: Title
        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(UITheme.FONT_SMALL);
        titleLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        // Center: Value
        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(UITheme.FONT_METRIC_VALUE);
        valueLabel.setForeground(UITheme.COLOR_TEXT_PRIMARY);

        // Bottom: Subtitle
        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(UITheme.FONT_SMALL);
        subtitleLabel.setForeground(UITheme.COLOR_TEXT_MUTED);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(UITheme.COLOR_SURFACE);

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(valueLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(subtitleLabel);

        add(content, BorderLayout.CENTER);

        // Left accent bar
        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(4, 0));
        bar.setBackground(accentColor);
        add(bar, BorderLayout.WEST);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }
}
