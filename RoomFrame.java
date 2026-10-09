/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class RoomFrame extends javax.swing.JFrame {

    private com.dormsync.model.User loggedInUser;  // ✅ NEW

    private JTextField txtRoomNumber;
    private JTextField txtCapacity;
    private JComboBox<String> cmbRoomType;
    private JComboBox<String> cmbStatus;
    private JPanel cardsContainer;

    // ✅ NEW: constructor now accepts User
    public RoomFrame(com.dormsync.model.User user) {
        this.loggedInUser = user;

        setTitle("DormSync - Room Details");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // ============ HEADER ============
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("ROOM DETAILS");
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
        // ✅ FIXED: reopen dashboard instead of just closing
        btnBack.addActionListener(e -> {
            new DashboardFrame(loggedInUser).setVisible(true);
            this.dispose();
        });
        headerPanel.add(btnBack, BorderLayout.EAST);

        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // ============ FORM ============
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
        formPanel.add(styledLabel("Room Number:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtRoomNumber = styledTextField();
        formPanel.add(txtRoomNumber, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Room Type:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cmbRoomType = new JComboBox<>(new String[]{"Single", "Double", "Shared"});
        cmbRoomType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cmbRoomType, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(styledLabel("Capacity:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtCapacity = styledTextField();
        formPanel.add(txtCapacity, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cmbStatus = new JComboBox<>(new String[]{"Available", "Occupied"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cmbStatus, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);

        JButton btnSave = modernButton("SAVE ROOM", new Color(41, 128, 185));
        btnSave.addActionListener(e -> saveRoom());

        JButton btnRefresh = modernButton("REFRESH", new Color(39, 174, 96));
        btnRefresh.addActionListener(e -> loadRooms());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(btnSave);
        btnPanel.add(btnRefresh);
        formPanel.add(btnPanel, gbc);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        getContentPane().add(formWrapper, BorderLayout.CENTER);

        // ============ CARD GRID ============
        cardsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsContainer.setBackground(new Color(236, 240, 241));

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(new EmptyBorder(0, 40, 25, 40));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setPreferredSize(new Dimension(0, 350));

        getContentPane().add(scroll, BorderLayout.SOUTH);

        loadRooms();
    }

    // ============ CREATE A ROOM CARD ============
    private JPanel createRoomCard(com.dormsync.model.Room room) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(240, 200));
        card.setMaximumSize(new Dimension(240, 200));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lblNum = new JLabel("Room " + room.getRoomNumber());
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNum.setForeground(new Color(44, 62, 80));
        lblNum.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblType = new JLabel(room.getRoomType() + " Room");
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblType.setForeground(new Color(100, 100, 100));
        lblType.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStatus = new JLabel("● " + room.getStatus());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(room.getStatus().equalsIgnoreCase("Available")
                ? new Color(39, 174, 96)
                : new Color(192, 57, 43));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCapacity = new JLabel("Capacity: " + room.getCapacity() + " person(s)");
        lblCapacity.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCapacity.setForeground(new Color(80, 80, 80));
        lblCapacity.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnDelete = new JButton("DELETE");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDelete.setBackground(new Color(192, 57, 43));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete room " + room.getRoomNumber() + "?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                com.dormsync.dao.RoomDAO dao = new com.dormsync.dao.RoomDAO();
                if (dao.deleteRoom(room.getRoomId())) {
                    JOptionPane.showMessageDialog(this, "Room deleted!");
                    loadRooms();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete this room — it may have active allocations.",
                            "Delete Failed", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        card.add(lblNum);
        card.add(Box.createVerticalStrut(5));
        card.add(lblType);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatus);
        card.add(Box.createVerticalStrut(5));
        card.add(lblCapacity);
        card.add(Box.createVerticalStrut(15));
        card.add(btnDelete);

        return card;
    }

    // ============ LOAD ROOMS ============
    private void loadRooms() {
        cardsContainer.removeAll();
        com.dormsync.dao.RoomDAO dao = new com.dormsync.dao.RoomDAO();
        List<com.dormsync.model.Room> rooms = dao.getAllRooms();

        if (rooms.isEmpty()) {
            JLabel empty = new JLabel("No rooms added yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(120, 120, 120));
            cardsContainer.add(empty);
        } else {
            for (com.dormsync.model.Room r : rooms) {
                cardsContainer.add(createRoomCard(r));
            }
        }
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    // ============ SAVE ROOM ============
    private void saveRoom() {
        String roomNumber = txtRoomNumber.getText().trim();
        String roomType = cmbRoomType.getSelectedItem().toString();
        String status = cmbStatus.getSelectedItem().toString();

        if (roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter room number.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(txtCapacity.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a number.");
            return;
        }
        if (capacity <= 0) {
            JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.");
            return;
        }

        com.dormsync.model.Room room = new com.dormsync.model.Room(
                roomNumber, roomType, capacity, status);

        com.dormsync.dao.RoomDAO dao = new com.dormsync.dao.RoomDAO();
        if (dao.addRoom(room)) {
            JOptionPane.showMessageDialog(this, "Room added successfully!");
            txtRoomNumber.setText("");
            txtCapacity.setText("");
            loadRooms();
        } else {
            JOptionPane.showMessageDialog(this, "Failed. Room number may already exist.");
        }
    }

    // ============ STYLE HELPERS ============
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

    // ✅ FIXED: main method now passes a user
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
                1, "Test Admin", "admin@test.com", "1234", "9999999999", "ADMIN"
            );
            new RoomFrame(testUser).setVisible(true);
        });
    }
}