package com.dormsync.dao;

import com.dormsync.model.Visitor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VisitorDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/dormsync",
                "root",
                "root"
        );
    }

    // ADD VISITOR
    public boolean addVisitor(Visitor visitor) {

        String sql = "INSERT INTO visitors "
                + "(user_id, visitor_name, phone, visit_date, purpose, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, visitor.getUserId());
            ps.setString(2, visitor.getVisitorName());
            ps.setString(3, visitor.getPhone());
            ps.setString(4, visitor.getVisitDate());
            ps.setString(5, visitor.getPurpose());
            ps.setString(6, visitor.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Add Visitor Error: " + e.getMessage());
            return false;
        }
    }

    // GET ALL VISITORS
    public List<Visitor> getAllVisitors() {

        List<Visitor> visitorList = new ArrayList<>();

        String sql = "SELECT * FROM visitors ORDER BY visitor_id DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Visitor visitor = new Visitor();

                visitor.setVisitorId(rs.getInt("visitor_id"));
                visitor.setUserId(rs.getInt("user_id"));
                visitor.setVisitorName(rs.getString("visitor_name"));
                visitor.setPhone(rs.getString("phone"));
                visitor.setVisitDate(rs.getString("visit_date"));
                visitor.setPurpose(rs.getString("purpose"));
                visitor.setStatus(rs.getString("status"));

                visitorList.add(visitor);
            }

        } catch (SQLException e) {
            System.out.println("Get Visitors Error: " + e.getMessage());
        }

        return visitorList;
    }

    // UPDATE VISITOR
    public boolean updateVisitor(Visitor visitor) {

        String sql = "UPDATE visitors SET "
                + "user_id=?, "
                + "visitor_name=?, "
                + "phone=?, "
                + "visit_date=?, "
                + "purpose=?, "
                + "status=? "
                + "WHERE visitor_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, visitor.getUserId());
            ps.setString(2, visitor.getVisitorName());
            ps.setString(3, visitor.getPhone());
            ps.setString(4, visitor.getVisitDate());
            ps.setString(5, visitor.getPurpose());
            ps.setString(6, visitor.getStatus());
            ps.setInt(7, visitor.getVisitorId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Update Visitor Error: " + e.getMessage());
            return false;
        }
    }

    // DELETE VISITOR
    public boolean deleteVisitor(int visitorId) {

        String sql = "DELETE FROM visitors WHERE visitor_id=?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, visitorId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Delete Visitor Error: " + e.getMessage());
            return false;
        }
    }
    // ✅ NEW: Get visitors belonging to ONE user (for USER role)
public List<Visitor> getVisitorsByUser(int userId) {

    List<Visitor> visitorList = new ArrayList<>();

    String sql = "SELECT * FROM visitors WHERE user_id = ? ORDER BY visit_date DESC";

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Visitor v = new Visitor();
            v.setVisitorId(rs.getInt("visitor_id"));
            v.setUserId(rs.getInt("user_id"));
            v.setVisitorName(rs.getString("visitor_name"));
            v.setPhone(rs.getString("phone"));
            v.setVisitDate(rs.getString("visit_date"));
            v.setPurpose(rs.getString("purpose"));
            v.setStatus(rs.getString("status"));
            visitorList.add(v);
        }

    } catch (SQLException e) {
        System.out.println("Get Visitors By User Error: " + e.getMessage());
    }

    return visitorList;
}

// ✅ NEW: Get all visitors with the user's NAME (for ADMIN role)
public List<Object[]> getAllVisitorsWithUser() {

    List<Object[]> rows = new ArrayList<>();

    String sql = "SELECT v.visitor_id, u.name, v.visitor_name, v.phone, "
               + "v.visit_date, v.purpose, v.status "
               + "FROM visitors v "
               + "JOIN users u ON v.user_id = u.user_id "
               + "ORDER BY v.visit_date DESC";

    try (Connection con = getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("visitor_id"),
                rs.getString("name"),          // ← user name
                rs.getString("visitor_name"),
                rs.getString("phone"),
                rs.getString("visit_date"),
                rs.getString("purpose"),
                rs.getString("status")
            });
        }

    } catch (SQLException e) {
        System.out.println("Get Visitors With User Error: " + e.getMessage());
    }

    return rows;
}
}