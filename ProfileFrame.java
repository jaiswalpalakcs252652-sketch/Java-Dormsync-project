/*
 * ProfileFrame.java
 * Lets the logged-in user (admin or regular) edit their own profile.
 */
package com.dormsync.gui;

import com.dormsync.dao.UserDAO;
import com.dormsync.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfileFrame extends javax.swing.JFrame {

    private User loggedInUser;
    private UserDAO userDAO = new UserDAO();

    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JPasswordField txtPassword;

    public ProfileFrame(User user) {
        this.loggedInUser = user;

        setTitle("DormSync - My Profile");
        setSize(850, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel lblTitle = new JLabel("MY PROFILE");
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

        // FORM CARD (centered)
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(40, 50, 40, 50)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblCardTitle = new JLabel("Edit Your Information");
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCardTitle.setForeground(new Color(44, 62, 80));
        lblCardTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 25, 0);
        cardPanel.add(lblCardTitle, gbc);

        JLabel lblRole = new JLabel("Role: " + loggedInUser.getRole());
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(120, 120, 120));
        lblRole.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        cardPanel.add(lblRole, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        cardPanel.add(styledLabel("Full Name:"), gbc);

        txtName = styledTextField(loggedInUser.getName());
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtName, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 5, 0);
        cardPanel.add(styledLabel("Email (cannot be changed):"), gbc);

        txtEmail = styledTextField(loggedInUser.getEmail());
        txtEmail.setEditable(false);
        txtEmail.setBackground(new Color(245, 245, 245));
        txtEmail.setForeground(new Color(120, 120, 120));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtEmail, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(5, 0, 5, 0);
        cardPanel.add(styledLabel("Phone:"), gbc);

        txtPhone = styledTextField(loggedInUser.getPhone());
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(txtPhone, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(5, 0, 5, 0);
        cardPanel.add(styledLabel("New Password (leave blank to keep current):"), gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(8, 10, 8, 10)));
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 25, 0);
        cardPanel.add(txtPassword, gbc);

        JButton btnSave = modernButton("SAVE CHANGES", new Color(41, 128, 185));
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBorder(new EmptyBorder(12, 30, 12, 30));
        btnSave.addActionListener(e -> saveProfile());
        gbc.gridy = 10;
        gbc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(btnSave, gbc);

        wrapper.add(cardPanel);
        getContentPane().add(wrapper, BorderLayout.CENTER);
    }

    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lbl;
    }

    private JTextField styledTextField(String value) {
        JTextField field = new JTextField(value == null ? "" : value, 20);
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

    private void saveProfile() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String newPass = new String(txtPassword.getPassword());

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.");
            return;
        }

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone cannot be empty.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Save these changes to your profile?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = userDAO.updateUserProfile(
                loggedInUser.getUserId(), name, phone, newPass);

        if (success) {
            loggedInUser.setName(name);
            loggedInUser.setPhone(phone);
            if (!newPass.trim().isEmpty()) {
                loggedInUser.setPassword(newPass.trim());
            }

            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update profile. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        } catch (Exception ex) {}

        java.awt.EventQueue.invokeLater(() -> {
            User testUser = new User(1, "Test User",
                    "test@test.com", "1234", "9999999999", "USER");
            new ProfileFrame(testUser).setVisible(true);
        });
    }
}