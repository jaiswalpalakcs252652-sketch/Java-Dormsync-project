package com.dormsync.dao;

import java.time.LocalDate;
import java.time.YearMonth;
import com.dormsync.model.Payment;
import com.dormsync.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // ADD PAYMENT (YOUR ORIGINAL — UNCHANGED)
    public boolean addPayment(Payment payment) {

        String sql = "INSERT INTO payments "
                   + "(user_id, amount, payment_type, payment_date, status) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, payment.getUserId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentType());
            ps.setString(4, payment.getPaymentDate());
            ps.setString(5, payment.getStatus());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            System.out.println("Payment Insert Failed!");
            e.printStackTrace();

            return false;
        }
    }


    // GET ALL PAYMENTS (YOUR ORIGINAL — UNCHANGED)
    public List<Payment> getAllPayments() {

        List<Payment> payments = new ArrayList<>();

        String sql = "SELECT * FROM payments";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Payment payment = new Payment();

                payment.setPaymentId(rs.getInt("payment_id"));
                payment.setUserId(rs.getInt("user_id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setPaymentType(rs.getString("payment_type"));
                payment.setPaymentDate(rs.getString("payment_date"));
                payment.setStatus(rs.getString("status"));

                payments.add(payment);
            }

        } catch (Exception e) {

            System.out.println("Failed to load payments!");
            e.printStackTrace();
        }

        return payments;
    }


    // UPDATE PAYMENT (YOUR ORIGINAL — UNCHANGED)
    public boolean updatePayment(Payment payment) {

        String sql = "UPDATE payments SET "
                   + "user_id = ?, "
                   + "amount = ?, "
                   + "payment_type = ?, "
                   + "payment_date = ?, "
                   + "status = ? "
                   + "WHERE payment_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, payment.getUserId());
            ps.setDouble(2, payment.getAmount());
            ps.setString(3, payment.getPaymentType());
            ps.setString(4, payment.getPaymentDate());
            ps.setString(5, payment.getStatus());
            ps.setInt(6, payment.getPaymentId());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            System.out.println("Payment Update Failed!");
            e.printStackTrace();

            return false;
        }
    }


    // DELETE PAYMENT (YOUR ORIGINAL — UNCHANGED)
    public boolean deletePayment(int paymentId) {

        String sql = "DELETE FROM payments WHERE payment_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, paymentId);

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            System.out.println("Payment Delete Failed!");
            e.printStackTrace();

            return false;
        }
    }

    // paymentExistsForMonth (YOUR ORIGINAL — UNCHANGED)
    public boolean paymentExistsForMonth(int userId, String monthYear) {

        String sql = "SELECT COUNT(*) FROM payments "
                   + "WHERE user_id = ? "
                   + "AND DATE_FORMAT(payment_date, '%Y-%m') = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);
            ps.setString(2, monthYear);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            System.out.println("Failed to check payment existence!");
            e.printStackTrace();
        }

        return false;
    }


    // generateMonthlyRent (YOUR ORIGINAL — UNCHANGED)
    public int generateMonthlyRent(String monthYear) {

        String selectSql = "SELECT user_id, rent_amount FROM users "
                         + "WHERE status = 'Active'";

        String insertSql = "INSERT INTO payments "
                         + "(user_id, amount, payment_type, payment_date, status) "
                         + "VALUES (?, ?, 'Auto-Rent', ?, 'Pending')";

        int generatedCount = 0;

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement selectPs = conn.prepareStatement(selectSql);
            PreparedStatement insertPs = conn.prepareStatement(insertSql)
        ) {

            LocalDate firstOfMonth = YearMonth.parse(monthYear).atDay(1);

            ResultSet rs = selectPs.executeQuery();

            while (rs.next()) {

                int userId = rs.getInt("user_id");
                double rentAmount = rs.getDouble("rent_amount");

                if (paymentExistsForMonth(userId, monthYear)) {
                    continue;
                }

                insertPs.setInt(1, userId);
                insertPs.setDouble(2, rentAmount);
                insertPs.setString(3, firstOfMonth.toString());

                insertPs.addBatch();
                generatedCount++;
            }

            if (generatedCount > 0) {
                insertPs.executeBatch();
            }

            System.out.println("Generated " + generatedCount 
                             + " rent records for " + monthYear);

        } catch (Exception e) {
            System.out.println("Rent Generation Failed!");
            e.printStackTrace();
            return 0;
        }

        return generatedCount;
    }

    // ✅ NEW METHOD: Count payments by status (for Dashboard stats)
    public int countByStatus(String status) {

        String sql = "SELECT COUNT(*) FROM payments WHERE status = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
    // ✅ NEW: Get payments for a specific user (for USER role)
public List<Payment> getPaymentsByUser(int userId) {
    List<Payment> payments = new ArrayList<>();

    String sql = "SELECT * FROM payments WHERE user_id = ? ORDER BY payment_date DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Payment payment = new Payment();
            payment.setPaymentId(rs.getInt("payment_id"));
            payment.setUserId(rs.getInt("user_id"));
            payment.setAmount(rs.getDouble("amount"));
            payment.setPaymentType(rs.getString("payment_type"));
            payment.setPaymentDate(rs.getString("payment_date"));
            payment.setStatus(rs.getString("status"));
            payments.add(payment);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return payments;
}
// ✅ NEW: Get payments with user's NAME (for admin view)
public List<Object[]> getAllPaymentsWithUser() {

    List<Object[]> rows = new ArrayList<>();

    String sql = "SELECT p.payment_id, u.name, p.amount, p.payment_type, "
               + "p.payment_date, p.status "
               + "FROM payments p "
               + "JOIN users u ON p.user_id = u.user_id "
               + "ORDER BY p.payment_date DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("payment_id"),
                rs.getString("name"),          // ← user NAME, not user_id
                rs.getDouble("amount"),
                rs.getString("payment_type"),
                rs.getString("payment_date"),
                rs.getString("status")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return rows;
}
}