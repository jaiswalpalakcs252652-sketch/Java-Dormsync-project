/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import com.dormsync.dao.PaymentDAO;
import com.dormsync.model.Payment;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PaymentFrame extends javax.swing.JFrame {

    private PaymentDAO paymentDAO = new PaymentDAO();
    private DefaultTableModel tableModel;
    private List<Payment> paymentList = new ArrayList<>();
    private com.dormsync.model.User loggedInUser;

    // Admin view components
    private JTable tblPayments;
    private JButton btnBack, btnExportPDF, btnPrintReceipt;

    // User view components
    private JPanel userCardsContainer;
    private JPanel userSummaryPanel;

    private boolean isAdmin() {
        return loggedInUser != null && "ADMIN".equalsIgnoreCase(loggedInUser.getRole());
    }

    public PaymentFrame(com.dormsync.model.User user) {
        this.loggedInUser = user;

        setTitle(isAdmin() ? "DormSync - Payments" : "DormSync - My Payments");
        setSize(1050, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        String frameTitle = isAdmin() ? "PAYMENT MANAGEMENT" : "MY PAYMENTS";
        JLabel lblTitle = new JLabel(frameTitle);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        btnBack = new JButton("BACK TO DASHBOARD");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> btnBackActionPerformed());
        headerPanel.add(btnBack, BorderLayout.EAST);

        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // ══════════════════════════════════════════
        // BUILD DIFFERENT VIEWS PER ROLE
        // ══════════════════════════════════════════
        if (isAdmin()) {
            buildAdminView();
        } else {
            buildUserView();
        }

        loadPayments();
    }

    // ══════════════════════════════════════════
    // ADMIN VIEW — table + export/print
    // ══════════════════════════════════════════
    private void buildAdminView() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(20, 40, 25, 40));

        JLabel lblTable = new JLabel("Payment Records");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTable.setForeground(new Color(44, 62, 80));
        lblTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableWrapper.add(lblTable, BorderLayout.NORTH);

        tblPayments = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblPayments);

        tblPayments.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblPayments.setRowHeight(30);
        tblPayments.setGridColor(new Color(220, 220, 220));
        tblPayments.setShowVerticalLines(false);
        tblPayments.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblPayments.getTableHeader().setBackground(new Color(44, 62, 80));
        tblPayments.getTableHeader().setForeground(Color.WHITE);
        tblPayments.getTableHeader().setPreferredSize(new Dimension(0, 35));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        // Utility buttons at bottom
        JPanel utilityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        utilityPanel.setOpaque(false);
        utilityPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        btnExportPDF = modernButton("EXPORT PDF", new Color(142, 68, 173));
        btnPrintReceipt = modernButton("PRINT RECEIPT", new Color(22, 160, 133));
        btnExportPDF.addActionListener(e -> btnExportPDFActionPerformed());
        btnPrintReceipt.addActionListener(e -> btnPrintReceiptActionPerformed());

        utilityPanel.add(btnExportPDF);
        utilityPanel.add(btnPrintReceipt);
        tableWrapper.add(utilityPanel, BorderLayout.SOUTH);

        getContentPane().add(tableWrapper, BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════
    // USER VIEW — summary tiles + card list + ADD button
    // ══════════════════════════════════════════
    private void buildUserView() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Summary tiles
        userSummaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        userSummaryPanel.setOpaque(false);
        userSummaryPanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        userSummaryPanel.add(createSummaryTile("Total Paid",    "₹0",  new Color(39, 174, 96)));
        userSummaryPanel.add(createSummaryTile("Pending",       "₹0",  new Color(230, 126, 34)));
        userSummaryPanel.add(createSummaryTile("Transactions",  "0",   new Color(41, 128, 185)));

        mainPanel.add(userSummaryPanel, BorderLayout.NORTH);

        // Cards container
        userCardsContainer = new JPanel();
        userCardsContainer.setLayout(new BoxLayout(userCardsContainer, BoxLayout.Y_AXIS));
        userCardsContainer.setBackground(new Color(236, 240, 241));
        userCardsContainer.setBorder(new EmptyBorder(10, 40, 10, 40));

        JScrollPane scroll = new JScrollPane(userCardsContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        // ADD PAYMENT button at bottom
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        addPanel.setOpaque(false);
        JButton btnAdd = modernButton("➕  ADD PAYMENT", new Color(41, 128, 185));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBorder(new EmptyBorder(12, 30, 12, 30));
        btnAdd.addActionListener(e -> showAddPaymentDialog());
        addPanel.add(btnAdd);
        mainPanel.add(addPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
    }

    // Add payment dialog (user only)
    private void showAddPaymentDialog() {
        JTextField amountField = new JTextField();
        JComboBox<String> typeField = new JComboBox<>(new String[]{"Cash", "UPI", "Card", "Bank Transfer"});
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());

        Object[] message = {
            "Amount (₹):", amountField,
            "Payment Type:", typeField,
            "Payment Date (YYYY-MM-DD):", dateField
        };

        int option = JOptionPane.showConfirmDialog(this, message,
                "Add Payment", JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) return;

        String amountText = amountField.getText().trim();
        String dateText = dateField.getText().trim();
        String type = typeField.getSelectedItem().toString();

        if (amountText.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            java.sql.Date.valueOf(dateText);  // validate

            Payment payment = new Payment();
            payment.setUserId(loggedInUser.getUserId());    // ✅ always self
            payment.setAmount(amount);
            payment.setPaymentType(type);
            payment.setPaymentDate(dateText);
            payment.setStatus("Pending");                   // default for user-added

            if (paymentDAO.addPayment(payment)) {
                JOptionPane.showMessageDialog(this, "Payment added successfully!");
                loadPayments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add payment.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
        }
    }

    private JPanel createSummaryTile(String title, String value, Color color) {
        JPanel tile = new JPanel(new BorderLayout());
        tile.setBackground(Color.WHITE);
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, color),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(100, 100, 100));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(color);

        tile.add(lblTitle, BorderLayout.NORTH);
        tile.add(lblValue, BorderLayout.CENTER);
        return tile;
    }

    private JPanel createPaymentCard(Payment p) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Left: amount + method
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblAmount = new JLabel("₹" + (int) p.getAmount());
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblAmount.setForeground(new Color(44, 62, 80));
        lblAmount.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMethod = new JLabel("via " + p.getPaymentType());
        lblMethod.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMethod.setForeground(new Color(120, 120, 120));
        lblMethod.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(lblAmount);
        left.add(Box.createVerticalStrut(4));
        left.add(lblMethod);

        // Right: date + status
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel lblDate = new JLabel("📅 " + p.getPaymentDate());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(new Color(120, 120, 120));
        lblDate.setAlignmentX(Component.RIGHT_ALIGNMENT);

        Color statusColor;
        if ("Paid".equalsIgnoreCase(p.getStatus())) statusColor = new Color(39, 174, 96);
        else if ("Pending".equalsIgnoreCase(p.getStatus())) statusColor = new Color(230, 126, 34);
        else statusColor = new Color(192, 57, 43);

        JLabel lblStatus = new JLabel("● " + p.getStatus());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(statusColor);
        lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        right.add(lblDate);
        right.add(Box.createVerticalStrut(4));
        right.add(lblStatus);

        card.add(left, BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    // ══════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════
    private JButton modernButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void btnBackActionPerformed() {
        new DashboardFrame(loggedInUser).setVisible(true);
        this.dispose();
    }

    // ══════════════════════════════════════════
    // PRINT RECEIPT (admin only)
    // ══════════════════════════════════════════
    private void btnPrintReceiptActionPerformed() {
        try {
            boolean complete = tblPayments.print();
            JOptionPane.showMessageDialog(this,
                    complete ? "Printing Complete" : "Printing Cancelled");
        } catch (java.awt.print.PrinterException e) {
            JOptionPane.showMessageDialog(this, "Print Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    // EXPORT PDF (admin only)
    // ══════════════════════════════════════════
    private void btnExportPDFActionPerformed() {
        try {
            com.lowagie.text.Document document = new com.lowagie.text.Document();
            java.io.FileOutputStream fos = new java.io.FileOutputStream("Payments_Report.pdf");
            com.lowagie.text.pdf.PdfWriter.getInstance(document, fos);
            document.open();

            document.add(new com.lowagie.text.Paragraph("DORMSYNC - PAYMENTS REPORT"));
            document.add(new com.lowagie.text.Paragraph(" "));

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(6);
            table.addCell("Payment ID");
            table.addCell("User");
            table.addCell("Amount");
            table.addCell("Type");
            table.addCell("Date");
            table.addCell("Status");

            DefaultTableModel model = (DefaultTableModel) tblPayments.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    table.addCell(value == null ? "" : value.toString());
                }
            }

            document.add(table);
            document.close();
            fos.close();

            JOptionPane.showMessageDialog(this, "PDF Exported to Payments_Report.pdf");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════
    // LOAD DATA — role-based
    // ══════════════════════════════════════════
    private void loadPayments() {

        // ===== ADMIN: use JOIN query, show user names =====
        if (isAdmin()) {
            String[] columns = {"ID", "User", "Amount", "Type", "Date", "Status"};
            tableModel = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            tblPayments.setModel(tableModel);

            List<Object[]> rows = paymentDAO.getAllPaymentsWithUser();
            for (Object[] row : rows) {
                tableModel.addRow(row);
            }
            return;
        }

        // ===== USER: use getPaymentsByUser, show cards =====
        paymentList = paymentDAO.getPaymentsByUser(loggedInUser.getUserId());

        if (userCardsContainer == null) return;

        userCardsContainer.removeAll();

        double totalPaid = 0, totalPending = 0;
        for (Payment p : paymentList) {
            if ("Paid".equalsIgnoreCase(p.getStatus())) totalPaid += p.getAmount();
            if ("Pending".equalsIgnoreCase(p.getStatus())) totalPending += p.getAmount();
        }

        // Refresh summary tiles
        userSummaryPanel.removeAll();
        userSummaryPanel.add(createSummaryTile("Total Paid",    "₹" + (int) totalPaid,    new Color(39, 174, 96)));
        userSummaryPanel.add(createSummaryTile("Pending",       "₹" + (int) totalPending, new Color(230, 126, 34)));
        userSummaryPanel.add(createSummaryTile("Transactions",  String.valueOf(paymentList.size()), new Color(41, 128, 185)));

        if (paymentList.isEmpty()) {
            JLabel empty = new JLabel("You have no payment records yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(120, 120, 120));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            userCardsContainer.add(empty);
        } else {
            for (Payment p : paymentList) {
                userCardsContainer.add(createPaymentCard(p));
                userCardsContainer.add(Box.createVerticalStrut(10));
            }
        }

        userCardsContainer.revalidate();
        userCardsContainer.repaint();
        userSummaryPanel.revalidate();
        userSummaryPanel.repaint();
    }

    // ══════════════════════════════════════════
    // MAIN (for testing)
    // ══════════════════════════════════════════
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {}

        java.awt.EventQueue.invokeLater(() -> {
            com.dormsync.model.User testUser = new com.dormsync.model.User(
                1, "Test User", "testuser@gmail.com", "12345", "9876543210", "USER"
            );
            new PaymentFrame(testUser).setVisible(true);
        });
    }
}