/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import com.dormsync.dao.VisitorDAO;
import com.dormsync.model.User;
import com.dormsync.model.Visitor;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VisitorFrame extends javax.swing.JFrame {

    private User loggedInUser;

    // Admin components
    private JTextField txtVisitorName;
    private JTextField txtPhone;
    private JTextField txtVisitDate;
    private JTextField txtPurpose;
    private JComboBox<String> cmbStatus;
    private JTable tblVisitors;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnBack;

    // User components
    private JPanel userCardsContainer;
    private JPanel userSummaryPanel;

    private boolean isAdmin() {
        return loggedInUser != null && "ADMIN".equalsIgnoreCase(loggedInUser.getRole());
    }

    public VisitorFrame(User user) {
        this.loggedInUser = user;

        setTitle(isAdmin() ? "DormSync - Visitors" : "DormSync - My Visitors");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        String frameTitle = isAdmin() ? "VISITOR MANAGEMENT" : "MY VISITORS";
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

        // ROLE-BASED VIEW
        if (isAdmin()) {
            buildAdminView();
        } else {
            buildUserView();
        }

        loadVisitors();
    }

    // ══════════════════════════════════════════
    // ADMIN VIEW — form + CRUD + table
    // ══════════════════════════════════════════
    private void buildAdminView() {
        // FORM
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(20, 40, 10, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 30, 20, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(styledLabel("Visitor Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtVisitorName = styledTextField();
        formPanel.add(txtVisitorName, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Phone:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtPhone = styledTextField();
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(styledLabel("Visit Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtVisitDate = styledTextField();
        formPanel.add(txtVisitDate, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Purpose:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtPurpose = styledTextField();
        formPanel.add(txtPurpose, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbStatus = new JComboBox<>(new String[]{"Pending", "Approved", "Rejected", "Completed"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        btnAdd = modernButton("ADD VISITOR", new Color(41, 128, 185));
        btnUpdate = modernButton("UPDATE", new Color(230, 126, 34));
        btnDelete = modernButton("DELETE", new Color(192, 57, 43));
        btnClear = modernButton("CLEAR", new Color(127, 140, 141));

        btnAdd.addActionListener(e -> btnAddActionPerformed());
        btnUpdate.addActionListener(e -> btnUpdateActionPerformed());
        btnDelete.addActionListener(e -> btnDeleteActionPerformed());
        btnClear.addActionListener(e -> btnClearActionPerformed());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        formPanel.add(buttonPanel, gbc);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        getContentPane().add(formWrapper, BorderLayout.CENTER);

        // TABLE
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(10, 40, 25, 40));

        JLabel lblTable = new JLabel("Visitor Records");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTable.setForeground(new Color(44, 62, 80));
        lblTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableWrapper.add(lblTable, BorderLayout.NORTH);

        tblVisitors = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblVisitors);
        styleTable(tblVisitors, scrollPane);

        tblVisitors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVisitorsMouseClicked(evt);
            }
        });

        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(tableWrapper, BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════
    // USER VIEW — summary tiles + card list
    // ══════════════════════════════════════════
    private void buildUserView() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Summary tiles
        userSummaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        userSummaryPanel.setOpaque(false);
        userSummaryPanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        userSummaryPanel.add(createSummaryTile("Total Visitors", "0",  new Color(22, 160, 133)));
        userSummaryPanel.add(createSummaryTile("Pending",        "0",  new Color(230, 126, 34)));
        userSummaryPanel.add(createSummaryTile("Approved",       "0",  new Color(39, 174, 96)));

        mainPanel.add(userSummaryPanel, BorderLayout.NORTH);

        // Cards container
        userCardsContainer = new JPanel();
        userCardsContainer.setLayout(new BoxLayout(userCardsContainer, BoxLayout.Y_AXIS));
        userCardsContainer.setBackground(new Color(236, 240, 241));
        userCardsContainer.setBorder(new EmptyBorder(10, 40, 25, 40));

        JScrollPane scroll = new JScrollPane(userCardsContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scroll, BorderLayout.CENTER);

        getContentPane().add(mainPanel, BorderLayout.CENTER);
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

    private JPanel createVisitorCard(Visitor v) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Left: visitor name + phone
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblName = new JLabel("👤 " + v.getVisitorName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(new Color(44, 62, 80));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPhone = new JLabel("📞 " + v.getPhone());
        lblPhone.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPhone.setForeground(new Color(120, 120, 120));
        lblPhone.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(lblName);
        left.add(Box.createVerticalStrut(4));
        left.add(lblPhone);

        // Right: purpose + date + status
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel lblPurpose = new JLabel(v.getPurpose());
        lblPurpose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPurpose.setForeground(new Color(80, 80, 80));
        lblPurpose.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblDate = new JLabel("📅 " + v.getVisitDate());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(new Color(120, 120, 120));
        lblDate.setAlignmentX(Component.RIGHT_ALIGNMENT);

        Color statusColor;
        if ("Approved".equalsIgnoreCase(v.getStatus()) || "Completed".equalsIgnoreCase(v.getStatus()))
            statusColor = new Color(39, 174, 96);
        else if ("Rejected".equalsIgnoreCase(v.getStatus()))
            statusColor = new Color(192, 57, 43);
        else
            statusColor = new Color(230, 126, 34);

        JLabel lblStatus = new JLabel("● " + v.getStatus());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(statusColor);
        lblStatus.setAlignmentX(Component.RIGHT_ALIGNMENT);

        right.add(lblPurpose);
        right.add(Box.createVerticalStrut(4));
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
    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lbl;
    }

    private JTextField styledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(6, 8, 6, 8)));
        return field;
    }

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

    private void styleTable(JTable table, JScrollPane scroll) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(false);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
    }

    // ══════════════════════════════════════════
    // ADMIN ACTIONS (only reachable when isAdmin)
    // ══════════════════════════════════════════
    private void btnAddActionPerformed() {
        if (txtVisitorName.getText().trim().isEmpty()
                || txtPhone.getText().trim().isEmpty()
                || txtVisitDate.getText().trim().isEmpty()
                || txtPurpose.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        Visitor visitor = new Visitor();
        visitor.setUserId(loggedInUser.getUserId());  // admin's own id — used as logger
        visitor.setVisitorName(txtVisitorName.getText().trim());
        visitor.setPhone(txtPhone.getText().trim());
        visitor.setVisitDate(txtVisitDate.getText().trim());
        visitor.setPurpose(txtPurpose.getText().trim());
        visitor.setStatus(cmbStatus.getSelectedItem().toString());

        VisitorDAO dao = new VisitorDAO();
        if (dao.addVisitor(visitor)) {
            JOptionPane.showMessageDialog(this, "Visitor added successfully!");
            clearFields();
            loadVisitors();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add visitor!");
        }
    }

    private void btnUpdateActionPerformed() {
        int selectedRow = tblVisitors.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a visitor to update.");
            return;
        }

        String dateText = txtVisitDate.getText().trim();
        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Visit Date cannot be empty.");
            return;
        }

        int visitorId = Integer.parseInt(tblVisitors.getValueAt(selectedRow, 0).toString());
        int userId = Integer.parseInt(tblVisitors.getValueAt(selectedRow, 1).toString());

        Visitor visitor = new Visitor(
                visitorId, userId,
                txtVisitorName.getText().trim(),
                txtPhone.getText().trim(),
                dateText, "",
                txtPurpose.getText().trim(),
                cmbStatus.getSelectedItem().toString()
        );

        VisitorDAO dao = new VisitorDAO();
        if (dao.updateVisitor(visitor)) {
            JOptionPane.showMessageDialog(this, "Visitor updated successfully!");
            clearFields();
            loadVisitors();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.");
        }
    }

    private void btnDeleteActionPerformed() {
        int selectedRow = tblVisitors.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a visitor first.");
            return;
        }

        int visitorId = Integer.parseInt(tblVisitors.getValueAt(selectedRow, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this visitor?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            VisitorDAO dao = new VisitorDAO();
            if (dao.deleteVisitor(visitorId)) {
                JOptionPane.showMessageDialog(this, "Visitor deleted!");
                clearFields();
                loadVisitors();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete.");
            }
        }
    }

    private void btnClearActionPerformed() {
        clearFields();
    }

    private void tblVisitorsMouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = tblVisitors.getSelectedRow();
        if (selectedRow == -1) return;

        // Note: with the JOIN column layout, columns are:
        // 0=ID, 1=User, 2=Visitor, 3=Phone, 4=Date, 5=Purpose, 6=Status
        txtVisitorName.setText(tblVisitors.getValueAt(selectedRow, 2).toString());
        txtPhone.setText(tblVisitors.getValueAt(selectedRow, 3).toString());
        txtVisitDate.setText(tblVisitors.getValueAt(selectedRow, 4).toString());
        txtPurpose.setText(tblVisitors.getValueAt(selectedRow, 5).toString());
        cmbStatus.setSelectedItem(tblVisitors.getValueAt(selectedRow, 6).toString());
    }

    private void btnBackActionPerformed() {
        new DashboardFrame(loggedInUser).setVisible(true);
        this.dispose();
    }

    // ══════════════════════════════════════════
    // LOAD DATA
    // ══════════════════════════════════════════
    private void loadVisitors() {
        VisitorDAO dao = new VisitorDAO();

        // ===== ADMIN — table with user names =====
        if (isAdmin()) {
            String[] columns = {"ID", "User", "Visitor", "Phone", "Date", "Purpose", "Status"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            tblVisitors.setModel(model);

            List<Object[]> rows = dao.getAllVisitorsWithUser();
            for (Object[] row : rows) {
                model.addRow(row);
            }
            return;
        }

        // ===== USER — card list + summary =====
        List<Visitor> visitorList = dao.getVisitorsByUser(loggedInUser.getUserId());

        if (userCardsContainer == null) return;

        userCardsContainer.removeAll();

        int total = visitorList.size();
        int pending = 0;
        int approved = 0;
        for (Visitor v : visitorList) {
            if ("Pending".equalsIgnoreCase(v.getStatus())) pending++;
            if ("Approved".equalsIgnoreCase(v.getStatus()) || "Completed".equalsIgnoreCase(v.getStatus())) approved++;
        }

        // Refresh summary tiles
        userSummaryPanel.removeAll();
        userSummaryPanel.add(createSummaryTile("Total Visitors", String.valueOf(total),    new Color(22, 160, 133)));
        userSummaryPanel.add(createSummaryTile("Pending",        String.valueOf(pending),  new Color(230, 126, 34)));
        userSummaryPanel.add(createSummaryTile("Approved",       String.valueOf(approved), new Color(39, 174, 96)));

        if (visitorList.isEmpty()) {
            JLabel empty = new JLabel("No visitors have visited you yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(120, 120, 120));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            userCardsContainer.add(empty);
        } else {
            for (Visitor v : visitorList) {
                userCardsContainer.add(createVisitorCard(v));
                userCardsContainer.add(Box.createVerticalStrut(10));
            }
        }

        userCardsContainer.revalidate();
        userCardsContainer.repaint();
        userSummaryPanel.revalidate();
        userSummaryPanel.repaint();
    }

    private void clearFields() {
        if (txtVisitorName != null) txtVisitorName.setText("");
        if (txtPhone != null) txtPhone.setText("");
        if (txtVisitDate != null) txtVisitDate.setText("");
        if (txtPurpose != null) txtPurpose.setText("");
        if (cmbStatus != null) cmbStatus.setSelectedIndex(0);
        if (tblVisitors != null) tblVisitors.clearSelection();
    }

    // ══════════════════════════════════════════
    // MAIN
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
            User testUser = new User(1, "Test User", "testuser@gmail.com",
                    "12345", "9876543210", "USER");
            new VisitorFrame(testUser).setVisible(true);
        });
    }
}