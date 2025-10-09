package za.ac.mycput.enrollment_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import za.ac.mycput.enrollment_server.connection.DBConnection;
import za.ac.mycput.enrollment_server.connection.DBInitializer;
import za.ac.mycput.enrollment_server.dao.EnrollmentDAO;
import za.ac.mycput.enrollment_server.domain.Course;
import za.ac.mycput.enrollment_server.domain.User;

/**
 * Enrollment Server
 *
 * @author Inganathi Mobobo (230711723)
 * @modifier Dumisane Madondo(230949703)
 */
public class Enrollment_server {

    // Declare our most important attributes
    private static final int PORT = 6666;
    private ServerSocket listener;
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final EnrollmentDAO dao = new EnrollmentDAO(); // Import and Instintiante the dao class

    /**
     * Creates a new instance of Server
     */
    public Enrollment_server() {
        // Create server socket
        try {
            listener = new ServerSocket(PORT);
            System.out.println("Enrollment Server created on port " + PORT);
        } catch (IOException ioe) {
            System.out.println("IO Exception: " + ioe.getMessage());
        }
    }

    public void listen() {
        // Start listening for client connections
        while (true) {
            try {
                System.out.println("Server is listening...");
                client = listener.accept();
                System.out.println("New client connected: " + client.getInetAddress());
                System.out.println("Now moving onto process client");

                // Handle each client in a separate thread like Mr Naaido showed us how to use
                // it in class
                new Thread(() -> processClient()).start(); // This will allow us to continuously run the Client.java
                // class without having to run the Server.java class all over
                // and over again
            } catch (IOException ioe) {
                System.out.println("IO Exception: " + ioe.getMessage());
            }
        }
    }

