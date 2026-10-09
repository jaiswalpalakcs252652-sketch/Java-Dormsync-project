package com.dormsync.dao;

import com.dormsync.model.Expense;
import com.dormsync.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDAO {

    // =========================
    // ADD EXPENSE
    // =========================
    public boolean addExpense(Expense expense) {

        String sql = "INSERT INTO expenses "
                + "(description, amount, paid_by, category, expense_date) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, expense.getDescription());
            ps.setDouble(2, expense.getAmount());
            ps.setString(3, expense.getPaidBy());
            ps.setString(4, expense.getCategory());
            ps.setString(5, expense.getExpenseDate());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Expense Insert Failed!");
            e.printStackTrace();

            return false;
        }
    }


    // =========================
    // GET ALL EXPENSES
    // =========================
    public List<Expense> getAllExpenses() {

        List<Expense> expenses = new ArrayList<>();

        String sql = "SELECT * FROM expenses ORDER BY expense_id DESC";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Expense expense = new Expense();

                expense.setExpenseId(rs.getInt("expense_id"));
                expense.setDescription(rs.getString("description"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setPaidBy(rs.getString("paid_by"));
                expense.setCategory(rs.getString("category"));
                expense.setExpenseDate(rs.getString("expense_date"));

                expenses.add(expense);
            }

        } catch (Exception e) {

            System.out.println("Failed to load expenses!");
            e.printStackTrace();
        }

        return expenses;
    }


    // =========================
    // UPDATE EXPENSE
    // =========================
    public boolean updateExpense(Expense expense) {

        String sql = "UPDATE expenses SET "
                + "description = ?, "
                + "amount = ?, "
                + "paid_by = ?, "
                + "category = ?, "
                + "expense_date = ? "
                + "WHERE expense_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, expense.getDescription());
            ps.setDouble(2, expense.getAmount());
            ps.setString(3, expense.getPaidBy());
            ps.setString(4, expense.getCategory());
            ps.setString(5, expense.getExpenseDate());
            ps.setInt(6, expense.getExpenseId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Expense Update Failed!");
            e.printStackTrace();

            return false;
        }
    }


    // =========================
    // DELETE EXPENSE
    // =========================
    public boolean deleteExpense(int expenseId) {

        String sql = "DELETE FROM expenses WHERE expense_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, expenseId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Expense Delete Failed!");
            e.printStackTrace();

            return false;
        }
    }
}