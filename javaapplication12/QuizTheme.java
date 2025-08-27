package javaapplication12;

import javax.swing.*;
import java.awt.*;

public class QuizTheme {
    // Colors
    public static final Color PRIMARY_DARK = new Color(48, 63, 159);  // Dark Blue
    public static final Color PRIMARY_LIGHT = new Color(227, 242, 253);  // Light Blue
    public static final Color ACCENT = new Color(255, 193, 7);  // Amber
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);  // Dark Gray
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);  // Medium Gray
    public static final Color BACKGROUND = new Color(250, 250, 250);  // Off White
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADING_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    // Apply theme to a button
    public static void styleButton(JButton button) {
        button.setBackground(ACCENT);
        button.setForeground(TEXT_PRIMARY);
        button.setFont(BUTTON_FONT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT.darker(), 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT);
            }
        });
    }
    
    // Apply theme to a radio button
    public static void styleRadioButton(JRadioButton radio) {
        radio.setBackground(BACKGROUND);
        radio.setForeground(TEXT_PRIMARY);
        radio.setFont(BODY_FONT);
        radio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radio.setFocusPainted(false);
    }
    
    // Apply theme to a label
    public static void styleLabel(JLabel label) {
        label.setForeground(TEXT_PRIMARY);
        label.setFont(BODY_FONT);
    }
    
    // Apply theme to a title label
    public static void styleTitleLabel(JLabel label) {
        label.setForeground(TEXT_PRIMARY);
        label.setFont(TITLE_FONT);
    }
    
    // Apply theme to a text field
    public static void styleTextField(JTextField textField) {
        textField.setFont(BODY_FONT);
        textField.setBackground(PRIMARY_LIGHT);
        textField.setForeground(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_DARK, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    // Apply theme to a text area
    public static void styleTextArea(JTextArea textArea) {
        textArea.setFont(BODY_FONT);
        textArea.setBackground(PRIMARY_LIGHT);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_DARK, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    // Apply theme to a panel
    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_DARK, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
    // Apply theme to a frame
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND);
        ((JPanel)frame.getContentPane()).setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
    }
}