    // Communicate with the client
    public void processClient() {
        // First step: initiate channels
        out = null;
        in = null;

        try {
            out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            in = new ObjectInputStream(client.getInputStream());

            // Step 2: communicate
            String request; // While the client and sever are exchanging data they are communication on the
            // Server terminal
            while ((request = (String) in.readObject()) != null) { // Using the while loop to continuously read the
                // client input until the client disconnects
                System.out.println("From CLIENT>> " + request);
                String response = processRequest(request);
                out.writeObject(response);
                out.flush();
                System.out.println("To CLIENT>> " + response);
            }

        } catch (EOFException eofe) {
            System.out.println("Client disconnected normally");
        } catch (IOException ioe) {
            System.out.println("IO Exception: " + ioe.getMessage());
        } catch (ClassNotFoundException cnfe) {
            System.out.println("Class not found: " + cnfe.getMessage());
        } finally {
            // Step 3: close down
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (client != null) {
                    client.close();
                }
                System.out.println("Client connection closed");
            } catch (IOException ioe) {
                System.out.println("Error closing connection: " + ioe.getMessage());
            }
        }
    }

    /**
     * Process different types of requests
     */
    private String processRequest(String request) {
        String[] parts = request.split("\\|");
        String command = parts[0];

        // Here we use a switch case to handle different commands from the client, but
        // we can later change it to a diffetrent approach if needed
        // The switch case will call different methods based on the command received
        // from the client
        try {
            switch (command) {
                case "LOGIN":
                    return handleLogin(parts);
                case "GET_COURSES":
                    return handleGetCourses();
                case "GET_STUDENT_COURSES":
                    return handleGetStudentCourses(parts);
                case "ENROLL":
                    return handleEnroll(parts);
                case "ENROLL_COURSES":
                    return handleEnrollMultipleCourses(parts);
                case "DEREGISTER":
                    return handleDeregister(parts);
                case "ADD_STUDENT":
                    return handleAddStudent(parts);
                case "ADD_COURSE":
                    return handleAddCourse(parts);
                case "GET_ALL_STUDENTS":
                    return handleGetAllStudents();
                default:
                    return "ERROR|Unknown command";
            }
        } catch (Exception e) {
            return "ERROR|" + e.getMessage();
        }
    }

    private String handleLogin(String[] parts) {

        if (parts.length < 4) {
            return "ERROR|Invalid login request";
        }

        String userId = parts[1];
        String password = parts[2];
        String role = parts[3];

        User user = dao.authenticateUser(userId, password, role); // Get details from the database it's like
        // authentication

        if (user != null) {
            return "SUCCESS|" + user.getFirst_name() + "|" + user.getLast_name() + "|" + user.getRole(); // Check from
            // the db using
            // the dao
            // object if
            // user input
            // match
        } else {
            return "ERROR|Invalid credentials or role";
        }
    }

    private String handleGetCourses() {
        List<Course> courses = dao.getAllCourses(); // call our getAllCourses() method from the dao and store in on list
        // datastructure object courses

        if (courses.isEmpty()) { // isEmpty() is a java keyword to check if the list is empty or not, in this
            // case it will retreive from the database using the courses object
            return "ERROR|No courses available";
        }

        StringBuilder response = new StringBuilder("COURSES"); // this is the message to the client
        for (Course course : courses) { // it will display in a JTable
            response.append("|").append(course.getCourse_code()) // in this case we are inserting every couse from the
                    // db into the JTable using .append()
                    .append("|").append(course.getDescription());
        }

        return response.toString(); // obviously we are casting the message to a String before we send it to the
        // client, the reason I am using a StringBuilder is to type cast here already
    }

    private String handleGetStudentCourses(String[] parts) {
        if (parts.length < 2) {
            return "ERROR|Invalid request"; // All these capital letters at the start of every return trigger a
            // JOptionPane ERROR/MESSAGE/SUCCESS/
        }

        String studentId = parts[1];
        List<Course> courses = dao.getStudentEnrollments(studentId);

        StringBuilder response = new StringBuilder("ENROLLMENTS");
        for (Course course : courses) {
            response.append("|").append(course.getCourse_code())
                    .append("|").append(course.getDescription());
        }

        return response.toString();
    }

    // This method is used to enroll a student to a course
    private String handleEnroll(String[] parts) {
        if (parts.length < 3) {
            return "ERROR|Invalid enrollment request"; // All these capitale letters at the start of every return
            // trigger a JOptionPane ERROR/MESSAGE/SUCCESS/
        }

        String studentId = parts[1];
        String courseCode = parts[2];

        boolean success = dao.enrollStudent(studentId, courseCode); // Triggers a dao method that will add the studentID
        // and CourseID to the ENROLLMENTS table on the db

        if (success) {
            return "SUCCESS|Successfully enrolled in " + courseCode;
        } else {
            return "ERROR|Failed to enroll. You may already be enrolled in this course.";
        }
    }

    // enroll a student in multiple courses at once
    private String handleEnrollMultipleCourses(String[] parts) {
        if (parts.length < 3) {
            return "ERROR|Invalid enrollment request";
        }
        String studentId = parts[1];
        String[] courseCodes = parts[2].split(",");
        List<String> enrolled = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        for (String code : courseCodes) {
            if (dao.enrollStudent(studentId, code)) {
                enrolled.add(code);
            } else {
                skipped.add(code);
            }
        }
        StringBuilder response = new StringBuilder("Enrollment results:");
        if (!enrolled.isEmpty()) {
            response.append("\nEnrolled: ").append(String.join(", ", enrolled));
        }
        if (!skipped.isEmpty()) {
            response.append("\nAlready enrolled(skipped): ").append(String.join(", ", skipped));
        }
        return "SUCCESS|" + response.toString();
    }

    // This method handles the Deregestration process triggered when the user clicks
    // a button to deregister
    private String handleDeregister(String[] parts) {
        if (parts.length < 3) {
            return "ERROR|Invalid deregistration request";
        }

        String studentId = parts[1];
        String courseCode = parts[2];

        boolean success = dao.deregisterStudent(studentId, courseCode);

        if (success) {
            return "SUCCESS|Successfully deregistered from " + courseCode;
        } else {
            return "ERROR|Failed to deregister from " + courseCode;
        }
    }

    // This is the addStudent() method for the Admin functionality
    private String handleAddStudent(String[] parts) {
        if (parts.length < 3) { // only need firstName and lastName
            return "ERROR|Invalid add student request";
        }

        String firstName = parts[1];
        String lastName = parts[2];

        String userId = generateUserId(); // auto-generated with prefix 26 & 7 random digits (9 digits)
        String email = userId + "@mycput.ac.za";
        String password = generatePassword(lastName); // auto-generated with lastName + 4 random digits

        boolean success = dao.addStudent(userId, firstName, lastName, email, password); // Now here we are adding the
        // student to the database
        // calling the addStudent()
        // method from the dao

        if (success) {
            // Friendly message with first name, last name, and student number
            return "SUCCESS|Student added successfully!\nName: " + firstName + " " + lastName + "\nStudent Number: "
                    + userId;
        } else {
            return "ERROR|Failed to add student. Student may already exist.";
        }
    }

    /* Random digits generators */
    // generate random user id (student number) with prefix 26 for 2026 academic
    // year
    private String generateUserId() {
        Random rand = new Random();
        int randomDigits = rand.nextInt(10_000_000);
        return "26" + String.format("%07d", randomDigits);
    }

    // generate random digits for password
    private String generatePassword(String lastName) {
        Random rand = new Random();
        int randomDigits = rand.nextInt(10_000);
        return lastName.toLowerCase() + String.format("%04d", randomDigits);
    }

    // This is a method to add a course to the db
    private String handleAddCourse(String[] parts) {
        if (parts.length < 3) {
            return "ERROR|Invalid add course request";
        }

        String courseCode = parts[1];
        String description = parts[2];

        boolean success = dao.addCourse(courseCode, description); // here you are adding the course to the db

        if (success) {
            return "SUCCESS|Course " + courseCode + " added successfully"; // Returns a JOptionPane that shows the
            // courseCode
        } else {
            return "ERROR|Failed to add course. Code may already exist.";
        }
    }

    // This method is triggered when the Admin clicks the Refresh Students button
    private String handleGetAllStudents() {
        List<User> students = dao.getAllStudents(); // This method will fetch students from the database and store them
        // in a List datastructure

        if (students.isEmpty()) {
            return "ERROR|No students found";// All these capitale letters at the start of every return trigger a
            // JOptionPane ERROR/MESSAGE/SUCCESS/
        }

        StringBuilder response = new StringBuilder("STUDENTS"); // This is the message being sent to the client

        // This for loop will append or add student details to a JTable and display them
        // on the front-end Admin UI using the GUI in the Client.java Class
        for (User student : students) {
            response.append("|").append(student.getUser_id())
                    .append("|").append(student.getFirst_name())
                    .append("|").append(student.getLast_name());
        }

        return response.toString(); // Type casting the client message to a String, that is why I used a
        // StringBuilder to type cast from the server class
    }

    public static void main(String[] args) {
        Connection conn = DBConnection.derbyConnection();
        try {
            DBInitializer.dataSchema(conn);
            DBInitializer.clearAndResetData(conn);
        } catch (SQLException ex) {
            Logger.getLogger(Enrollment_server.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Instantiate/Create the server side application
        Enrollment_server server = new Enrollment_server();
        // Start waiting for connections
        server.listen();
    }

    // ??/
}
