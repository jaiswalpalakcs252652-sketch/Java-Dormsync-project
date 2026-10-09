package com.dormsync.dao;

import com.dormsync.model.RoomAllocation;
import com.dormsync.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomAllocationDAO {

    // ADD ROOM ALLOCATION
    public boolean addAllocation(RoomAllocation allocation) {

        String sql = "INSERT INTO room_allocations " +
                     "(user_id, room_id, allocation_date, status) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, allocation.getUserId());
            pst.setInt(2, allocation.getRoomId());
            pst.setDate(3, allocation.getAllocationDate());
            pst.setString(4, allocation.getStatus());

            int rows = pst.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // GET ALL ROOM ALLOCATIONS
    public List<RoomAllocation> getAllAllocations() {

        List<RoomAllocation> allocations = new ArrayList<>();

        String sql = "SELECT * FROM room_allocations";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                RoomAllocation allocation =
                        new RoomAllocation(
                                rs.getInt("allocation_id"),
                                rs.getInt("user_id"),
                                rs.getInt("room_id"),
                                rs.getDate("allocation_date"),
                                rs.getString("status")
                        );

                allocations.add(allocation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allocations;
    }

    // ✅ NEW: DELETE ROOM ALLOCATION
    public boolean deleteAllocation(int allocationId) {

        String sql = "DELETE FROM room_allocations WHERE allocation_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, allocationId);

            int rows = pst.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}