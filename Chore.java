package com.dormsync.model;

public class Chore {

    private int choreId;
    private int userId;
    private String choreName;
    private String description;
    private String choreDate;
    private String status;

    public Chore() {
    }

    public Chore(int userId, String choreName,
                 String description, String choreDate,
                 String status) {

        this.userId = userId;
        this.choreName = choreName;
        this.description = description;
        this.choreDate = choreDate;
        this.status = status;
    }

    public int getChoreId() {
        return choreId;
    }

    public void setChoreId(int choreId) {
        this.choreId = choreId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getChoreName() {
        return choreName;
    }

    public void setChoreName(String choreName) {
        this.choreName = choreName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChoreDate() {
        return choreDate;
    }

    public void setChoreDate(String choreDate) {
        this.choreDate = choreDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}