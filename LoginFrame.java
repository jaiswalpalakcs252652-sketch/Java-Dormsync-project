/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends javax.swing.JFrame {

    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;

    public LoginFrame() {
        // 1. Basic Frame Setup
        setTitle("DormSync Login");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Centers the card
        getContentPane().setBackground(new Color(236, 240, 241)); // Soft Light Gray

        // ==========================================
        // 2. CREATE THE WHITE CARD PANEL
        // ==========================================
        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ==========================================
        // 3. TITLE
        // ==========================================
        JLabel titleLabel = new JLabel("DORMSYNC LOGIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80)); // Navy
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        cardPanel.add(titleLabel, gbc);

        // ==========================================
        // 4. EMAIL LABEL & FIELD
        // ==========================================
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(emailLabel, gbc);

        txtEmail = new JTextField(20);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtEmail, gbc);

        // ==========================================
        // 5. PASSWORD LABEL & FIELD
        // ==========================================
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(passLabel, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 10, 8, 10)
        ));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 25, 0);
        cardPanel.add(txtPassword, gbc);

        // ==========================================
        // 6. LOGIN BUTTON
        // ==========================================
        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(41, 128, 185)); // Blue
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(new EmptyBorder(10, 20, 10, 20));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> btnLoginActionPerformed());
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 0);
        cardPanel.add(btnLogin, gbc);

        // ==========================================
        // 7. REGISTER BUTTON
        // ==========================================
        btnRegister = new JButton("REGISTER");
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRegister.setBackground(new Color(236, 240, 241)); // Light Gray
        btnRegister.setForeground(new Color(50, 50, 50));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.addActionListener(e -> btnRegisterActionPerformed());
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(btnRegister, gbc);

        // Add the white card to the center of the gray frame
        add(cardPanel);
    }

    // ==========================================
    // LOGIN ACTION (Keeps your Database Logic)
    // ==========================================
    private void btnLoginActionPerformed() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.");
            return;
        }

        com.dormsync.dao.UserDAO userDAO = new com.dormsync.dao.UserDAO();
com.dormsync.model.User user = userDAO.loginUser(email, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            
            // Open the modern Dashboard and pass the user
            DashboardFrame dashboard = new DashboardFrame(user);
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.");
        }
    }

    // ==========================================
    // REGISTER ACTION
    // ==========================================
    private void btnRegisterActionPerformed() {
        RegisterFrame register = new RegisterFrame();
        register.setVisible(true);
        this.dispose();
    }

    // ==========================================
    // MAIN METHOD
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

        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}