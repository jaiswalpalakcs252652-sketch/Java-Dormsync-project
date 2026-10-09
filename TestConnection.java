package com.dormsync.test;

import com.dormsync.dao.PaymentDAO;
import com.dormsync.util.DBConnection;
import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  DORMSYNC FULL TEST SUITE");
        System.out.println("========================================");
        System.out.println();

        // ------------------------------------------------
        // PART 1: Test Database Connection
        // ------------------------------------------------
        System.out.println(">>> PART 1: Database Connection Test");
        Connection con = DBConnection.getConnection();

        if (con == null) {
            System.out.println("Database Connection FAILED!");
            System.out.println("Stopping tests. Fix DB connection first.");
            return;
        }

        System.out.println("Database Connected Successfully!");
        System.out.println();

        // ------------------------------------------------
        // PART 2: Test Automated Rent Generation
        // ------------------------------------------------
        System.out.println(">>> PART 2: Automated Rent Generation Test");
        System.out.println("----------------------------------------");

        PaymentDAO dao = new PaymentDAO();

        // TEST 1: First run - should generate records
        System.out.println("TEST 1: Generating rent for 2026-09");
        int result1 = dao.generateMonthlyRent("2026-09");
        System.out.println("Records generated: " + result1);
        System.out.println();

        // TEST 2: Second run - should be 0 (duplicate check)
        System.out.println("TEST 2: Running again for 2026-09 (duplicate check)");
        int result2 = dao.generateMonthlyRent("2026-09");
        System.out.println("Records generated: " + result2);
        System.out.println();

        // ------------------------------------------------
        // FINAL SUMMARY
        // ------------------------------------------------
        System.out.println("========================================");
        System.out.println("  FINAL SUMMARY");
        System.out.println("========================================");

        if (result1 > 0 && result2 == 0) {
            System.out.println("ALL TESTS PASSED!");
            System.out.println("   - DB connection: OK");
            System.out.println("   - Rent generated: " + result1 + " records");
            System.out.println("   - Duplicate check: OK (0 on second run)");
        } else if (result1 == 0 && result2 == 0) {
            System.out.println("PARTIAL PASS");
            System.out.println("   - DB connection: OK");
            System.out.println("   - No rent generated.");
            System.out.println("   - Check: users must have status='Active'");
        } else {
            System.out.println("UNEXPECTED RESULT");
            System.out.println("   - First run: " + result1);
            System.out.println("   - Second run: " + result2);
        }
    }
}