/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dormsync.gui;

import com.dormsync.dao.ExpenseDAO;
import com.dormsync.model.Expense;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ExpenseFrame extends javax.swing.JFrame {

    private com.dormsync.model.User loggedInUser;

    private ExpenseDAO expenseDAO = new ExpenseDAO();
    private DefaultTableModel tableModel;
    private List<Expense> expenseList = new ArrayList<>();

    // --- Declare all components ---
    private JTextField txtDescription;
    private JTextField txtAmount;
    private JTextField txtPaidBy;
    private JTextField txtCategory;
    private JTextField txtExpenseDate;
    private JTable tblExpenses;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnBack;

    public ExpenseFrame(com.dormsync.model.User user) {
        this.loggedInUser = user;
        
        // 1. Basic Frame Setup
        setTitle("DormSync - Expenses");
        setSize(1000, 750);
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

        JLabel lblTitle = new JLabel("EXPENSE MANAGEMENT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
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
        // 4. FORM PANEL (White Card)
        // ==========================================
        JPanel formWrapper = new JPanel();
        formWrapper.setOpaque(false);
        formWrapper.setLayout(new BorderLayout());
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

        // Row 0: Description
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(styledLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDescription = styledTextField();
        formPanel.add(txtDescription, gbc);

        // Row 0 (Right): Amount
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Amount:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtAmount = styledTextField();
        formPanel.add(txtAmount, gbc);

        // Row 1: Paid By
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(styledLabel("Paid By:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPaidBy = styledTextField();
        formPanel.add(txtPaidBy, gbc);

        // Row 1 (Right): Category
        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Category:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtCategory = styledTextField();
        formPanel.add(txtCategory, gbc);

        // Row 2: Expense Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Expense Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3;
        txtExpenseDate = styledTextField();
        formPanel.add(txtExpenseDate, gbc);

        // Row 3: Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 0, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        btnAdd = modernButton("ADD EXPENSE", new Color(41, 128, 185));     // Blue
        btnUpdate = modernButton("UPDATE", new Color(230, 126, 34));       // Orange
        btnDelete = modernButton("DELETE", new Color(192, 57, 43));        // Red
        btnClear = modernButton("CLEAR", new Color(127, 140, 141));        // Gray

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

        // ==========================================
        // 5. TABLE PANEL (Bottom)
        // ==========================================
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(10, 40, 25, 40));

        JLabel lblTable = new JLabel("Expense History");
        lblTable.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTable.setForeground(new Color(44, 62, 80));
        lblTable.setBorder(new EmptyBorder(0, 0, 10, 0));
        tableWrapper.add(lblTable, BorderLayout.NORTH);

        tblExpenses = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblExpenses);

        // Apply modern table styling
        tblExpenses.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblExpenses.setRowHeight(30);
        tblExpenses.setGridColor(new Color(220, 220, 220));
        tblExpenses.setShowVerticalLines(false);
        tblExpenses.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblExpenses.getTableHeader().setBackground(new Color(44, 62, 80));
        tblExpenses.getTableHeader().setForeground(Color.WHITE);
        tblExpenses.getTableHeader().setPreferredSize(new Dimension(0, 35));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Mouse click to load row into the form
        tblExpenses.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblExpensesMouseClicked(evt);
            }
        });

        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(tableWrapper, BorderLayout.SOUTH);

        // Load data on startup
        loadExpenses();
    }

    // ==========================================
    // HELPER: Style labels, fields, buttons
    // ==========================================
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

    // ==========================================
    // ACTIONS (Your original DB logic preserved)
    // ==========================================
    private void btnAddActionPerformed() {
        String description = txtDescription.getText().trim();
        String amountText = txtAmount.getText().trim();
        String paidBy = txtPaidBy.getText().trim();
        String category = txtCategory.getText().trim();
        String expenseDate = txtExpenseDate.getText().trim();

        if (description.isEmpty() || amountText.isEmpty() || paidBy.isEmpty()
                || category.isEmpty() || expenseDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            Expense expense = new Expense();
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setPaidBy(paidBy);
            expense.setCategory(category);
            expense.setExpenseDate(expenseDate);

            boolean success = expenseDAO.addExpense(expense);

            if (success) {
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
                loadExpenses();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add expense.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnUpdateActionPerformed() {
        int selectedRow = tblExpenses.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense from the table.");
            return;
        }

        try {
            Expense selectedExpense = expenseList.get(selectedRow);
            int expenseId = selectedExpense.getExpenseId();

            String description = txtDescription.getText().trim();
            String amountText = txtAmount.getText().trim();
            String paidBy = txtPaidBy.getText().trim();
            String category = txtCategory.getText().trim();
            String expenseDate = txtExpenseDate.getText().trim();

            if (description.isEmpty() || amountText.isEmpty() || paidBy.isEmpty()
                    || category.isEmpty() || expenseDate.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            double amount = Double.parseDouble(amountText);

            Expense expense = new Expense();
            expense.setExpenseId(expenseId);
            expense.setDescription(description);
            expense.setAmount(amount);
            expense.setPaidBy(paidBy);
            expense.setCategory(category);
            expense.setExpenseDate(expenseDate);

            boolean success = expenseDAO.updateExpense(expense);

            if (success) {
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
                loadExpenses();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnDeleteActionPerformed() {
        int selectedRow = tblExpenses.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an expense from the table.");
            return;
        }

        Expense selectedExpense = expenseList.get(selectedRow);
        int expenseId = selectedExpense.getExpenseId();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean success = expenseDAO.deleteExpense(expenseId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
            loadExpenses();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete expense.");
        }
    }

    private void btnClearActionPerformed() {
        clearFields();
    }

    private void tblExpensesMouseClicked(java.awt.event.MouseEvent evt) {
        int selectedRow = tblExpenses.getSelectedRow();
        if (selectedRow != -1) {
            Expense expense = expenseList.get(selectedRow);
            txtDescription.setText(expense.getDescription());
            txtAmount.setText(String.valueOf(expense.getAmount()));
            txtPaidBy.setText(expense.getPaidBy());
            txtCategory.setText(expense.getCategory());
            txtExpenseDate.setText(expense.getExpenseDate());
        }
    }

    private void btnBackActionPerformed() {
        new DashboardFrame(loggedInUser).setVisible(true);
        this.dispose();
    }

    // ==========================================
    // LOAD DATA
    // ==========================================
    private void loadExpenses() {
        String[] columns = {"Description", "Amount", "Paid By", "Category", "Expense Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tblExpenses.setModel(tableModel);

        expenseList = expenseDAO.getAllExpenses();

        for (Expense expense : expenseList) {
            tableModel.addRow(new Object[]{
                expense.getDescription(),
                expense.getAmount(),
                expense.getPaidBy(),
                expense.getCategory(),
                expense.getExpenseDate()
            });
        }
    }

    private void clearFields() {
        txtDescription.setText("");
        txtAmount.setText("");
        txtPaidBy.setText("");
        txtCategory.setText("");
        txtExpenseDate.setText("");
        tblExpenses.clearSelection();
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
            System.out.println("Nimbus theme not found.");
        }

        // Test with a dummy user
        java.awt.EventQueue.invokeLater(() -> {
            com.dormsync.model.User testUser = new com.dormsync.model.User(
                1, "Test User", "testuser@gmail.com", "12345", "9876543210", "USER"
            );
            new ExpenseFrame(testUser).setVisible(true);
        });
    }
}