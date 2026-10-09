package com.dormsync.dao;

import com.dormsync.model.User;
import com.dormsync.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Register a new user (YOUR ORIGINAL — UNCHANGED)
    public boolean registerUser(User user) {

        String sql = "INSERT INTO users "
                   + "(name, email, password, phone, role) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getRole());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println("Registration Failed!");
            e.printStackTrace();

            return false;
        }
    }

    // Login user (YOUR ORIGINAL — UNCHANGED)
    public User loginUser(String email, String password) {

        String sql = "SELECT * FROM users "
                   + "WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                User user = new User();

                user.setUserId(rs.getInt("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getString("role"));

                return user;
            }

        } catch (SQLException e) {

            System.out.println("Login Failed!");
            e.printStackTrace();
        }

        return null;
    }

    // ✅ NEW METHOD: Count users with role = 'USER' (for Dashboard stats)
    public int countUsers() {

        String sql = "SELECT COUNT(*) FROM users WHERE role = 'USER'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    // ✅ Get all users with their roles (for admin User Management screen)
public java.util.List<Object[]> getAllUsersWithRoles() {
    java.util.List<Object[]> rows = new java.util.ArrayList<>();

    String sql = "SELECT user_id, name, email, phone, role FROM users ORDER BY user_id";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("role")
            });
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return rows;
}

// ✅ Update a user's role
public boolean updateUserRole(int userId, String newRole) {
    String sql = "UPDATE users SET role = ? WHERE user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, newRole);
        ps.setInt(2, userId);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// ✅ Delete a user (fails if FK constraints block it)
public boolean deleteUser(int userId) {
    String sql = "DELETE FROM users WHERE user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, userId);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        System.out.println("Delete failed (likely FK constraint): " + e.getMessage());
        return false;
    }
}

// ✅ Update a user's password (admin action)
public boolean updateUserPassword(int userId, String newPassword) {
    String sql = "UPDATE users SET password = ? WHERE user_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, newPassword);
        ps.setInt(2, userId);
        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
// ✅ Update a user's own profile (name, phone, optional password)
public boolean updateUserProfile(int userId, String name, String phone, String newPassword) {
    boolean changePassword = newPassword != null && !newPassword.trim().isEmpty();

    String sql;
    if (changePassword) {
        sql = "UPDATE users SET name = ?, phone = ?, password = ? WHERE user_id = ?";
    } else {
        sql = "UPDATE users SET name = ?, phone = ? WHERE user_id = ?";
    }

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, name);
        ps.setString(2, phone);

        if (changePassword) {
            ps.setString(3, newPassword.trim());
            ps.setInt(4, userId);
        } else {
            ps.setInt(3, userId);
        }

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}