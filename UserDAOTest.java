package com.dormsync.test;

import com.dormsync.dao.UserDAO;
import com.dormsync.model.User;

public class UserDAOTest {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        // Test login
        User user = userDAO.loginUser(
                "testuser@gmail.com",
                "12345"
        );

        if (user != null) {

            System.out.println("Login Successful!");
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Phone: " + user.getPhone());
            System.out.println("Role: " + user.getRole());

        } else {

            System.out.println("Login Failed!");
        }
    }
}