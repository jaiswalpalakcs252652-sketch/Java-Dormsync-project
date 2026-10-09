/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomAllocationFrame extends javax.swing.JFrame {

    private com.dormsync.model.User loggedInUser;

    private JComboBox<String> cmbUser;
    private JComboBox<String> cmbRoom;
    private JTextField txtAllocationDate;
    private JComboBox<String> cmbStatus;
    private JTable tblAllocations;
    private JButton btnAllocate, btnDelete, btnRefresh, btnBack;

    private List<Integer> roomIdList = new ArrayList<>();
    private List<Integer> allocationIdList = new ArrayList<>();

    public RoomAllocationFrame(com.dormsync.model.User user) {
        this.loggedInUser = user;

        // ============ FRAME SETUP ============
        setTitle("DormSync - Room Allocation");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // ============ HEADER ============
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("ROOM ALLOCATION");
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

        // ============ FORM CARD ============
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(20, 40, 10, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: User + Room
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(styledLabel("User:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cmbUser = new JComboBox<>();
        cmbUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbUser.setBackground(Color.WHITE);
        formPanel.add(cmbUser, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Room:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cmbRoom = new JComboBox<>();
        cmbRoom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRoom.setBackground(Color.WHITE);
        formPanel.add(cmbRoom, gbc);

        // Row 1: Date + Status
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(styledLabel("Allocation Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtAllocationDate = styledTextField();
        formPanel.add(txtAllocationDate, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Status:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setBackground(Color.WHITE);
        formPanel.add(cmbStatus, gbc);

        // Row 2: Buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        btnAllocate = modernButton("ALLOCATE ROOM", new Color(41, 128, 185));
        btnDelete = modernButton("DELETE", new Color(192, 57, 43));
        btnRefresh = modernButton("REFRESH", new Color(39, 174, 96));

        btnAllocate.addActionListener(e -> btnAllocateActionPerformed());
        btnDelete.addActionListener(e -> btnDeleteActionPerformed());
        btnRefresh.addActionListener(e -> {
            loadUsers();
            loadRooms();
            loadAllocations();
        });

        buttonPanel.add(btnAllocate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        formPanel.add(buttonPanel, gbc);

        formWrapper.add(formPanel, BorderLayout.CENTER);
        getContentPane().add(formWrapper, BorderLayout.CENTER);

        // ============ TABLE ============
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(10, 40, 25, 40));

        JLabel lblTable = new JLabel("Allocation Records");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTable.setForeground(new Color(44, 62, 80));
        lblTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableWrapper.add(lblTable, BorderLayout.NORTH);

        tblAllocations = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblAllocations);

        tblAllocations.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblAllocations.setRowHeight(30);
        tblAllocations.setGridColor(new Color(220, 220, 220));
        tblAllocations.setShowVerticalLines(false);
        tblAllocations.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblAllocations.getTableHeader().setBackground(new Color(44, 62, 80));
        tblAllocations.getTableHeader().setForeground(Color.WHITE);
        tblAllocations.getTableHeader().setPreferredSize(new Dimension(0, 35));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(tableWrapper, BorderLayout.SOUTH);

        // ============ INITIAL LOAD ============
        loadUsers();
        loadRooms();
        loadAllocations();
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

    // ============ LOAD USERS ============
    private void loadUsers() {
        cmbUser.removeAllItems();
        String sql = "SELECT user_id, name FROM users ORDER BY name";
        try (java.sql.Connection con = com.dormsync.util.DBConnection.getConnection();
             java.sql.PreparedStatement pst = con.prepareStatement(sql);
             java.sql.ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                cmbUser.addItem(rs.getInt("user_id") + " - " + rs.getString("name"));
            }
        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + e.getMessage());
        }
    }

    // ============ LOAD ROOMS (only Shared / Double / Single) ============
    private void loadRooms() {
        cmbRoom.removeAllItems();
        roomIdList.clear();

        String sql = "SELECT MIN(room_id) AS room_id, room_type " +
                     "FROM rooms WHERE status = 'Available' " +
                     "GROUP BY room_type ORDER BY room_type";

        try (java.sql.Connection con = com.dormsync.util.DBConnection.getConnection();
             java.sql.PreparedStatement pst = con.prepareStatement(sql);
             java.sql.ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                cmbRoom.addItem(rs.getString("room_type"));   // Display only type
                roomIdList.add(rs.getInt("room_id"));         // Hidden room_id
            }

            if (cmbRoom.getItemCount() == 0) {
                cmbRoom.addItem("No available rooms");
            }

        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load rooms: " + e.getMessage());
        }
    }

    // ============ LOAD ALLOCATIONS ============
    private void loadAllocations() {
        String[] columns = {"Allocation ID", "User ID", "Room ID", "Allocation Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblAllocations.setModel(model);

        allocationIdList.clear();

        com.dormsync.dao.RoomAllocationDAO dao = new com.dormsync.dao.RoomAllocationDAO();
        List<com.dormsync.model.RoomAllocation> allocations = dao.getAllAllocations();

        for (com.dormsync.model.RoomAllocation a : allocations) {
            model.addRow(new Object[]{
                a.getAllocationId(),
                a.getUserId(),
                a.getRoomId(),
                a.getAllocationDate(),
                a.getStatus()
            });
            allocationIdList.add(a.getAllocationId());
        }
    }

    // ============ ALLOCATE ROOM ============
    private void btnAllocateActionPerformed() {
        String selectedUser = (String) cmbUser.getSelectedItem();
        int selectedRoomIndex = cmbRoom.getSelectedIndex();
        String dateText = txtAllocationDate.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (selectedUser == null || selectedRoomIndex == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user and room.");
            return;
        }

        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter allocation date.");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(selectedUser.substring(0, selectedUser.indexOf(" - ")));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid user selection.");
            return;
        }

        int roomId;
        try {
            roomId = roomIdList.get(selectedRoomIndex);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No room selected.");
            return;
        }

        java.sql.Date allocationDate;
        try {
            allocationDate = java.sql.Date.valueOf(dateText);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format.");
            return;
        }

        com.dormsync.model.RoomAllocation allocation = new com.dormsync.model.RoomAllocation(
                userId, roomId, allocationDate, status
        );

        com.dormsync.dao.RoomAllocationDAO dao = new com.dormsync.dao.RoomAllocationDAO();
        boolean result = dao.addAllocation(allocation);

        if (result) {
            JOptionPane.showMessageDialog(this, "Room allocated successfully!");
            txtAllocationDate.setText("");
            loadAllocations();
            loadRooms();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to allocate room.");
        }
    }

    // ============ DELETE ALLOCATION (Complete) ============
    private void btnDeleteActionPerformed() {
        int selectedRow = tblAllocations.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an allocation from the table to delete.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int allocationId = allocationIdList.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete allocation ID " + allocationId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        com.dormsync.dao.RoomAllocationDAO dao = new com.dormsync.dao.RoomAllocationDAO();
        boolean success = dao.deleteAllocation(allocationId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Allocation deleted successfully!");
            loadAllocations();
            loadRooms();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete allocation.");
        }
    }

    // ============ BACK ============
    private void btnBackActionPerformed() {
        new DashboardFrame(loggedInUser).setVisible(true);
        this.dispose();
    }

    // ============ MAIN ============
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

        java.awt.EventQueue.invokeLater(() -> new RoomAllocationFrame(null).setVisible(true));
    }
}