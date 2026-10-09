package com.dormsync.model;

import java.sql.Date;

public class RoomAllocation {

    private int allocationId;
    private int userId;
    private int roomId;
    private Date allocationDate;
    private String status;

    public RoomAllocation() {
    }

    public RoomAllocation(int userId, int roomId, Date allocationDate, String status) {
        this.userId = userId;
        this.roomId = roomId;
        this.allocationDate = allocationDate;
        this.status = status;
    }

    public RoomAllocation(int allocationId, int userId, int roomId,
                          Date allocationDate, String status) {
        this.allocationId = allocationId;
        this.userId = userId;
        this.roomId = roomId;
        this.allocationDate = allocationDate;
        this.status = status;
    }

    public int getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(int allocationId) {
        this.allocationId = allocationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(Date allocationDate) {
        this.allocationDate = allocationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}