package ui;

import model.Event;
import model.*;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GradeTrackerGUI {
    private JFrame frame;
    private JPanel sidebar;
    private JPanel contentArea;
    private JMenuBar menuBar;
    private final List<Student> students;
    private final List<Course> courses;
    private Grade grade;
    static final String JSON_STORE = "gradeTracker.json";
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private JTextArea displayArea;
    private JTextArea courseSummaryDisplayArea;


    // GradeTrackerApp GUI constructor and Initializes the application with the provided lists of students and courses.
    public GradeTrackerGUI() {
        this.students = new ArrayList<>();
        this.courses = new ArrayList<>();
        grade = new Grade();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);

        setupStartupImage();
        initFrame();
        createAndShowGUI();
    }


    // Effects: constructs a frame for the Grade Tracker Application and disposes it off when application is quit.
    private void initFrame() {
        frame = new JFrame("Grade Tracker Application");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                System.out.println("Attempting to quit the application.");
                System.out.println("User confirmed quit. Printing Log and Closing application.");
                System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                printLoggedEvents();
                frame.dispose();
                System.exit(0);
            }
        });
    }

    // Effects: prints all the logged events.
    private void printLoggedEvents() {
        // Header
        System.out.println("---- Application Event Log Start ----");
        // Fetch the EventLog instance
        EventLog eventLog = EventLog.getInstance();
        // Print each event to the console
        for (Event e : eventLog) {
            System.out.println(e.toString());
        }
        // Footer
        System.out.println("---- Application Event Log End ----");
    }

    /**
     * Creates and shows the GUI.
     * Modifies: this
     * Effects: Sets up the GUI components and makes the frame visible.
     */
    private void createAndShowGUI() {
        createSidebar();
        createMenuBar();

        displayArea = new JTextArea(15, 30);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        courseSummaryDisplayArea = new JTextArea(5, 30);
        courseSummaryDisplayArea.setEditable(false);
        courseSummaryDisplayArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        JScrollPane summaryScrollPane = new JScrollPane(courseSummaryDisplayArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, summaryScrollPane);
        splitPane.setResizeWeight(0.8);

        contentArea = new JPanel(new BorderLayout());
        contentArea.add(splitPane, BorderLayout.CENTER);
        // Set up a border for spacing
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(contentArea, BorderLayout.CENTER);
        frame.setJMenuBar(menuBar);

        frame.setMinimumSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Now update the display with the initial data
        updateDisplay();
    }

    /**
     * Updates the display area with the current data.
     * Modifies: this
     * Effects: Updates the display area to show the current list of students and their enrolled courses.
     */
    private void updateDisplay() {
        StringBuilder sb = new StringBuilder("Students and Enrolled Courses:\n");

        if (!students.isEmpty()) {
            for (Student student : students) {
                sb.append(student.getName()).append(" (ID: ").append(student.getStudentID()).append(")\n");
                List<Course> enrolledCourses = getCoursesForStudent(student);
                for (Course course : enrolledCourses) {
                    sb.append("  - ").append(course.getCourseName()).append("\n");
                }
                sb.append("\n");
            }
        }
        displayArea.setText(sb.toString());
        updateCourseEnrollmentSummary();
    }

    /**
     * Updates the course summary display area with the current data.
     * Modifies: this
     * Effects: Updates the course summary display area to show the current total number of students in the courses.
     */
    private void updateCourseEnrollmentSummary() {
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("Total number of Enrolled Students:\n");

        for (Course course : courses) {
            long count = students.stream()
                    .filter(s -> s.getEnrolledCourses().contains(course.getCourseID()))
                    .count();
            summaryBuilder.append(String.format("Total Students in %s: %d%n", course.getCourseName(), count));
        }

        courseSummaryDisplayArea.setText(summaryBuilder.toString());
    }


    // Effects: sets up the startup image of the app.
    private void setupStartupImage() {
        JWindow startWindow = new JWindow();
        ImageIcon starScreenImage = new ImageIcon("./src/resources/GradeTracker.png");
        JLabel label = new JLabel(starScreenImage);
        startWindow.getContentPane().add(label);
        startWindow.pack();
        startWindow.setLocationRelativeTo(null);
        startWindow.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startWindow.dispose();
    }


    /**
     * Sets up the sidebar with buttons.
     * Modifies: this
     * Effects: Initializes and adds buttons to the sidebar for various actions.
     */
    private void createSidebar() {
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.PAGE_AXIS));

        addStudent();
        addCourse();
        enterGrades();
        calculateGpa();
        generateReport();
        summaryView();


        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Effects: Adds a "Summary View" button to the sidebar. When clicked, it triggers the doSummaryView()
     * method, which generates and displays a summary of all courses, their enrollments, and average grades.
     */
    private void summaryView() {
        JButton summaryViewButton = new JButton("Summary View");
        summaryViewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryViewButton.addActionListener(e -> doSummaryView());
        sidebar.add(summaryViewButton);
    }

    /**
     * Effects: Adds a "Generate Report" button to the sidebar. Clicking this button activates the doGenerateReport()
     * method, which prompts for a student ID and displays a detailed report of their courses and grades.
     */
    private void generateReport() {
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateReportButton.addActionListener(e -> doGenerateReport());
        sidebar.add(generateReportButton);
    }

    /**
     * Effects: Places a "Calculate GPA" button on the sidebar. When pressed, it calls doCalculateGPA(),
     * which calculates and shows the GPA for a specified student.
     */
    private void calculateGpa() {
        JButton calculateGpaButton = new JButton("Calculate GPA");
        calculateGpaButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateGpaButton.addActionListener(e -> doCalculateGPA());
        sidebar.add(calculateGpaButton);
    }

    /**
     * Modifies: Updates the grades of students for selected courses.
     * Effects: Adds an "Enter Grades" button to the sidebar. Activating this button executes doEnterGrades(),
     * enabling the entry of grades for students in specific courses.
     */
    private void enterGrades() {
        JButton enterGradesButton = new JButton("Enter Grades");
        enterGradesButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        enterGradesButton.addActionListener(e -> doEnterGrades());
        sidebar.add(enterGradesButton);
    }

    /**
     * Modifies: The courses list by adding a new Course object.
     * Effects: Introduces an "Add Course" button to the sidebar. On click, doAddCourse() is invoked,
     * which opens a dialog to enter new course details and, upon validation, adds a new course to the system.
     */
    private void addCourse() {
        JButton addCourseButton = new JButton("Add Course");
        addCourseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addCourseButton.addActionListener(e -> doAddCourse());
        sidebar.add(addCourseButton);
    }

    /**
     * Modifies: Alters the students list by including a new Student object.
     * Effects: Implements an "Add Student" button on the sidebar. Clicking this button initiates doAddStudent(),
     * presenting a dialog to collect new student information and adding the student to the application upon
     * confirmation.
      */
    private void addStudent() {
        JButton addStudentButton = new JButton("Add Student");
        addStudentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addStudentButton.addActionListener(e -> doAddStudent());
        sidebar.add(addStudentButton);
    }


    /**
     * Creates the menu bar.
     * Modifies: this
     * Effects: Sets up the menu bar with options for loading, saving, clearing data, and quitting the app.
     */
    private void createMenuBar() {
        menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // Load Data MenuItem
        JMenuItem loadItem = new JMenuItem("Load Data");
        loadItem.addActionListener(e -> doLoadData());
        fileMenu.add(loadItem);

        // Save Data MenuItem
        JMenuItem saveItem = new JMenuItem("Save Data");
        saveItem.addActionListener(e -> doSaveData());
        fileMenu.add(saveItem);

        // Clear Data MenuItem
        JMenuItem clearItem = new JMenuItem("Clear Data");
        clearItem.addActionListener(e -> doClearData());
        fileMenu.add(clearItem);

        // Quit MenuItem
        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(e -> doQuitApp());
        fileMenu.add(quitItem);

        // Event Log Menu
        JMenu eventLogMenu = new JMenu("Event Log");
        menuBar.add(eventLogMenu);


        // View Log MenuItem
        JMenuItem viewLogItem = new JMenuItem("View Log");
        viewLogItem.addActionListener(e -> doViewEventLog());
        eventLogMenu.add(viewLogItem);

        // Clear Log MenuItem
        JMenuItem clearLogItem = new JMenuItem("Clear Log");
        clearLogItem.addActionListener(e -> doClearEventLog());
        eventLogMenu.add(clearLogItem);
    }


    /**
     * Displays the event log.
     * Effects: Shows the event log in a dialog or the display area.
     */
    private void doViewEventLog() {
        EventLog eventLog = EventLog.getInstance();
        StringBuilder logBuilder = new StringBuilder();
        for (Event each : eventLog) {
            logBuilder.append(each.toString()).append("\n\n");
        }

        // display in a pop-up dialog
        JTextArea logTextArea = new JTextArea(25, 40);
        logTextArea.setText(logBuilder.toString());
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        JOptionPane.showMessageDialog(frame, scrollPane, "Event Log", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Clears the event log.
     * Effects: Clears the event log and shows a confirmation message.
     */
    private void doClearEventLog() {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear the event log?",
                "Clear Event Log Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            EventLog.getInstance().clear();
            JOptionPane.showMessageDialog(frame, "The event log has been cleared.", "Event Log Cleared",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Adds a new student.
     * Modifies: students
     * Effects: Adds a new student to the list with the entered details.
     */
    private void doAddStudent() {
        JTextField nameField = new JTextField(5);
        JTextField idField = new JTextField(5);

        // Create a list model to hold courses
        DefaultListModel<Course> listModel = new DefaultListModel<>();
        courses.forEach(listModel::addElement);

        JList<Course> courseList = getStudentList(listModel);

        JScrollPane courseScrollPane = new JScrollPane(courseList);

        JPanel panel = getStudentPanel(nameField, idField, courseScrollPane);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Student Details",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String idText = idField.getText().trim();

            tryForStudent(courseList, name, idText);
        }
    }

    // helper method
    private void tryForStudent(JList<Course> courseList, String name, String idText) {
        try {
            int id = Integer.parseInt(idText);
            if (students.stream().anyMatch(student -> student.getStudentID() == id)) {
                JOptionPane.showMessageDialog(frame, "A student with this ID already exists.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                Student newStudent = new Student(name, id);
                // Get the selected courses from the list
                List<Course> selectedCourses = courseList.getSelectedValuesList();
                for (Course course : selectedCourses) {
                    newStudent.addCourse(course);
                    course.enrollStudent(newStudent);
                }

                students.add(newStudent);
                JOptionPane.showMessageDialog(frame, "Student added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                SwingUtilities.invokeLater(this::updateDisplay);

            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "ID must be an integer", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    // helper method
    private JList<Course> getStudentList(DefaultListModel<Course> listModel) {
        // JList for selecting courses with custom renderer
        JList<Course> courseList = new JList<>(listModel);
        courseList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    setText(((Course) value).getCourseName());
                }
                return this;
            }
        });
        return courseList;
    }

    // helper method
    private static JPanel getStudentPanel(JTextField nameField, JTextField idField, JScrollPane courseScrollPane) {
        JPanel panel = new JPanel(new GridLayout(1,2));

        JPanel panelUp = new JPanel(new GridLayout(4,1,2,2));
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        panelUp.add(nameLabel);
        panelUp.add(nameField);
        JLabel idLabel = new JLabel("ID:");
        idLabel.setHorizontalAlignment(JLabel.CENTER);
        panelUp.add(idLabel);
        panelUp.add(idField);

        JPanel panelDown = new JPanel(new BorderLayout());

        JLabel coursesText = new JLabel("List of Courses Available");
        coursesText.setHorizontalAlignment(JLabel.CENTER);
        panelDown.add(coursesText,BorderLayout.NORTH);
        panelDown.add(courseScrollPane,BorderLayout.SOUTH);

        panel.add(panelUp,BorderLayout.WEST);
        panel.add(panelDown,BorderLayout.EAST);

        return panel;
    }

    /**
     * Adds a new course.
     * Modifies: courses
     * Effects: Adds a new course to the list with the entered details.
     */
    private void doAddCourse() {
        JTextField courseNameField = new JTextField(5);
        JTextField courseCodeField = new JTextField(5);
        JTextField courseDescriptionField = new JTextField(5);
        JTextField courseIDField = new JTextField(5);
        JTextField creditsField = new JTextField(5);
        JTextField percentageGradeField = new JTextField(5);

        JPanel myPanel = getCoursePanel(courseNameField, courseCodeField, courseDescriptionField, courseIDField,
                creditsField, percentageGradeField);

        int result = JOptionPane.showConfirmDialog(frame, myPanel,
                "Please Enter Course Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (tryForCourse(courseNameField, courseCodeField, courseDescriptionField, courseIDField, creditsField,
                    percentageGradeField)) {
                return;
            }
        }
        updateDisplay();
    }

    // helper method
    private boolean tryForCourse(JTextField courseNameField, JTextField courseCodeField,
                                 JTextField courseDescriptionField, JTextField courseIDField,
                                 JTextField creditsField, JTextField percentageGradeField) {
        try {
            String courseName = courseNameField.getText().trim();
            String courseCode = courseCodeField.getText().trim();
            String courseDescription = courseDescriptionField.getText().trim();
            int courseID = Integer.parseInt(courseIDField.getText().trim());
            int credits = Integer.parseInt(creditsField.getText().trim());
            double percentageGrade = Double.parseDouble(percentageGradeField.getText().trim());

            // Validate input data as necessary
            if (courseName.isEmpty() || courseCode.isEmpty() || courseDescription.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please ensure all fields are filled out correctly.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return true;
            }

            Course newCourse = new Course(courseName, courseCode, courseDescription, courseID,
                    credits, percentageGrade);
            courses.add(newCourse);
            JOptionPane.showMessageDialog(frame, "Course added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame,
                    "Ensure numeric fields (Course ID, Credits, Percentage Grade) contain valid numbers.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // helper method
    private static JPanel getCoursePanel(JTextField courseNameField, JTextField courseCodeField,
                                         JTextField courseDescriptionField, JTextField courseIDField,
                                         JTextField creditsField, JTextField percentageGradeField) {
        JPanel myPanel = new JPanel(new GridLayout(0, 2, 2, 2));
        myPanel.add(new JLabel("Course Name:"));
        myPanel.add(courseNameField);
        myPanel.add(new JLabel("Course Code:"));
        myPanel.add(courseCodeField);
        myPanel.add(new JLabel("Description:"));
        myPanel.add(courseDescriptionField);
        myPanel.add(new JLabel("Course ID:"));
        myPanel.add(courseIDField);
        myPanel.add(new JLabel("Credits:"));
        myPanel.add(creditsField);
        myPanel.add(new JLabel("Maximum Percentage Grade:"));
        myPanel.add(percentageGradeField);
        return myPanel;
    }


    /**
     * Requires: Numeric grade input; at least one student and course must exist.
     * Modifies: The selected student's grade for a chosen course.
     * Effects: Allows grade entry for a student in a course and updates the system.
     * Validates numeric input, showing success or error messages accordingly.
     */
    private void doEnterGrades() {
        // Create a JPanel to hold our form elements
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));

        JComboBox<Student> studentComboBox = getStudentJComboBox();

        JComboBox<Course> courseComboBox = getCourseJComboBox();

        // TextField for entering a grade
        JTextField gradeField = new JTextField();

        // Adding components to the panel
        getPanelForGrades(panel, studentComboBox, courseComboBox, gradeField);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(frame, panel, "Enter Grades", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Student selectedStudent = (Student) studentComboBox.getSelectedItem();
            Course selectedCourse = (Course) courseComboBox.getSelectedItem();
            String gradeText = gradeField.getText().trim();
            double grade;

            // Validate and parse the grade input
            try {
                grade = Double.parseDouble(gradeText);
                // Here, add the grade to the selected course for the selected student
                selectedCourse.addGrade(selectedStudent, grade);
                JOptionPane.showMessageDialog(frame, "Grade entered successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid grade format. Please enter a numeric value.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // helper method
    private static void getPanelForGrades(JPanel panel, JComboBox<Student> studentComboBox,
                                          JComboBox<Course> courseComboBox, JTextField gradeField) {
        panel.add(new JLabel("Select Student:"));
        panel.add(studentComboBox);
        panel.add(new JLabel("Select Course:"));
        panel.add(courseComboBox);
        panel.add(new JLabel("Enter Grade:"));
        panel.add(gradeField);
    }

    // helper method
    private JComboBox<Course> getCourseJComboBox() {
        // ComboBox for selecting a course with custom renderer
        JComboBox<Course> courseComboBox = new JComboBox<>();
        courses.forEach(courseComboBox::addItem);
        courseComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    setText(((Course) value).getCourseName());
                }
                return this;
            }
        });
        return courseComboBox;
    }

    // helper method
    private JComboBox<Student> getStudentJComboBox() {
        // ComboBox for selecting a student with custom renderer
        JComboBox<Student> studentComboBox = new JComboBox<>();
        students.forEach(studentComboBox::addItem);
        studentComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    setText(((Student) value).getName());
                }
                return this;
            }
        });
        return studentComboBox;
    }


    /**
     * Requires: there should be a student and a course.
     * Modifies: this
     * Effects: calculates GPA for the given student over his all enrolled courses.
     */
    private void doCalculateGPA() {
        String studentIdStr = JOptionPane.showInputDialog(frame, "Enter the Student ID to calculate GPA:",
                "Calculate GPA", JOptionPane.QUESTION_MESSAGE);
        if (studentIdStr != null && !studentIdStr.trim().isEmpty()) {
            tryForCalculateGpa(studentIdStr);
        }
    }

    // helper method
    private void tryForCalculateGpa(String studentIdStr) {
        try {
            int studentId = Integer.parseInt(studentIdStr.trim());
            Student student = students.stream()
                    .filter(s -> s.getStudentID() == studentId)
                    .findFirst()
                    .orElse(null);
            if (student == null) {
                JOptionPane.showMessageDialog(frame, "Student not found!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prepare details for the message dialog
            String studentInfo = String.format("Name: %s (ID: %d)", student.getName(), student.getStudentID());
            String enrolledCourses = "Courses enrolled: \n" + getCoursesForStudent(student).stream()
                    .map(course -> "  - " + course.getCourseName() + " (" + course.getCourseCode() + ")")
                    .collect(Collectors.joining("\n"));
            double gpa = grade.calculateGPA(getCoursesForStudent(student));
            String gpaInfo = String.format("GPA: %.2f", gpa);

            // Combine all info into one message
            String message = studentInfo + "\n\n" + enrolledCourses + "\n\n" + gpaInfo;

            JOptionPane.showMessageDialog(frame, message, "GPA Calculated", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid Student ID format.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to get Course objects from a student's enrolled courses IDs
    private List<Course> getCoursesForStudent(Student student) {
        return student.getEnrolledCourses().stream()
                .map(courseId -> courses.stream()
                        .filter(course -> course.getCourseID() == courseId)
                        .findFirst()
                        .orElse(null))
                .collect(Collectors.toList());
    }

    /**
     * Requires: there should be a student and a course.
     * Modifies: this
     * Effects: generates report of the given student.
     */
    private void doGenerateReport() {
        // Prompt for student ID
        String studentIdStr = JOptionPane.showInputDialog(frame, "Enter Student ID for Report:",
                "Generate Report", JOptionPane.QUESTION_MESSAGE);
        if (studentIdStr != null && !studentIdStr.trim().isEmpty()) {
            tryForGenerateReport(studentIdStr);
        }
    }

    // helper method
    private void tryForGenerateReport(String studentIdStr) {
        try {
            int studentId = Integer.parseInt(studentIdStr.trim());
            // Find the student
            Student student = students.stream()
                    .filter(s -> s.getStudentID() == studentId)
                    .findFirst()
                    .orElse(null);

            if (student == null) {
                JOptionPane.showMessageDialog(frame, "Student not found!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            StringBuilder report = getStringBuilderForLoop(student);

            // Display the report
            JTextArea textArea = new JTextArea(6, 25);
            textArea.setText(report.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(frame, scrollPane, "Student Report",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid Student ID format.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // helper method
    private StringBuilder getStringBuilderForLoop(Student student) {
        // Generate the report
        StringBuilder report = new StringBuilder();
        report.append("Report for ").append(student.getName()).append(" (ID: ")
                .append(student.getStudentID()).append(")\n\n");
        report.append("Enrolled Courses and Grades:\n");
        for (Integer courseId : student.getEnrolledCourses()) {
            Course course = courses.stream()
                    .filter(c -> c.getCourseID() == courseId)
                    .findFirst()
                    .orElse(null);
            if (course != null) {
                Double grade = course.getGrade(student);
                report.append(course.getCourseName())
                        .append(" (").append(course.getCourseCode()).append("): ")
                        .append(grade != null ? grade : "No grade")
                        .append("\n");
            }
        }
        return report;
    }

    /**
     * Requires: there should be a student and a course.
     * Modifies: this
     * Effects: Shows the summary view of grade distributions for a all courses
     */
    private void doSummaryView() {
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append(String.format("%-10s %-30s %-20s %-15s %n", "Course ID", "Course Name", "Enrollment",
                "Average Grade"));

        for (Course course : courses) {
            double averageGrade = course.calculateAverageGrade();
            int enrollment = course.getEnrolledStudentsID().size();
            summaryBuilder.append(String.format("%-10d %-30s %-20d %-15.2f %n", course.getCourseID(),
                    course.getCourseName(), enrollment, averageGrade));
        }

        JTextArea textArea = new JTextArea(10, 50);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setText(summaryBuilder.toString());
        textArea.setEditable(false);
//        textArea.setSize(1000,textArea.getHeight());
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(frame, scrollPane, "Summary View", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Saves the current data to a file.
     * Effects: Prompts the user to select a file and saves the current data to it.
     */
    private void doSaveData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save data");
        fileChooser.setApproveButtonText("Save");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
        int userSelection = fileChooser.showSaveDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Ensure it has the .json extension
            if (!fileToSave.getName().toLowerCase().endsWith(".json")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".json");
            }

            tryForSaveData(fileToSave);
        }
        updateDisplay();
    }

    // helper method
    private void tryForSaveData(File fileToSave) {
        try {
            jsonWriter = new JsonWriter(fileToSave.getAbsolutePath());
            jsonWriter.open();
            jsonWriter.write(students, courses);
            jsonWriter.close();
            JOptionPane.showMessageDialog(frame, "Data saved successfully to "
                            + fileToSave.getAbsolutePath(),"Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Unable to write to file: " + fileToSave.getAbsolutePath(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while saving the data: "
                            + e.getMessage(),"Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads data from a file.
     * Modifies: students, courses
     * Effects: Prompts the user to select a file and loads data from it into the application.
     */
    private void doLoadData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a file to load data from");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON Files", "json"));

        int userSelection = fileChooser.showOpenDialog(frame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            String filePath = fileToLoad.getAbsolutePath();
            loadDataFromFile(filePath);
        }
        updateDisplay();
    }

    // helper method
    private void loadDataFromFile(String filePath) {
        try {
            jsonReader = new JsonReader(filePath);
            JsonReader.Pair<List<Student>, List<Course>> data = jsonReader.read();
            students.clear();
            students.addAll(data.first);
            courses.clear();
            courses.addAll(data.second);
            JOptionPane.showMessageDialog(frame, "Data loaded successfully from " + filePath,
                    "Load Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to read from file: " + filePath, "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "An error occurred while loading the data.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // For debugging
        }
    }

    /**
     * Clears all data from the application.
     * Modifies: students, courses
     * Effects: Clears all student and course data from the application.
     */
    private void doClearData() {
        // Show a confirmation dialog to avoid accidental clears
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear all data?",
                "Clear Data Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Clear the lists
            students.clear();
            courses.clear();
            // Optionally, refresh the GUI or show a message
            JOptionPane.showMessageDialog(frame, "All data has been cleared.", "Data Cleared",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        updateDisplay();
    }

    /**
     * Quits the application.
     * Effects: Prompts for confirmation and exits the application if confirmed.
     */
    private void doQuitApp() {
        System.out.println("Attempting to quit the application.");
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to quit without saving?",
                "Quit Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("User confirmed quit. Printing Log and Closing application.");
            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            printLoggedEvents(); // Print the event log before exiting
            frame.dispose();
            System.exit(0);
        } else {
            System.out.println("User canceled quit. Remaining open.");
        }
    }
}

