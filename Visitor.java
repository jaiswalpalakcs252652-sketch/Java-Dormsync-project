package com.dormsync.model;

public class Visitor {

    private int visitorId;
    private int userId;
    private String visitorName;
    private String phone;
    private String visitDate;
    private String visitTime;
    private String purpose;
    private String status;

    // Default Constructor
    public Visitor() {
    }

    // Parameterized Constructor
    public Visitor(int visitorId, int userId, String visitorName,
                   String phone, String visitDate, String visitTime,
                   String purpose, String status) {

        this.visitorId = visitorId;
        this.userId = userId;
        this.visitorName = visitorName;
        this.phone = phone;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.purpose = purpose;
        this.status = status;
    }

    // Getters and Setters

    public int getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(int visitorId) {
        this.visitorId = visitorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}