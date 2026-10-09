
package com.dormsync.dao;

import com.dormsync.model.Chore;
import com.dormsync.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChoreDAO {

    // ADD CHORE
    public boolean addChore(Chore chore) {

        String sql = "INSERT INTO chores "
                + "(user_id, chore_name, description, chore_date, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, chore.getUserId());
            ps.setString(2, chore.getChoreName());
            ps.setString(3, chore.getDescription());
            ps.setString(4, chore.getChoreDate());
            ps.setString(5, chore.getStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Chore Insert Failed!");
            e.printStackTrace();

            return false;
        }
    }

    // GET ALL CHORES
    public List<Chore> getAllChores() {

        List<Chore> chores = new ArrayList<>();

        String sql = "SELECT * FROM chores";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Chore chore = new Chore();

                chore.setChoreId(rs.getInt("chore_id"));
                chore.setUserId(rs.getInt("user_id"));
                chore.setChoreName(rs.getString("chore_name"));
                chore.setDescription(rs.getString("description"));
                chore.setChoreDate(rs.getString("chore_date"));
                chore.setStatus(rs.getString("status"));

                chores.add(chore);
            }

        } catch (Exception e) {

            System.out.println("Failed to load chores!");
            e.printStackTrace();
        }

        return chores;
    }

    // UPDATE CHORE
    public boolean updateChore(Chore chore) {

        String sql = "UPDATE chores SET "
                + "user_id = ?, "
                + "chore_name = ?, "
                + "description = ?, "
                + "chore_date = ?, "
                + "status = ? "
                + "WHERE chore_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, chore.getUserId());
            ps.setString(2, chore.getChoreName());
            ps.setString(3, chore.getDescription());
            ps.setString(4, chore.getChoreDate());
            ps.setString(5, chore.getStatus());
            ps.setInt(6, chore.getChoreId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Chore Update Failed!");
            e.printStackTrace();

            return false;
        }
    }

    // DELETE CHORE
    public boolean deleteChore(int choreId) {

        String sql = "DELETE FROM chores WHERE chore_id = ?";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, choreId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            System.out.println("Chore Delete Failed!");
            e.printStackTrace();

            return false;
        }
    }
    // ✅ NEW: Get chores belonging to ONE user (for USER role)
public List<Chore> getChoresByUser(int userId) {

    List<Chore> chores = new ArrayList<>();

    String sql = "SELECT * FROM chores WHERE user_id = ? ORDER BY chore_date DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Chore c = new Chore();
            c.setChoreId(rs.getInt("chore_id"));
            c.setUserId(rs.getInt("user_id"));
            c.setChoreName(rs.getString("chore_name"));
            c.setDescription(rs.getString("description"));
            c.setChoreDate(rs.getString("chore_date"));
            c.setStatus(rs.getString("status"));
            chores.add(c);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return chores;
}

// ✅ NEW: Get all chores with user NAME (for ADMIN role)
public List<Object[]> getAllChoresWithUser() {

    List<Object[]> rows = new ArrayList<>();

    String sql = "SELECT c.chore_id, u.name, c.chore_name, c.description, "
               + "c.chore_date, c.status "
               + "FROM chores c "
               + "JOIN users u ON c.user_id = u.user_id "
               + "ORDER BY c.chore_date DESC";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            rows.add(new Object[]{
                rs.getInt("chore_id"),
                rs.getString("name"),          // ← user name
                rs.getString("chore_name"),
                rs.getString("description"),
                rs.getString("chore_date"),
                rs.getString("status")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return rows;
}
}