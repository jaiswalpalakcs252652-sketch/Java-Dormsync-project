/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminPanelFrame extends javax.swing.JFrame {

    private com.dormsync.model.User loggedInUser;

    // --- Components ---
    private JButton btnGenerateRent, btnExportPaymentsPDF, btnBack;

    public AdminPanelFrame(com.dormsync.model.User user) {
        this.loggedInUser = user;

        // 1. Basic Frame Setup
        setTitle("DormSync - Admin Panel");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        // 2. Main Background
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // ==========================================
        // 3. TOP HEADER BAR (Dark Navy)
        // ==========================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("ADMIN PANEL");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        btnBack = new JButton("BACK TO DASHBOARD");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setBackground(new Color(127, 140, 141)); // Gray
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> btnBackActionPerformed());
        headerPanel.add(btnBack, BorderLayout.EAST);

        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // ==========================================
        // 4. CENTER CARD (White)
        // ==========================================
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // --- Section 1: Bulk Operations ---
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        JLabel lblBulkTitle = new JLabel("Bulk Operations");
        lblBulkTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBulkTitle.setForeground(new Color(44, 62, 80));
        lblBulkTitle.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(lblBulkTitle, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblBulkDesc = new JLabel("Generate pending rent for all active tenants.");
        lblBulkDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblBulkDesc.setForeground(new Color(100, 100, 100));
        lblBulkDesc.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(lblBulkDesc, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 30, 0);
        btnGenerateRent = modernButton("GENERATE MONTHLY RENT", new Color(230, 126, 34)); // Orange
        btnGenerateRent.addActionListener(e -> btnGenerateRentActionPerformed());
        cardPanel.add(btnGenerateRent, gbc);

        // --- Section 2: Reports & Exports ---
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        JLabel lblReportsTitle = new JLabel("Reports & Exports");
        lblReportsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblReportsTitle.setForeground(new Color(44, 62, 80));
        lblReportsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(lblReportsTitle, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel lblReportsDesc = new JLabel("Export all payment records as a PDF report.");
        lblReportsDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblReportsDesc.setForeground(new Color(100, 100, 100));
        lblReportsDesc.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(lblReportsDesc, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        btnExportPaymentsPDF = modernButton("EXPORT PAYMENTS TO PDF", new Color(142, 68, 173)); // Purple
        btnExportPaymentsPDF.addActionListener(e -> btnExportPaymentsPDFActionPerformed());
        cardPanel.add(btnExportPaymentsPDF, gbc);

        cardWrapper.add(cardPanel);
        getContentPane().add(cardWrapper, BorderLayout.CENTER);
    }

    // ==========================================
    // HELPER METHOD
    // ==========================================
    private JButton modernButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 30, 12, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==========================================
    // BULK OPERATION: Generate Monthly Rent
    // ==========================================
    private void btnGenerateRentActionPerformed() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "This will generate PENDING rent for ALL Active tenants.\n\nProceed?",
                "Confirm Rent Generation",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) return;

        String monthYear = JOptionPane.showInputDialog(
                this,
                "Enter Month (YYYY-MM):",
                java.time.YearMonth.now().toString()
        );

        if (monthYear == null || monthYear.trim().isEmpty()) return;
        monthYear = monthYear.trim();

        if (!monthYear.matches("\\d{4}-\\d{2}")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid format!\nPlease use YYYY-MM (Example: 2026-09)",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            com.dormsync.dao.PaymentDAO dao = new com.dormsync.dao.PaymentDAO();
            int generated = dao.generateMonthlyRent(monthYear);

            if (generated > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        generated + " rent records generated for " + monthYear,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No new records generated.\n\nAll Active tenants may already be billed for " + monthYear + ".",
                        "No Changes",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    // ==========================================
    // REPORT: Export Payments to PDF
    // ==========================================
    private void btnExportPaymentsPDFActionPerformed() {
        try {
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            java.io.FileOutputStream fos = new java.io.FileOutputStream("Admin_Payments_Report.pdf");
            com.lowagie.text.pdf.PdfWriter.getInstance(document, fos);

            document.open();
            document.add(new com.lowagie.text.Paragraph("DORMSYNC - ADMIN PAYMENTS REPORT"));
            document.add(new com.lowagie.text.Paragraph(" "));

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(6);
            table.addCell("Payment ID");
            table.addCell("User ID");
            table.addCell("Amount");
            table.addCell("Payment Type");
            table.addCell("Date");
            table.addCell("Status");

            String sql = "SELECT * FROM payments ORDER BY payment_id";
            try (java.sql.Connection con = com.dormsync.util.DBConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    table.addCell(String.valueOf(rs.getInt("payment_id")));
                    table.addCell(String.valueOf(rs.getInt("user_id")));
                    table.addCell(String.valueOf(rs.getDouble("amount")));
                    table.addCell(rs.getString("payment_type"));
                    table.addCell(rs.getString("payment_date"));
                    table.addCell(rs.getString("status"));
                }
            }

            document.add(table);
            document.close();
            fos.close();

            JOptionPane.showMessageDialog(this, "PDF Exported: Admin_Payments_Report.pdf");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================
    // BACK
    // ==========================================
    private void btnBackActionPerformed() {
        new DashboardFrame(loggedInUser).setVisible(true);
        this.dispose();
    }

    // ==========================================
    // MAIN (For testing)
    // ==========================================
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Nimbus theme not found.");
        }

        java.awt.EventQueue.invokeLater(() -> {
            com.dormsync.model.User testUser = new com.dormsync.model.User(
                    1, "Test Admin", "admin@test.com", "12345", "9999999999", "ADMIN"
            );
            new AdminPanelFrame(testUser).setVisible(true);
        });
    }
}