/*
 * UserManagementFrame.java
 * Admin-only screen for managing users
 */
package com.dormsync.gui;

import com.dormsync.dao.UserDAO;
import com.dormsync.model.User;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementFrame extends javax.swing.JFrame {

    private User loggedInUser;
    private UserDAO userDAO = new UserDAO();

    private JTable tblUsers;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private List<Object[]> userRows = new ArrayList<>();

    private JPanel summaryPanel;

    public UserManagementFrame(User user) {
        this.loggedInUser = user;

        // Role check — only admins can open this
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            JOptionPane.showMessageDialog(null,
                    "Access Denied: Admins only.",
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
            new DashboardFrame(user).setVisible(true);
            this.dispose();
            return;
        }

        setTitle("DormSync - User Management");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // ══════════════════════════════════════════
        // HEADER
        // ══════════════════════════════════════════
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("USER MANAGEMENT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnBack = new JButton("BACK TO DASHBOARD");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setBackground(new Color(127, 140, 141));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> {
            new DashboardFrame(loggedInUser).setVisible(true);
            this.dispose();
        });
        headerPanel.add(btnBack, BorderLayout.EAST);

        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // ══════════════════════════════════════════
        // SUMMARY TILES
        // ══════════════════════════════════════════
        summaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        getContentPane().add(summaryPanel, BorderLayout.CENTER);
        summaryPanel.setPreferredSize(new Dimension(0, 0)); // will be part of center later

        // ══════════════════════════════════════════
        // CENTER PANEL: summary + table
        // ══════════════════════════════════════════
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);

        // Rebuild summary panel inside wrapper
        summaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        centerWrapper.add(summaryPanel, BorderLayout.NORTH);

        // Table wrapper
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(10, 40, 10, 40));

        // Search bar + title
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblTable = new JLabel("All Users");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTable.setForeground(new Color(44, 62, 80));
        topPanel.add(lblTable, BorderLayout.WEST);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(6, 10, 6, 10)));
        txtSearch.setToolTipText("Search by name or email...");
        topPanel.add(txtSearch, BorderLayout.EAST);

        tableWrapper.add(topPanel, BorderLayout.NORTH);

        tblUsers = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblUsers);
        styleTable(tblUsers, scrollPane);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        // Action buttons under table
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        actionPanel.setOpaque(false);

        JButton btnPromote = modernButton("PROMOTE TO ADMIN", new Color(41, 128, 185));
        JButton btnDemote = modernButton("DEMOTE TO USER", new Color(230, 126, 34));
        JButton btnDelete = modernButton("DELETE USER", new Color(192, 57, 43));
        JButton btnResetPass = modernButton("RESET PASSWORD", new Color(142, 68, 173));
        JButton btnRefresh = modernButton("REFRESH", new Color(39, 174, 96));

        btnPromote.addActionListener(e -> changeRole("ADMIN"));
        btnDemote.addActionListener(e -> changeRole("USER"));
        btnDelete.addActionListener(e -> deleteSelected());
        btnResetPass.addActionListener(e -> resetPassword());
        btnRefresh.addActionListener(e -> loadUsers());

        actionPanel.add(btnPromote);
        actionPanel.add(btnDemote);
        actionPanel.add(btnDelete);
        actionPanel.add(btnResetPass);
        actionPanel.add(btnRefresh);

        tableWrapper.add(actionPanel, BorderLayout.SOUTH);

        centerWrapper.add(tableWrapper, BorderLayout.CENTER);
        getContentPane().add(centerWrapper, BorderLayout.CENTER);

        // Live search
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });

        loadUsers();
    }

    // ══════════════════════════════════════════
    // STYLE HELPERS
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

    // ══════════════════════════════════════════
    // LOAD USERS
    // ══════════════════════════════════════════
    private void loadUsers() {
        String[] columns = {"ID", "Name", "Email", "Phone", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers.setModel(tableModel);

        userRows = userDAO.getAllUsersWithRoles();

        int total = 0, admins = 0, users = 0;
        for (Object[] row : userRows) {
            tableModel.addRow(row);
            total++;
            String role = (String) row[4];
            if ("ADMIN".equalsIgnoreCase(role)) admins++;
            else users++;
        }

        // Refresh summary tiles
        summaryPanel.removeAll();
        summaryPanel.add(createSummaryTile("Total Users", String.valueOf(total),  new Color(41, 128, 185)));
        summaryPanel.add(createSummaryTile("Admins",      String.valueOf(admins), new Color(142, 68, 173)));
        summaryPanel.add(createSummaryTile("Regular Users", String.valueOf(users), new Color(39, 174, 96)));
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    // ══════════════════════════════════════════
    // SEARCH FILTER
    // ══════════════════════════════════════════
    private void applyFilter() {
        String keyword = txtSearch.getText().trim();
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
        javax.swing.table.TableRowSorter<DefaultTableModel> sorter =
                new javax.swing.table.TableRowSorter<>(model);
        tblUsers.setRowSorter(sorter);

        if (keyword.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + keyword));
        }
    }

    // ══════════════════════════════════════════
    // ACTIONS
    // ══════════════════════════════════════════
    private void changeRole(String newRole) {
        int row = tblUsers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
            return;
        }

        int userId = Integer.parseInt(tblUsers.getValueAt(row, 0).toString());
        String userName = tblUsers.getValueAt(row, 1).toString();
        String currentRole = tblUsers.getValueAt(row, 4).toString();

        if (currentRole.equalsIgnoreCase(newRole)) {
            JOptionPane.showMessageDialog(this,
                    userName + " is already " + newRole + ".");
            return;
        }

        // Prevent admin from demoting themselves
        if (userId == loggedInUser.getUserId() && newRole.equals("USER")) {
            JOptionPane.showMessageDialog(this,
                    "You cannot demote yourself!",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Change " + userName + "'s role from " + currentRole + " to " + newRole + "?",
                "Confirm Role Change", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        if (userDAO.updateUserRole(userId, newRole)) {
            JOptionPane.showMessageDialog(this, "Role updated successfully!");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update role.");
        }
    }

    private void deleteSelected() {
        int row = tblUsers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
            return;
        }

        int userId = Integer.parseInt(tblUsers.getValueAt(row, 0).toString());
        String userName = tblUsers.getValueAt(row, 1).toString();

        if (userId == loggedInUser.getUserId()) {
            JOptionPane.showMessageDialog(this,
                    "You cannot delete your own account!",
                    "Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user \"" + userName + "\"?\n\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        if (userDAO.deleteUser(userId)) {
            JOptionPane.showMessageDialog(this, "User deleted.");
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete this user — they may have existing payments, "
                    + "allocations, chores, or visitors. Delete those first.",
                    "Delete Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetPassword() {
        int row = tblUsers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
            return;
        }

        int userId = Integer.parseInt(tblUsers.getValueAt(row, 0).toString());
        String userName = tblUsers.getValueAt(row, 1).toString();

        String newPass = JOptionPane.showInputDialog(this,
                "Enter new password for " + userName + ":");

        if (newPass == null || newPass.trim().isEmpty()) return;

        if (userDAO.updateUserPassword(userId, newPass.trim())) {
            JOptionPane.showMessageDialog(this, "Password updated.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update password.");
        }
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
            com.dormsync.model.User testAdmin = new com.dormsync.model.User(
                    1, "Test Admin", "admin@test.com", "12345", "9999999999", "ADMIN");
            new UserManagementFrame(testAdmin).setVisible(true);
        });
    }
}