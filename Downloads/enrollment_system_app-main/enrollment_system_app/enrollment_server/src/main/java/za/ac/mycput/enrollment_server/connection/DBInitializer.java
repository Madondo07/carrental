package za.ac.mycput.enrollment_server.connection;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Dumisane MADONDO (23949703)
 */
public class DBInitializer {

    public static void dataSchema(Connection conn) throws SQLException {
        System.out.println("Starting database schema initialization...");
        createUsers(conn);
        createCourses(conn);
        createEnrollments(conn);
        System.out.println("Database schema initialization completed!");
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName.toUpperCase(), null)) {
            return rs.next();
        }
    }

    // create USERS table (check if it exists)
    private static void createUsers(Connection conn) throws SQLException {
        if (tableExists(conn, "USERS")) {
            System.out.println("USERS table already exists - skipping creation");
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("""
                        CREATE TABLE USERS (
                            USER_ID VARCHAR(9) PRIMARY KEY,
                            FIRST_NAME VARCHAR(50) NOT NULL,
                            LAST_NAME VARCHAR(50) NOT NULL,
                            EMAIL VARCHAR(100) NOT NULL,
                            PASSWORD VARCHAR(100) NOT NULL,
                            USER_TYPE VARCHAR(20) NOT NULL
                        )
                    """);
            System.out.println("USERS table created successfully");
        } catch (SQLException e) {
            System.out.println("Error creating USERS table: " + e.getMessage());
            throw e;
        }
    }

    // create COURSE table (check if it exists)
    private static void createCourses(Connection conn) throws SQLException {
        if (tableExists(conn, "COURSES")) {
            System.out.println("COURSES table already exists - skipping creation");
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("""
                        CREATE TABLE COURSES (
                            COURSE_CODE VARCHAR(20) PRIMARY KEY,
                            COURSE_DESC VARCHAR(100) NOT NULL
                        )
                    """);
            System.out.println("COURSES table created successfully");
        } catch (SQLException e) {
            System.out.println("Error creating COURSES table: " + e.getMessage());
            throw e;
        }
    }

    // create ENROLLMENTS table (check if it exists)
    private static void createEnrollments(Connection conn) throws SQLException {
        if (tableExists(conn, "ENROLLMENTS")) {
            System.out.println("ENROLLMENTS table already exists - skipping creation");
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("""
                        CREATE TABLE ENROLLMENTS (
                            USER_ID VARCHAR(9) NOT NULL,
                            COURSE_CODE VARCHAR(20) NOT NULL
                        )
                    """);
            System.out.println("ENROLLMENTS table created successfully");
        } catch (SQLException e) {
            System.out.println("Error creating ENROLLMENTS table: " + e.getMessage());
            throw e;
        }
    }

    public static void clearAndResetData(Connection conn) throws SQLException {
        System.out.println("Clearing all existing data...");
        try (Statement st = conn.createStatement()) {
            // Clear data in correct order to avoid foreign key constraints
            st.executeUpdate("DELETE FROM ENROLLMENTS");
            st.executeUpdate("DELETE FROM USERS");
            st.executeUpdate("DELETE FROM COURSES");
            System.out.println("All existing data cleared successfully");
        }

        // Now insert fresh sample data
        sampleData(conn);
    }

    public static void sampleData(Connection conn) throws SQLException {
        System.out.println("Starting sample data population...");

        // add demo USERS
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM USERS");
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            int userCount = rs.getInt(1);
            if (userCount == 0) {
                System.out.println("Inserting sample users...");
                try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO USERS (USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, USER_TYPE) VALUES (?, ?, ?, ?, ?, ?)")) {

                    // First user
                    insert.setString(1, "230949703");
                    insert.setString(2, "Dumisane");
                    insert.setString(3, "Madondo");
                    insert.setString(4, "230949703@mycput.ac.za");
                    insert.setString(5, "dumi03");
                    insert.setString(6, "Student");
                    insert.executeUpdate();

                    // Second user - FIXED: Removed extra digit to make it 9 characters
                    insert.setString(1, "230711723");
                    insert.setString(2, "Inganathi");
                    insert.setString(3, "Mbobo");
                    insert.setString(4, "230711723@mycput.ac.za");
                    insert.setString(5, "Techwizard");
                    insert.setString(6, "Student");
                    insert.executeUpdate();

                    // Third user
                    insert.setString(1, "221568817");
                    insert.setString(2, "Pamela");
                    insert.setString(3, "Masina");
                    insert.setString(4, "pammasina@admin.ac.za");
                    insert.setString(5, "pam18");
                    insert.setString(6, "Admin");
                    insert.executeUpdate();

                    /* more user's to populate the table */
                    insert.setString(1, "267198463");
                    insert.setString(2, "Oscar");
                    insert.setString(3, "Piastri");
                    insert.setString(4, "267198463@mycput.ac.za");
                    insert.setString(5, "pam18");
                    insert.setString(6, "Student");
                    insert.executeUpdate();

                    insert.setString(1, "221170092");
                    insert.setString(2, "Abel");
                    insert.setString(3, "Smith");
                    insert.setString(4, "smith@admin.ac.za");
                    insert.setString(5, "smith@92");
                    insert.setString(6, "Admin");
                    insert.executeUpdate();

                    insert.setString(1, "254697320");
                    insert.setString(2, "Sundar");
                    insert.setString(3, "Pichai");
                    insert.setString(4, "254697320@mycput.ac.za");
                    insert.setString(5, "pichai123");
                    insert.setString(6, "Student");
                    insert.executeUpdate();
                    
                    System.out.println("USERS Inserted Successfully");
                }
            } else {
                System.out.println("USERS table already contains " + userCount + " records - skipping insertion");

                // Debug: Show what users exist
                try (Statement stmt = conn.createStatement();
                        ResultSet userRs = stmt.executeQuery("SELECT USER_ID, FIRST_NAME, LAST_NAME FROM USERS")) {
                    System.out.println("Existing users in database:");
                    while (userRs.next()) {
                        System.out.println(" - " + userRs.getString("USER_ID") + ": " +
                                userRs.getString("FIRST_NAME") + " " + userRs.getString("LAST_NAME"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error inserting users: " + e.getMessage());
            e.printStackTrace();
        }

        // add demo COURSES
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM COURSES");
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            int courseCount = rs.getInt(1);
            if (courseCount == 0) {
                System.out.println("Inserting sample courses...");
                try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO COURSES (COURSE_CODE, COURSE_DESC) VALUES (?, ?)")) {

                    String[][] courses = {
                            { "ADF262S", "APPLICATIONS DEVELOPMENT FUNDAMENTALS 2" },
                            { "ADP262S", "APPLICATIONS DEVELOPMENT PRACTICE 2" },
                            { "CNF262S", "COMMUNICATIONS NETWORKS FUNDAMENTALS 2" },
                            { "ICE262S", "ICT ELECTIVES 2" },
                            { "INM262S", "INFORMATION MANAGEMENT 2" },
                            { "ISA262S", "INFORMATION SYSTEMS ANALYSIS" },
                            { "MAF262S", "MULTIMEDIA APPLICATIONS FUNDAMENTALS 2" },
                            { "PRC262S", "PROFESSIONAL COMMUNICATIONS 2" },
                            { "PRT262S", "PROJECT 2" }
                    };

                    for (String[] course : courses) {
                        insert.setString(1, course[0]);
                        insert.setString(2, course[1]);
                        insert.executeUpdate();
                    }

                    System.out.println("COURSES Inserted Successfully");
                }
            } else {
                System.out.println("COURSES table already contains " + courseCount + " records - skipping insertion");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting courses: " + e.getMessage());
            e.printStackTrace();
        }

        // add demo ENROLLMENTS
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM ENROLLMENTS");
                ResultSet rs = ps.executeQuery()) {
            rs.next();
            int enrollmentCount = rs.getInt(1);
            if (enrollmentCount == 0) {
                System.out.println("Inserting sample enrollments...");
                try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO ENROLLMENTS (USER_ID, COURSE_CODE) VALUES (?, ?)")) {

                    String[][] enrollments = {
                            { "230949703", "ICE262S" },
                            { "267198463", "ICE262S" },
                            { "254697320", "ADP262S" },
                            { "230772319", "ICE262S" },
                            { "230949703", "PRC262S" },
                            { "230949703", "INM262S" },
                            { "267198463", "PRC262S" },
                            { "230772319", "ADP262S" },
                            { "230949703", "ADP262S" },
                            { "254697320", "ADP262S" },
                            { "254697320", "INM262S" },
                            { "230772319", "CNF262S" },
                            { "230772319", "INM262S" },
                            { "230949703", "CNF262S" },
                            { "254697320", "PRC262S" }
                    };

                    for (String[] enrollment : enrollments) {
                        insert.setString(1, enrollment[0]);
                        insert.setString(2, enrollment[1]);
                        insert.executeUpdate();
                    }

                    System.out.println("ENROLLMENTS Inserted Successfully");
                }
            } else {
                System.out.println(
                        "ENROLLMENTS table already contains " + enrollmentCount + " records - skipping insertion");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting enrollments: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Sample data population completed!");
    }

    // Method to reset database for testing
    public static void resetDatabase(Connection conn) throws SQLException {
        System.out.println("Resetting database...");
        try (Statement st = conn.createStatement()) {
            // Drop tables in correct order (due to potential foreign key constraints)
            st.executeUpdate("DROP TABLE IF EXISTS ENROLLMENTS");
            st.executeUpdate("DROP TABLE IF EXISTS USERS");
            st.executeUpdate("DROP TABLE IF EXISTS COURSES");
            System.out.println("All tables dropped successfully");
        }

        // Recreate everything
        dataSchema(conn);
        sampleData(conn);
        System.out.println("Database reset completed successfully");
    }
}