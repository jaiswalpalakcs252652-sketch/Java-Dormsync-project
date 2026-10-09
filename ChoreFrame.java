/*
 * ChoreFrame.java — Admin gets Kanban board, User gets card view.
 */
package com.dormsync.gui;

import com.dormsync.dao.ChoreDAO;
import com.dormsync.model.Chore;

import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ChoreFrame extends javax.swing.JFrame {

    private ChoreDAO choreDAO = new ChoreDAO();
    private DefaultTableModel tableModel;
    private List<Chore> choreList = new ArrayList<>();
    private com.dormsync.model.User loggedInUser;

    // Admin components
    private JTextField txtUserId;
    private JTextField txtChoreName;
    private JTextField txtDescription;
    private JTextField txtChoreDate;
    private JComboBox<String> cmbStatus;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnBack;

    // Admin Kanban columns
    private JPanel pendingColumn;
    private JPanel inProgressColumn;
    private JPanel completedColumn;

    // User components
    private JPanel userCardsContainer;
    private JPanel userSummaryPanel;

    private boolean isAdmin() {
        return loggedInUser != null && "ADMIN".equalsIgnoreCase(loggedInUser.getRole());
    }

    public ChoreFrame(com.dormsync.model.User user) {
        this.loggedInUser = user;

        setTitle(isAdmin() ? "DormSync - Chores" : "DormSync - My Chores");
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(236, 240, 241));
        getContentPane().setLayout(new BorderLayout());

        // HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80));
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        String frameTitle = isAdmin() ? "CHORE MANAGEMENT" : "MY CHORES";
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

        if (isAdmin()) {
            buildAdminKanbanView();
        } else {
            buildUserView();
        }

        loadChores();
    }

    // ══════════════════════════════════════════
    // ADMIN VIEW — Kanban + Add form
    // ══════════════════════════════════════════
    private void buildAdminKanbanView() {

        // Top: compact add form
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(15, 40, 5, 40));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(15, 25, 15, 25)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: User ID + Chore Name + Chore Date
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(styledLabel("User ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtUserId = styledTextField();
        formPanel.add(txtUserId, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(styledLabel("Chore Name:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        txtChoreName = styledTextField();
        formPanel.add(txtChoreName, gbc);

        gbc.gridx = 4; gbc.weightx = 0;
        formPanel.add(styledLabel("Due Date:"), gbc);
        gbc.gridx = 5; gbc.weightx = 1.0;
        txtChoreDate = styledTextField();
        formPanel.add(txtChoreDate, gbc);

        // Row 1: Description + Status
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(styledLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 3;
        txtDescription = styledTextField();
        formPanel.add(txtDescription, gbc);

        gbc.gridx = 4; gbc.gridwidth = 1; gbc.weightx = 0;
        formPanel.add(styledLabel("Status:"), gbc);
        gbc.gridx = 5; gbc.weightx = 1.0;
        cmbStatus = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(cmbStatus, gbc);

        // Row 2: Action buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(12, 0, 0, 0);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setOpaque(false);

        btnAdd = modernButton("ADD CHORE", new Color(41, 128, 185));
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
        getContentPane().add(formWrapper, BorderLayout.NORTH);

        // Kanban board (3 columns)
        JPanel kanbanWrapper = new JPanel(new GridLayout(1, 3, 15, 15));
        kanbanWrapper.setOpaque(false);
        kanbanWrapper.setBorder(new EmptyBorder(15, 40, 25, 40));

        pendingColumn = createKanbanColumn("PENDING", new Color(230, 126, 34));
        inProgressColumn = createKanbanColumn("IN PROGRESS", new Color(41, 128, 185));
        completedColumn = createKanbanColumn("COMPLETED", new Color(39, 174, 96));

        kanbanWrapper.add(pendingColumn);
        kanbanWrapper.add(inProgressColumn);
        kanbanWrapper.add(completedColumn);

        getContentPane().add(kanbanWrapper, BorderLayout.CENTER);
    }

    private JPanel createKanbanColumn(String title, Color headerColor) {
        JPanel column = new JPanel(new BorderLayout());
        column.setBackground(Color.WHITE);
        column.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(0, 0, 0, 0)));

        // Column header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerColor);
        header.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel lblHeader = new JLabel(title);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(Color.WHITE);
        header.add(lblHeader, BorderLayout.WEST);

        JLabel lblCount = new JLabel("0");
        lblCount.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCount.setForeground(Color.WHITE);
        lblCount.setName("count");
        header.add(lblCount, BorderLayout.EAST);

        column.add(header, BorderLayout.NORTH);

        // Card list container
        JPanel cardsArea = new JPanel();
        cardsArea.setLayout(new BoxLayout(cardsArea, BoxLayout.Y_AXIS));
        cardsArea.setBackground(new Color(250, 250, 250));
        cardsArea.setName("cardsArea");

        JScrollPane scroll = new JScrollPane(cardsArea);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        column.add(scroll, BorderLayout.CENTER);

        return column;
    }

    private void populateKanban() {
        if (pendingColumn == null) return;

        // Clear all columns
        JPanel pendingCards = (JPanel) ((JScrollPane) pendingColumn.getComponent(1)).getViewport().getView();
        JPanel inProgressCards = (JPanel) ((JScrollPane) inProgressColumn.getComponent(1)).getViewport().getView();
        JPanel completedCards = (JPanel) ((JScrollPane) completedColumn.getComponent(1)).getViewport().getView();

        pendingCards.removeAll();
        inProgressCards.removeAll();
        completedCards.removeAll();

        int pendingCount = 0, inProgressCount = 0, completedCount = 0;

        List<Object[]> rows = choreDAO.getAllChoresWithUser();
        for (Object[] row : rows) {
            // row: [chore_id, user_name, chore_name, description, chore_date, status]
            int choreId = (int) row[0];
            String userName = (String) row[1];
            String choreName = (String) row[2];
            String description = (String) row[3];
            String choreDate = (String) row[4];
            String status = (String) row[5];

            Chore chore = new Chore();
            chore.setChoreId(choreId);
            chore.setChoreName(choreName);
            chore.setDescription(description);
            chore.setChoreDate(choreDate);
            chore.setStatus(status);

            JPanel card = createKanbanCard(chore, userName);

            if ("Pending".equalsIgnoreCase(status)) {
                pendingCards.add(card);
                pendingCards.add(Box.createVerticalStrut(8));
                pendingCount++;
            } else if ("In Progress".equalsIgnoreCase(status)) {
                inProgressCards.add(card);
                inProgressCards.add(Box.createVerticalStrut(8));
                inProgressCount++;
            } else {
                completedCards.add(card);
                completedCards.add(Box.createVerticalStrut(8));
                completedCount++;
            }
        }

        // Update counts in headers
        updateCount(pendingColumn, pendingCount);
        updateCount(inProgressColumn, inProgressCount);
        updateCount(completedColumn, completedCount);

        pendingCards.revalidate(); pendingCards.repaint();
        inProgressCards.revalidate(); inProgressCards.repaint();
        completedCards.revalidate(); completedCards.repaint();
    }

    private void updateCount(JPanel column, int count) {
        JPanel header = (JPanel) column.getComponent(0);
        for (Component c : header.getComponents()) {
            if ("count".equals(c.getName())) {
                ((JLabel) c).setText(String.valueOf(count));
            }
        }
    }

    private JPanel createKanbanCard(Chore chore, String userName) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 12, 10, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Chore name
        JLabel lblName = new JLabel(chore.getChoreName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(44, 62, 80));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        // User + description
        String descText = chore.getDescription() == null || chore.getDescription().isEmpty()
                ? "—" : chore.getDescription();
        JLabel lblDesc = new JLabel("<html><div style='width:200px;'>" + descText + "</div></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(120, 120, 120));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("👤 " + userName);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUser.setForeground(new Color(100, 100, 100));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDate = new JLabel("📅 " + chore.getChoreDate());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDate.setForeground(new Color(150, 150, 150));
        lblDate.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action buttons row
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!"Completed".equalsIgnoreCase(chore.getStatus())) {
            JButton btnAdvance = new JButton("→");
            btnAdvance.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdvance.setBackground(new Color(41, 128, 185));
            btnAdvance.setForeground(Color.WHITE);
            btnAdvance.setFocusPainted(false);
            btnAdvance.setBorder(new EmptyBorder(3, 10, 3, 10));
            btnAdvance.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnAdvance.setToolTipText("Move forward");
            btnAdvance.addActionListener(e -> advanceChore(chore));
            actionRow.add(btnAdvance);
        }

        JButton btnDelete = new JButton("🗑");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDelete.setBackground(new Color(192, 57, 43));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setBorder(new EmptyBorder(3, 8, 3, 8));
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.setToolTipText("Delete chore");
        btnDelete.addActionListener(e -> quickDelete(chore));
        actionRow.add(btnDelete);

        card.add(lblName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblDesc);
        card.add(Box.createVerticalStrut(4));
        card.add(lblUser);
        card.add(Box.createVerticalStrut(2));
        card.add(lblDate);
        card.add(Box.createVerticalStrut(6));
        card.add(actionRow);

        return card;
    }

    private void advanceChore(Chore chore) {
        String newStatus;
        if ("Pending".equalsIgnoreCase(chore.getStatus())) {
            newStatus = "In Progress";
        } else if ("In Progress".equalsIgnoreCase(chore.getStatus())) {
            newStatus = "Completed";
        } else {
            return;
        }

        chore.setStatus(newStatus);

        if (choreDAO.updateChore(chore)) {
            populateKanban();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update chore status.");
        }
    }

    private void quickDelete(Chore chore) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete chore \"" + chore.getChoreName() + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (choreDAO.deleteChore(chore.getChoreId())) {
            populateKanban();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete chore.");
        }
    }

    // ══════════════════════════════════════════
    // USER VIEW — summary tiles + card list
    // ══════════════════════════════════════════
    private void buildUserView() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        userSummaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        userSummaryPanel.setOpaque(false);
        userSummaryPanel.setBorder(new EmptyBorder(20, 40, 10, 40));
        userSummaryPanel.add(createSummaryTile("Total Chores", "0",  new Color(142, 68, 173)));
        userSummaryPanel.add(createSummaryTile("Pending",      "0",  new Color(230, 126, 34)));
        userSummaryPanel.add(createSummaryTile("Completed",    "0",  new Color(39, 174, 96)));

        mainPanel.add(userSummaryPanel, BorderLayout.NORTH);

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

    private JPanel createChoreCard(Chore c) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 15, 20)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel lblName = new JLabel("🧹 " + c.getChoreName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(new Color(44, 62, 80));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        String desc = c.getDescription() == null || c.getDescription().isEmpty() ? "—" : c.getDescription();
        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(new Color(120, 120, 120));
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(lblName);
        left.add(Box.createVerticalStrut(4));
        left.add(lblDesc);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel lblDate = new JLabel("📅 " + c.getChoreDate());
        lblDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDate.setForeground(new Color(120, 120, 120));
        lblDate.setAlignmentX(Component.RIGHT_ALIGNMENT);

        Color statusColor;
        if ("Completed".equalsIgnoreCase(c.getStatus()))
            statusColor = new Color(39, 174, 96);
        else if ("In Progress".equalsIgnoreCase(c.getStatus()))
            statusColor = new Color(41, 128, 185);
        else
            statusColor = new Color(230, 126, 34);

        JLabel lblStatus = new JLabel("● " + c.getStatus());
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
    private JLabel styledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JTextField styledTextField() {
        JTextField field = new JTextField(12);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(6, 8, 6, 8)));
        return field;
    }

    private JButton modernButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ══════════════════════════════════════════
    // ADMIN ACTIONS
    // ══════════════════════════════════════════
    private void btnAddActionPerformed() {
        String userIdText = txtUserId.getText().trim();
        String choreName = txtChoreName.getText().trim();
        String description = txtDescription.getText().trim();
        String choreDate = txtChoreDate.getText().trim();
        String status = cmbStatus.getSelectedItem().toString();

        if (userIdText.isEmpty() || choreName.isEmpty() || choreDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText);
            Chore chore = new Chore();
            chore.setUserId(userId);
            chore.setChoreName(choreName);
            chore.setDescription(description);
            chore.setChoreDate(choreDate);
            chore.setStatus(status);

            if (choreDAO.addChore(chore)) {
                JOptionPane.showMessageDialog(this, "Chore added successfully!");
                loadChores();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add chore.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "User ID must be a number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnUpdateActionPerformed() {
        // Update uses form values + first chore name match
        // (table row-based update not used in Kanban view)
        if (txtChoreName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a chore name first.");
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Tip: use the → button on a card to advance its status.\n" +
                "For full edit, use the delete → add flow.");
    }

    private void btnDeleteActionPerformed() {
        JOptionPane.showMessageDialog(this,
                "Tip: use the 🗑 button on a card to delete it directly.");
    }

    private void btnClearActionPerformed() {
        clearFields();
    }

    private void btnBackActionPerformed() {
        new DashboardFrame(loggedInUser).setVisible(true);
        this.dispose();
    }

    // ══════════════════════════════════════════
    // LOAD DATA
    // ══════════════════════════════════════════
    private void loadChores() {

        // ADMIN — populate Kanban
        if (isAdmin()) {
            populateKanban();
            return;
        }

        // USER — card list + summary
        choreList = choreDAO.getChoresByUser(loggedInUser.getUserId());

        if (userCardsContainer == null) return;

        userCardsContainer.removeAll();

        int total = choreList.size();
        int pending = 0, completed = 0;
        for (Chore c : choreList) {
            if ("Pending".equalsIgnoreCase(c.getStatus())) pending++;
            if ("Completed".equalsIgnoreCase(c.getStatus())) completed++;
        }

        userSummaryPanel.removeAll();
        userSummaryPanel.add(createSummaryTile("Total Chores", String.valueOf(total),     new Color(142, 68, 173)));
        userSummaryPanel.add(createSummaryTile("Pending",      String.valueOf(pending),   new Color(230, 126, 34)));
        userSummaryPanel.add(createSummaryTile("Completed",    String.valueOf(completed), new Color(39, 174, 96)));

        if (choreList.isEmpty()) {
            JLabel empty = new JLabel("You have no chores yet.");
            empty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            empty.setForeground(new Color(120, 120, 120));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            userCardsContainer.add(empty);
        } else {
            for (Chore c : choreList) {
                userCardsContainer.add(createChoreCard(c));
                userCardsContainer.add(Box.createVerticalStrut(10));
            }
        }

        userCardsContainer.revalidate();
        userCardsContainer.repaint();
        userSummaryPanel.revalidate();
        userSummaryPanel.repaint();
    }

    private void clearFields() {
        if (txtUserId != null) txtUserId.setText("");
        if (txtChoreName != null) txtChoreName.setText("");
        if (txtDescription != null) txtDescription.setText("");
        if (txtChoreDate != null) txtChoreDate.setText("");
        if (cmbStatus != null) cmbStatus.setSelectedIndex(0);
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
            com.dormsync.model.User testUser = new com.dormsync.model.User(
                2, "Test Admin", "admin@test.com", "1234", "9999999999", "ADMIN");
            new ChoreFrame(testUser).setVisible(true);
        });
    }
}