/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dormsync.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends javax.swing.JFrame {
    private com.dormsync.model.User loggedInUser;

    public DashboardFrame(com.dormsync.model.User user) {
        this.loggedInUser = user;

        boolean isAdmin = user != null && "ADMIN".equalsIgnoreCase(user.getRole());

        setTitle(isAdmin ? "DormSync Dashboard - Admin" : "DormSync Dashboard - My Account");
        setSize(1000, isAdmin ? 720 : 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel(isAdmin ? "DORMSYNC ADMIN DASHBOARD" : "DORMSYNC - MY ACCOUNT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        headerRightPanel.setOpaque(false);

        JLabel lblWelcome = new JLabel("Welcome, " + user.getName());
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("LOGOUT");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> btnLogoutActionPerformed());

        headerRightPanel.add(lblWelcome);
        headerRightPanel.add(btnLogout);
        headerPanel.add(headerRightPanel, BorderLayout.EAST);

        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // STATS
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(20, 40, 10, 40));

        com.dormsync.dao.RoomDAO roomDAO = new com.dormsync.dao.RoomDAO();
        com.dormsync.dao.UserDAO userDAO = new com.dormsync.dao.UserDAO();
        com.dormsync.dao.PaymentDAO paymentDAO = new com.dormsync.dao.PaymentDAO();

        if (isAdmin) {
            int totalRooms = roomDAO.countByStatus("Available") + roomDAO.countByStatus("Occupied");
            int available  = roomDAO.countByStatus("Available");
            int tenants    = userDAO.countUsers();
            int pending    = paymentDAO.countByStatus("Pending");

            statsPanel.add(createStatCard("Total Rooms", String.valueOf(totalRooms), new Color(41, 128, 185)));
            statsPanel.add(createStatCard("Available",   String.valueOf(available),  new Color(39, 174, 96)));
            statsPanel.add(createStatCard("Tenants",     String.valueOf(tenants),    new Color(230, 126, 34)));
            statsPanel.add(createStatCard("Pending Pay", String.valueOf(pending),    new Color(192, 57, 43)));
        } else {
            statsPanel.add(createStatCard("My Room",     "—",  new Color(41, 128, 185)));
            statsPanel.add(createStatCard("Rent Due",    "—",  new Color(39, 174, 96)));
            statsPanel.add(createStatCard("My Visitors", "0",  new Color(230, 126, 34)));
            statsPanel.add(createStatCard("My Chores",   "0",  new Color(142, 68, 173)));
        }

        // BUTTONS
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 40, 40, 40));

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(centerPanel);

        if (isAdmin) {
            centerPanel.setLayout(new GridLayout(3, 3, 25, 25));
            centerPanel.setPreferredSize(new Dimension(700, 380));

            JButton btnRooms = createDashboardButton("ROOMS", new Color(41, 128, 185));
            JButton btnExpenses = createDashboardButton("EXPENSES", new Color(230, 126, 34));
            JButton btnPayments = createDashboardButton("PAYMENTS", new Color(39, 174, 96));
            JButton btnChores = createDashboardButton("CHORES", new Color(142, 68, 173));
            JButton btnVisitors = createDashboardButton("VISITORS", new Color(22, 160, 133));
            JButton btnAdminPanel = createDashboardButton("ADMIN PANEL", new Color(44, 62, 80));
            JButton btnRoomAllocation = createDashboardButton("ROOM ALLOCATION", new Color(211, 84, 0));
            JButton btnSettings = createDashboardButton("USER MANAGEMENT", new Color(127, 140, 141));

            btnRooms.addActionListener(e -> { new RoomFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnExpenses.addActionListener(e -> { new ExpenseFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnPayments.addActionListener(e -> { new PaymentFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnChores.addActionListener(e -> { new ChoreFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnVisitors.addActionListener(e -> { new VisitorFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnAdminPanel.addActionListener(e -> { new AdminPanelFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnRoomAllocation.addActionListener(e -> { new RoomAllocationFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnSettings.addActionListener(e -> { new UserManagementFrame(loggedInUser).setVisible(true); this.dispose(); });

            centerPanel.add(btnRooms);
            centerPanel.add(btnExpenses);
            centerPanel.add(btnPayments);
            centerPanel.add(btnChores);
            centerPanel.add(btnVisitors);
            centerPanel.add(btnAdminPanel);
            centerPanel.add(btnRoomAllocation);
            centerPanel.add(btnSettings);
        } else {
            centerPanel.setLayout(new GridLayout(2, 2, 25, 25));
            centerPanel.setPreferredSize(new Dimension(600, 280));

            JButton btnMyPayments = createDashboardButton("MY PAYMENTS", new Color(39, 174, 96));
            JButton btnMyVisitors = createDashboardButton("MY VISITORS", new Color(22, 160, 133));
            JButton btnMyChores = createDashboardButton("MY CHORES", new Color(142, 68, 173));
            JButton btnMyProfile = createDashboardButton("MY PROFILE", new Color(41, 128, 185));

            btnMyPayments.addActionListener(e -> { new PaymentFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnMyVisitors.addActionListener(e -> { new VisitorFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnMyChores.addActionListener(e -> { new ChoreFrame(loggedInUser).setVisible(true); this.dispose(); });
            btnMyProfile.addActionListener(e -> { new ProfileFrame(loggedInUser).setVisible(true); this.dispose(); });

            centerPanel.add(btnMyPayments);
            centerPanel.add(btnMyVisitors);
            centerPanel.add(btnMyChores);
            centerPanel.add(btnMyProfile);
        }

        JPanel mainCenter = new JPanel(new BorderLayout());
        mainCenter.setOpaque(false);
        mainCenter.add(statsPanel, BorderLayout.NORTH);
        mainCenter.add(wrapperPanel, BorderLayout.CENTER);

        getContentPane().add(mainCenter, BorderLayout.CENTER);
    }

    private JButton createDashboardButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, color),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(100, 100, 100));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblValue.setForeground(color);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private void btnLogoutActionPerformed() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Nimbus theme not found. Using default.");
        }

        java.awt.EventQueue.invokeLater(() -> {
            com.dormsync.model.User testUser = new com.dormsync.model.User(
                1, "Test User", "testuser@gmail.com", "12345", "9876543210", "USER"
            );
            new DashboardFrame(testUser).setVisible(true);
        });
    }
}