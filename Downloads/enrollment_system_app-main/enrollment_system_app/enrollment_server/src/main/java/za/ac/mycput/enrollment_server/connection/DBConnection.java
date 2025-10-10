package za.ac.mycput.enrollment_server.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Dumisane MADONDO (23949703)
 */
public class DBConnection {

    public static Connection derbyConnection() {
        Connection conn = null;
        try {
            String dbURL = "jdbc:derby://localhost:1527/EnrollmentSystemDB";
            String user = "administrator";
            String password = "password";

            System.out.println("Attempting to connect to database...");
            conn = DriverManager.getConnection(dbURL, user, password);
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        return conn;
    }
}
