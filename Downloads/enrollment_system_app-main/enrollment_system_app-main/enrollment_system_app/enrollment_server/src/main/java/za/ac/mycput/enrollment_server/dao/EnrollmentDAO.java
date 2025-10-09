package za.ac.mycput.enrollment_server.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import za.ac.mycput.enrollment_server.connection.DBConnection;
import za.ac.mycput.enrollment_server.domain.Course;
import za.ac.mycput.enrollment_server.domain.User;

/**
 * Data Access Object for all database operations
 * 
 * @author Dumisane MADONDO (230949703)
 */
public class EnrollmentDAO {

    // User authentication and management
    public User authenticateUser(String userId, String password, String role) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ? AND PASSWORD = ? AND USER_TYPE = ?";

        try (Connection conn = DBConnection.derbyConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            ps.setString(2, password);
            ps.setString(3, role);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUser_id(Integer.parseInt(rs.getString("USER_ID")));
                user.setFirst_name(rs.getString("FIRST_NAME"));
                user.setLast_name(rs.getString("LAST_NAME"));
                user.setPassword(rs.getString("PASSWORD"));
                user.setRole(rs.getString("USER_TYPE"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }
        return null;
    }

    public boolean addStudent(String userId, String firstName, String lastName, String email, String password) {
        String sql = "INSERT INTO USERS (USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, USER_TYPE) VALUES (?, ?, ?, ?, ?, 'Student')";
        try (Connection conn = DBConnection.derbyConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, password);
            int rows = pstmt.executeUpdate();
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
            return false;
        }
    }

    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE USER_TYPE = 'Student'";

        try (Connection conn = DBConnection.derbyConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setUser_id(Integer.parseInt(rs.getString("USER_ID")));
                user.setFirst_name(rs.getString("FIRST_NAME"));
                user.setLast_name(rs.getString("LAST_NAME"));
                user.setRole(rs.getString("USER_TYPE"));
                students.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving students: " + e.getMessage());
        }
        return students;
    }

    // Course management
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM COURSES";

        try (Connection conn = DBConnection.derbyConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourse_code(rs.getString("COURSE_CODE"));
                course.setDescription(rs.getString("COURSE_DESC"));
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving courses: " + e.getMessage());
        }
        return courses;
    }

    public boolean addCourse(String courseCode, String description) {
        String sql = "INSERT INTO COURSES (COURSE_CODE, COURSE_DESC) VALUES (?, ?)";
        try (Connection conn = DBConnection.derbyConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Ensure both code and description are stored in uppercase
            pstmt.setString(1, courseCode.toUpperCase());
            pstmt.setString(2, description.toUpperCase());
            int rows = pstmt.executeUpdate();
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
            return false;
        }
    }

    // Enrollment management
    public List<Course> getStudentEnrollments(String studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.COURSE_CODE, c.COURSE_DESC " +
                "FROM COURSES c " +
                "INNER JOIN ENROLLMENTS e ON c.COURSE_CODE = e.COURSE_CODE " +
                "WHERE e.USER_ID = ?";

        try (Connection conn = DBConnection.derbyConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.setCourse_code(rs.getString("COURSE_CODE"));
                course.setDescription(rs.getString("COURSE_DESC"));
                courses.add(course);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving enrollments: " + e.getMessage());
        }
        return courses;
    }

    public boolean enrollStudent(String studentId, String courseCode) {
        // First check if already enrolled
        if (isStudentEnrolled(studentId, courseCode)) {
            return false;
        }

        String sql = "INSERT INTO ENROLLMENTS (USER_ID, COURSE_CODE) VALUES (?, ?)";
        try (Connection conn = DBConnection.derbyConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseCode);
            int rows = pstmt.executeUpdate();
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error enrolling student: " + e.getMessage());
            return false;
        }
    }

    public boolean deregisterStudent(String studentId, String courseCode) {
        String sql = "DELETE FROM ENROLLMENTS WHERE USER_ID = ? AND COURSE_CODE = ?";
        try (Connection conn = DBConnection.derbyConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseCode);
            int rows = pstmt.executeUpdate();
            conn.commit();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Error deregistering student: " + e.getMessage());
            return false;
        }
    }

    private boolean isStudentEnrolled(String studentId, String courseCode) {
        String sql = "SELECT COUNT(*) FROM ENROLLMENTS WHERE USER_ID = ? AND COURSE_CODE = ?";

        try (Connection conn = DBConnection.derbyConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ps.setString(2, courseCode);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking enrollment: " + e.getMessage());
        }
        return false;
    }
}