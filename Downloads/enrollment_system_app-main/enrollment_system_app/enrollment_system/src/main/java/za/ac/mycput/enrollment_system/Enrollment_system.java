package za.ac.mycput.enrollment_system;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;

/**
 * Enrollment System (Client)
 * 
 * @author Masina (221568816)
 * @co-author Dumisane Madondo (230949703)
 * @co-author Inganathi Mobobo (230711723)
 */
public class Enrollment_system extends JFrame {

    // Enrollment_system connection components
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 6666;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    // Main GUI components
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Login components
    private JTextField txtUserId;
    private JPasswordField txtPassword;
    private JRadioButton radioStudent, radioAdmin;

    // Student panel components
    private JTable studentCourseTable;
    private DefaultTableModel studentTableModel;
    // Remove studentIdField, add student info label and variables
    private JLabel lblStudentInfo;
    private String loggedInStudentId;

    // Admin panel components
    private JRadioButton adminStudentRadio, adminCourseRadio;
    private JPanel adminFormContainer;
    private JTable adminDataTable;
    private DefaultTableModel adminTableModel;

    // Admin form components
    private JTextField adminStudentId, adminFirstName, adminLastName;
    private JTextField adminCourseCode, adminCourseDesc;

    public Enrollment_system() {
        setTitle("Enrollment System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize client connection
        initializeClientConnection();

        // Create main interface
        initializeGUI();

        // Add window listener to close socket on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeConnection();
            }
        });

        setVisible(true);
    }

    private void initializeClientConnection() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server successfully");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot connect to server. Please ensure the server is running.\nError: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void closeConnection() {
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null)
                socket.close();
            System.out.println("Connection closed");
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    private String sendRequest(String request) {
        try {
            out.writeObject(request);
            out.flush();
            return (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return "ERROR|Communication error: " + e.getMessage();
        }
    }

    // Enrollment_system methods for different operations
    private String login(String userId, String password, String role) {
        String request = "LOGIN|" + userId + "|" + password + "|" + role;
        return sendRequest(request);
    }

    private String getAvailableCourses() {
        return sendRequest("GET_COURSES");
    }

    private String getStudentCourses(String studentId) {
        String request = "GET_STUDENT_COURSES|" + studentId;
        return sendRequest(request);
    }

    private String deregisterStudent(String studentId, String courseCode) {
        String request = "DEREGISTER|" + studentId + "|" + courseCode;
        return sendRequest(request);
    }

    private String addStudent(String firstName, String lastName) {
        String request = "ADD_STUDENT|" + firstName + "|" + lastName;
        return sendRequest(request);
    }

    private String addCourse(String courseCode, String description) {
        String request = "ADD_COURSE|" + courseCode + "|" + description;
        return sendRequest(request);
    }

    private String getAllStudents() {
        return sendRequest("GET_ALL_STUDENTS");
    }

    private void initializeGUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create all panels
        createLoginPanel();
        createStudentPanel();
        createAdminPanel();

        add(mainPanel);
        showScreen("Login");
    }

    private void createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loginPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Enrollment System Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 100, 200));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // User ID
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        txtUserId = new JTextField(20);
        loginPanel.add(txtUserId, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        loginPanel.add(txtPassword, gbc);

        // Role selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout());
        radioPanel.setBackground(new Color(240, 240, 240));
        radioStudent = new JRadioButton("Student", true);
        radioAdmin = new JRadioButton("Admin");
        radioStudent.setBackground(new Color(240, 240, 240));
        radioAdmin.setBackground(new Color(240, 240, 240));
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(radioStudent);
        roleGroup.add(radioAdmin);
        radioPanel.add(radioStudent);
        radioPanel.add(radioAdmin);
        loginPanel.add(radioPanel, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Login");
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.addActionListener(new LoginAction());
        loginPanel.add(btnLogin, gbc);

        // Sample credentials
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JLabel sampleLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                        "Sample Credentials:<br>" +
                        "Student: 230949703 / dumi03<br>" +
                        "Admin: 221568817 / pam18" +
                        "</div></html>",
                JLabel.CENTER);
        sampleLabel.setForeground(Color.GRAY);
        loginPanel.add(sampleLabel, gbc);

        mainPanel.add(loginPanel, "Login");
    }

    private void createStudentPanel() {
        JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
        studentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top controls
        JPanel topPanel = new JPanel(new BorderLayout()); // Use BorderLayout for left/right separation
        topPanel.setBorder(BorderFactory.createTitledBorder(" Student Controls "));

        // Left side, student info label
        lblStudentInfo = new JLabel();
        topPanel.add(lblStudentInfo, BorderLayout.WEST);

        // Right side, controller buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnViewCourses = new JButton("View Available Courses");
        JButton btnViewMyCourses = new JButton("View My Courses");
        JButton btnEnroll = new JButton("Enroll");
        JButton btnDeregister = new JButton("Deregister");
        JButton btnBack = new JButton("Log Out");

        buttonPanel.add(btnViewCourses);
        buttonPanel.add(btnViewMyCourses);
        buttonPanel.add(btnEnroll);
        buttonPanel.add(btnDeregister);
        buttonPanel.add(btnBack);

        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Course table with checkbox column for selection
        studentTableModel = new DefaultTableModel(new Object[] { "Select", "Course Code", "Description" }, 0) {
            private boolean checkboxEditable = true;

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 && checkboxEditable;
            }
        };
        studentCourseTable = new JTable(studentTableModel);
        // Checkbox column width
        int columnWidth = 70;
        studentCourseTable.getColumnModel().getColumn(0).setPreferredWidth(columnWidth);
        studentCourseTable.getColumnModel().getColumn(0).setMinWidth(columnWidth);
        studentCourseTable.getColumnModel().getColumn(0).setMaxWidth(columnWidth);

        studentCourseTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Ctrl/Shift multi-select
        // Checkboxes
        studentCourseTable.getTableHeader().setReorderingAllowed(false);
        studentCourseTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(studentCourseTable);

        studentPanel.add(topPanel, BorderLayout.NORTH);
        studentPanel.add(scrollPane, BorderLayout.CENTER);

        // Event handlers
        btnViewCourses.addActionListener(e -> viewAvailableCourses());
        btnViewMyCourses.addActionListener(e -> viewMyCourses());
        btnEnroll.addActionListener(e -> enrollInCourse());
        btnDeregister.addActionListener(e -> deregisterFromCourse());
        btnBack.addActionListener(e -> {
            clearLoginFields();
            showScreen("Login");
        });

        mainPanel.add(studentPanel, "Student");
    }

    private void createAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout(10, 10));
        adminPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top radio buttons
        JPanel radioPanel = new JPanel(new FlowLayout());
        radioPanel.setBorder(BorderFactory.createTitledBorder(" Mode Selection "));
        adminStudentRadio = new JRadioButton("Student Management", true);
        adminCourseRadio = new JRadioButton("Course Management");
        ButtonGroup adminGroup = new ButtonGroup();
        adminGroup.add(adminStudentRadio);
        adminGroup.add(adminCourseRadio);

        JButton btnBack = new JButton("Log Out");

        radioPanel.add(new JLabel("Select Mode:"));
        radioPanel.add(adminStudentRadio);
        radioPanel.add(adminCourseRadio);
        radioPanel.add(btnBack);

        // Form container with CardLayout
        adminFormContainer = new JPanel(new CardLayout());
        createAdminStudentForm();
        createAdminCourseForm();

        // Data table
        adminTableModel = new DefaultTableModel(new String[] { "ID", "First Name", "Last Name" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        adminDataTable = new JTable(adminTableModel);
        adminDataTable.setRowHeight(22); // Row height
        JScrollPane tableScrollPane = new JScrollPane(adminDataTable);
        tableScrollPane.setPreferredSize(new Dimension(800, 300));

        adminPanel.add(radioPanel, BorderLayout.NORTH);
        adminPanel.add(adminFormContainer, BorderLayout.CENTER);
        adminPanel.add(tableScrollPane, BorderLayout.SOUTH);

        // Event handlers
        adminStudentRadio.addActionListener(e -> switchAdminMode("Student"));
        adminCourseRadio.addActionListener(e -> switchAdminMode("Course"));
        btnBack.addActionListener(e -> {
            clearLoginFields();
            showScreen("Login");
        });

        mainPanel.add(adminPanel, "Admin");
    }

    private void createAdminStudentForm() {
        JPanel studentForm = new JPanel(new GridLayout(4, 2, 10, 10));
        studentForm.setBorder(BorderFactory.createTitledBorder(" Add New Student "));

        // Only first name and last name are entered; student number is generated by
        // server
        adminStudentId = new JTextField();
        adminStudentId.setEditable(false);
        adminFirstName = new JTextField();
        adminLastName = new JTextField();
        JButton btnAddStudent = new JButton("Add Student");
        JButton btnRefresh = new JButton("Refresh Students");

        studentForm.add(new JLabel("First Name:"));
        studentForm.add(adminFirstName);
        studentForm.add(new JLabel("Last Name:"));
        studentForm.add(adminLastName);
        studentForm.add(btnAddStudent);
        studentForm.add(btnRefresh);

        btnAddStudent.addActionListener(e -> addNewStudent());
        btnRefresh.addActionListener(e -> {
            refreshStudents();
            adminStudentId.setText("");
        });

        adminFormContainer.add(studentForm, "Student");

    }

    private void createAdminCourseForm() {
        JPanel courseForm = new JPanel(new GridLayout(3, 2, 10, 10));
        courseForm.setBorder(BorderFactory.createTitledBorder("Add New Course"));

        adminCourseCode = new JTextField();
        adminCourseDesc = new JTextField();
        JButton btnAddCourse = new JButton("Add Course");
        JButton btnRefresh = new JButton("Refresh Courses");

        courseForm.add(new JLabel("Course Code:"));
        courseForm.add(adminCourseCode);
        courseForm.add(new JLabel("Description:"));
        courseForm.add(adminCourseDesc);
        courseForm.add(btnAddCourse);
        courseForm.add(btnRefresh);

        btnAddCourse.addActionListener(e -> addNewCourse());
        btnRefresh.addActionListener(e -> refreshCourses());

        adminFormContainer.add(courseForm, "Course");
    }

    // Navigation method
    private void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
        if (screenName.equals("Admin")) {
            switchAdminMode("Student");
        }
    }

    // Restore clearLoginFields method
    private void clearLoginFields() {
        txtUserId.setText("");
        txtPassword.setText("");
        radioStudent.setSelected(true);
        loggedInStudentId = null;
        if (lblStudentInfo != null)
            lblStudentInfo.setText("");
    }

    // Restore LoginAction inner class
    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userId = txtUserId.getText().trim();
            String password = new String(txtPassword.getPassword());
            String role = radioStudent.isSelected() ? "Student" : "Admin";

            if (userId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(Enrollment_system.this,
                        "Please enter both user ID and password",
                        "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String response = login(userId, password, role);
            String[] parts = response.split("\\|");

            if (parts[0].equals("SUCCESS")) {
                String firstName = parts[1];
                String lastName = parts[2];
                String userType = parts[3];

                JOptionPane.showMessageDialog(Enrollment_system.this,
                        "Welcome " + firstName + " " + lastName + "!");

                if (userType.equals("Student")) {
                    // Store student info
                    loggedInStudentId = userId;
                    // Set label
                    if (lblStudentInfo != null) {
                        String displayName = lastName.toUpperCase() + ", " + firstName + " (" + userId + ")";
                        lblStudentInfo.setText(displayName);
                    }
                    showScreen("Student");
                } else if (userType.equals("Admin")) {
                    showScreen("Admin");
                }
            } else {
                JOptionPane.showMessageDialog(Enrollment_system.this,
                        "Login failed: " + (parts.length > 1 ? parts[1] : "Unknown error"),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Student panel methods
    private void viewAvailableCourses() {
        String studentId = loggedInStudentId;
        if (studentId == null || studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student not logged in");
            return;
        }

        String response = getAvailableCourses();
        String[] parts = response.split("\\|");

        if (parts[0].equals("COURSES")) {
            // Swap in a model with checkbox column
            DefaultTableModel checkboxModel = new DefaultTableModel(
                    new Object[] { "Select", "Course Code", "Description" }, 0) {
                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 0)
                        return Boolean.class;
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0;
                }
            };
            for (int i = 1; i < parts.length; i += 2) {
                if (i + 1 < parts.length) {
                    checkboxModel.addRow(new Object[] { false, parts[i], parts[i + 1] });
                }
            }
            studentCourseTable.setModel(checkboxModel);
            studentCourseTable.setRowHeight(24);
            // Checkbox column width
            int columnWidth = 50;
            studentCourseTable.getColumnModel().getColumn(0).setPreferredWidth(columnWidth);
            studentCourseTable.getColumnModel().getColumn(0).setMinWidth(columnWidth);
            studentCourseTable.getColumnModel().getColumn(0).setMaxWidth(columnWidth);
            studentTableModel = checkboxModel;
            int loaded = (parts.length - 1) / 2;
            JOptionPane.showMessageDialog(this,
                    loaded > 0 ? ("Loaded " + loaded + " available courses") : "No available courses found.");
        } else {
            String msg = (parts.length > 1 && !parts[1].trim().isEmpty()) ? parts[1] : "Unknown error";
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + msg,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewMyCourses() {
        String studentId = loggedInStudentId;
        if (studentId == null || studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student not logged in");
            return;
        }

        String response = getStudentCourses(studentId);
        String[] parts = response.split("\\|");

        if (parts[0].equals("ENROLLMENTS")) {
            // Swap in a model without checkbox column
            DefaultTableModel noCheckboxModel = new DefaultTableModel(new Object[] { "Course Code", "Description" },
                    0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            int courseCount = 0;
            for (int i = 1; i < parts.length; i += 2) {
                if (i + 1 < parts.length) {
                    noCheckboxModel.addRow(new Object[] { parts[i], parts[i + 1] });
                    courseCount++;
                }
            }
            studentCourseTable.setModel(noCheckboxModel);
            studentCourseTable.setRowHeight(22);
            studentTableModel = noCheckboxModel;
            JOptionPane.showMessageDialog(this, courseCount > 0 ? ("You are enrolled in " + courseCount + " course(s)")
                    : "You are not enrolled in any courses.");
        } else {
            String msg = (parts.length > 1 && !parts[1].trim().isEmpty()) ? parts[1] : "Unknown error";
            JOptionPane.showMessageDialog(this,
                    "Error loading your courses: " + msg);
        }
    }

    private void enrollInCourse() {
        String studentId = loggedInStudentId;
        if (studentId == null || studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student not logged in");
            return;
        }

        // Collect all checked course codes from the checkbox column
        List<String> courseCodes = new ArrayList<>();
        for (int row = 0; row < studentTableModel.getRowCount(); row++) {
            Boolean checked = (Boolean) studentTableModel.getValueAt(row, 0);
            if (checked != null && checked) {
                String code = studentTableModel.getValueAt(row, 1).toString();
                courseCodes.add(code);
            }
        }
        if (courseCodes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please check at least one course to enroll in");
            return;
        }
        String codesSelected = String.join(",", courseCodes);
        String request = "ENROLL_COURSES|" + studentId + "|" + codesSelected;
        String response = sendRequest(request);
        String[] parts = response.split("\\|", 2);

        if (parts[0].equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, parts.length > 1 ? parts[1] : "Enrollment successful.");
            // Uncheck all boxes after enrollment
            for (int row = 0; row < studentTableModel.getRowCount(); row++) {
                studentTableModel.setValueAt(false, row, 0);
            }
            viewMyCourses();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Enrollment failed: " + (parts.length > 1 ? parts[1] : "Unknown error"));
        }
    }

    private void deregisterFromCourse() {
        String studentId = loggedInStudentId;
        int selectedRow = studentCourseTable.getSelectedRow();

        if (studentId == null || studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student not logged in");
            return;
        }

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a course to deregister from");
            return;
        }

        String courseCode = studentCourseTable.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to deregister from " + courseCode + "?",
                "Confirm Deregistration", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String response = deregisterStudent(studentId, courseCode);
            String[] parts = response.split("\\|");

            if (parts[0].equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this, parts[1]);
                viewMyCourses();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Deregistration failed: " + (parts.length > 1 ? parts[1] : "Unknown error"));
            }
        }
    }

    // Admin panel methods
    private void switchAdminMode(String mode) {
        CardLayout cl = (CardLayout) adminFormContainer.getLayout();
        cl.show(adminFormContainer, mode);

        if (mode.equals("Student")) {
            adminTableModel.setColumnIdentifiers(new String[] { "Student ID", "First Name", "Last Name" });
            refreshStudents();
        } else if (mode.equals("Course")) {
            adminTableModel.setColumnIdentifiers(new String[] { "Course Code", "Description" });
            refreshCourses();
        }
    }

    private void addNewStudent() {
        String firstName = adminFirstName.getText().trim();
        String lastName = adminLastName.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Only send first and last name to server; server generates student number
        String response = addStudent(firstName, lastName);
        String[] parts = response.split("\\|", 2);

        if (parts[0].equals("SUCCESS")) {
            // Show the friendly message from the server (includes student number)
            JOptionPane.showMessageDialog(this, parts.length > 1 ? parts[1] : "Student added successfully!");
            adminStudentId.setText("");
            adminFirstName.setText("");
            adminLastName.setText("");
            refreshStudents();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error adding student: " + (parts.length > 1 ? parts[1] : "Unknown error"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewCourse() {
        String courseCode = adminCourseCode.getText().trim();
        String description = adminCourseDesc.getText().trim();

        if (courseCode.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields",
                    "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String response = addCourse(courseCode, description);
        String[] parts = response.split("\\|");

        if (parts[0].equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, parts[1]);
            adminCourseCode.setText("");
            adminCourseDesc.setText("");
            refreshCourses();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error adding course: " + (parts.length > 1 ? parts[1] : "Unknown error"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshStudents() {
        String response = getAllStudents();
        String[] parts = response.split("\\|");

        if (parts[0].equals("STUDENTS")) {
            adminTableModel.setRowCount(0);
            for (int i = 1; i < parts.length; i += 3) {
                if (i + 2 < parts.length) {
                    adminTableModel.addRow(new Object[] { parts[i], parts[i + 1], parts[i + 2] });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error loading students: " + (parts.length > 1 ? parts[1] : "Unknown error"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCourses() {
        String response = getAvailableCourses();
        String[] parts = response.split("\\|");

        if (parts[0].equals("COURSES")) {
            adminTableModel.setRowCount(0);
            for (int i = 1; i < parts.length; i += 2) {
                if (i + 1 < parts.length) {
                    adminTableModel.addRow(new Object[] { parts[i], parts[i + 1] });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + (parts.length > 1 ? parts[1] : "Unknown error"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set Look and Feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new Enrollment_system();
        });
    }
}