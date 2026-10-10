package com.flashdeal.ui.theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Visual design system and theme constants for the FLASHDEAL PRO Swing Desktop App.
 * Implements a modern SaaS / E-commerce admin dashboard aesthetic.
 */
public final class UITheme {

    // Palette Colors
    public static final Color COLOR_PRIMARY = new Color(30, 41, 59);       // Slate 800
    public static final Color COLOR_PRIMARY_DARK = new Color(15, 23, 42);  // Slate 900
    public static final Color COLOR_ACCENT = new Color(225, 29, 72);       // Rose 600 (Flash Deal Sale)
    public static final Color COLOR_ACCENT_HOVER = new Color(190, 18, 60); // Rose 700
    public static final Color COLOR_INDIGO = new Color(79, 70, 229);       // Indigo 600
    public static final Color COLOR_BG = new Color(248, 250, 252);         // Slate 50
    public static final Color COLOR_SURFACE = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(226, 232, 240);     // Slate 200

    public static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);  // Slate 900
    public static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139); // Slate 500
    public static final Color COLOR_TEXT_LIGHT = new Color(241, 245, 249); // Slate 100

    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);     // Emerald 500
    public static final Color COLOR_SUCCESS_BG = new Color(209, 250, 229);  // Emerald 100
    public static final Color COLOR_DANGER = new Color(239, 68, 68);       // Red 500
    public static final Color COLOR_DANGER_BG = new Color(254, 226, 226);   // Red 100
    public static final Color COLOR_WARNING = new Color(245, 158, 11);     // Amber 500
    public static final Color COLOR_WARNING_BG = new Color(254, 243, 199); // Amber 100

    // Typography
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_MEDIUM = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC_VALUE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    private UITheme() {}

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(COLOR_ACCENT);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(COLOR_ACCENT, 1, true), new EmptyBorder(8, 18, 8, 18)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(COLOR_ACCENT_HOVER);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(COLOR_ACCENT);
            }
        });
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(COLOR_TEXT_PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1, true), new EmptyBorder(8, 16, 8, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(COLOR_BG);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    public static Border createCardBorder() {
        return new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        );
    }
}
