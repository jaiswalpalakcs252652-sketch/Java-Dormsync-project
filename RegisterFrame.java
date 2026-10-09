/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends javax.swing.JFrame {

    // --- Components ---
    private JTextField txtName;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JTextField txtPhone;
    private JButton btnRegister, btnBack;

    public RegisterFrame() {

        // 1. Basic Frame Setup
        setTitle("DormSync - Registration");
        setSize(850, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(236, 240, 241));

        // ==========================================
        // 2. TOP HEADER (Placed inside the card)
        // ==========================================
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("DORMSYNC REGISTRATION");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        cardPanel.add(titleLabel, gbc);

        // ==========================================
        // 3. FORM FIELDS
        // ==========================================
        // Name
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(styledLabel("Full Name:"), gbc);

        txtName = styledTextField();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtName, gbc);

        // Email
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(styledLabel("Email:"), gbc);

        txtEmail = styledTextField();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtEmail, gbc);

        // Password
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(styledLabel("Password:"), gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtPassword, gbc);

        // Phone
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(styledLabel("Phone:"), gbc);

        txtPhone = styledTextField();
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 25, 0);
        cardPanel.add(txtPhone, gbc);

        // ==========================================
        // 4. BUTTONS
        // ==========================================
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 10, 0);

        btnRegister = modernButton("REGISTER", new Color(41, 128, 185));
        btnRegister.addActionListener(e -> btnRegisterActionPerformed());
        cardPanel.add(btnRegister, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);

        btnBack = modernButton("BACK TO LOGIN", new Color(127, 140, 141));
        btnBack.addActionListener(e -> btnBackActionPerformed());
        cardPanel.add(btnBack, gbc);

        // Add the card to the frame
        add(cardPanel);
    }

    // ==========================================
    // STYLE HELPERS
    // ==========================================
    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lbl;
    }

    private JTextField styledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 10, 8, 10)));
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

    // ==========================================
    // REGISTER ACTION (DB logic preserved)
    // ==========================================
    private void btnRegisterActionPerformed() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String phone = txtPhone.getText().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.");
            return;
        }

       
com.dormsync.model.User user = new com.dormsync.model.User(
        name, email, password, phone, "USER"
);

        com.dormsync.dao.UserDAO userDAO = new com.dormsync.dao.UserDAO();
        boolean result = userDAO.registerUser(user);

        if (result) {
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed. Email may already exist.");
        }
    }

    // ==========================================
    // BACK TO LOGIN ACTION
    // ==========================================
    private void btnBackActionPerformed() {
        new LoginFrame().setVisible(true);
        this.dispose();
    }

    // ==========================================
    // MAIN METHOD (For testing)
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

        java.awt.EventQueue.invokeLater(() -> new RegisterFrame().setVisible(true));
    }
}