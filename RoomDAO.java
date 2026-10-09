package com.dormsync.dao;

import com.dormsync.model.Room;
import com.dormsync.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // ADD ROOM
    public boolean addRoom(Room room) {

        String sql = "INSERT INTO rooms "
                   + "(room_number, room_type, capacity, status) "
                   + "VALUES (?, ?, ?, ?)";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setInt(3, room.getCapacity());
            ps.setString(4, room.getStatus());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {

            System.out.println("Room Insert Failed!");
            e.printStackTrace();

            return false;
        }
    }

    // GET ALL ROOMS
    public List<Room> getAllRooms() {

        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT * FROM rooms";

        try (
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                Room room = new Room();

                room.setRoomId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setCapacity(rs.getInt("capacity"));
                room.setStatus(rs.getString("status"));

                rooms.add(room);
            }

        } catch (Exception e) {

            System.out.println("Failed to load rooms!");
            e.printStackTrace();
        }

        return rooms;
    }

    // COUNT ROOMS BY STATUS (used for Dashboard stats)
    public int countByStatus(String status) {

        String sql = "SELECT COUNT(*) FROM rooms WHERE status = ?";

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

    // DELETE ROOM (safer version — checks for active allocations first)
    public boolean deleteRoom(int roomId) {

        // Step 1: Check if the room has any active allocations
        String checkSql = "SELECT COUNT(*) FROM room_allocations WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setInt(1, roomId);
            ResultSet rs = check.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Cannot delete: room has active allocations.");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Step 2: Safe to delete
        String sql = "DELETE FROM rooms WHERE room_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}