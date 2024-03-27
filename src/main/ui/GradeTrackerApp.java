package ui;

import model.Course;
import model.Grade;
import model.Student;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Grade Tracker App console based UI
public class GradeTrackerApp {

    private final List<Student> students;
    private Scanner input;
    private final List<Course> courses; // Assuming you have a list of courses
    private Grade grade;
    static final String JSON_STORE = "gradeTracker.json"; // File path for the JSON data
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;

    public GradeTrackerApp() {
        students = new ArrayList<>();
        courses = new ArrayList<>();
        grade = new Grade();
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GradeTrackerGUI(students, courses);
            }
        });
        runGradeTracker();
    }

    // Effects: Run the app
    private void runGradeTracker() {
        boolean keepGoing = true;
        input = new Scanner(System.in);
        String command;

        while (keepGoing) {
            displayMenu();
            command = input.nextLine();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }
        }

        System.out.println("\nGoodbye!");
    }

    // Effects: displays the menu for the application
    private void displayMenu() {
        System.out.println("\nSelect from:");
        System.out.println("\ta -> Add a student");
        System.out.println("\tc -> Add a course");
        System.out.println("\te -> Enter grades");
        System.out.println("\tg -> Calculate GPA");
        System.out.println("\tr -> Generate report");
        System.out.println("\ts -> Summary view");
        System.out.println("\tsv -> Save data");
        System.out.println("\tld -> Load data");
        System.out.println("\tclr -> Clear save");
        System.out.println("\tq -> Quit");
    }


    // Modifies: this
    // Effects: process various commands
    
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void processCommand(String command) {
        switch (command) {
            case "a":
                doAddStudent();
                break;
            case "c":
                doAddCourse();
                break;
            case "e":
                doEnterGrades();
                break;
            case "g":
                doCalculateGPA();
                break;
            case "r":
                doGenerateReport();
                break;
            case "s":
                doSummaryView();
                break;
            case "sv":
                saveData();
                break;
            case "ld":
                loadData();
                break;
            case "clr":
                clearSaveData();
                break;
            case "q":
                saveOnQuit();
                break;
            default:
                printInvalidSelection();
        }
    }

    // Effects: prints "Selection not valid. Please try again." when nothing from the list of commands is selected.
    private static void printInvalidSelection() {
        System.out.println("Selection not valid. Please try again.");
    }

    // EFFECTS: saves state to file on quit after user confirmation
    private void saveOnQuit() {
        System.out.println("Would you like to save your changes before quitting? (y/n)");
        String inputString = input.nextLine().trim().toLowerCase();
        if (inputString.equals("y")) {
            saveData();
        }
    }

    // MODIFIES: this
    // EFFECTS: clears the saved JSON data
    private void clearSaveData() {
        System.out.println("Are you sure you want to clear all saved data? This cannot be undone. (y/n)");
        String inputString = input.nextLine().trim().toLowerCase();
        if (inputString.equals("y")) {
            try {
                jsonWriter.open();
                jsonWriter.write(new ArrayList<>(), new ArrayList<>()); // Writing empty lists to the file
                jsonWriter.close();
                System.out.println("All saved data has been cleared.");
            } catch (FileNotFoundException e) {
                System.out.println("Unable to access the file: " + JSON_STORE);
            }
        } else {
            System.out.println("Data clear cancelled.");
        }
    }


    // MODIFIES: this
    // EFFECTS: loads students and courses from file
    public void loadData() {
        try {
            JsonReader.Pair<List<Student>, List<Course>> data = jsonReader.read();
            students.clear();
            courses.clear();
            students.addAll(data.first);
            courses.addAll(data.second);
            System.out.println("Data loaded successfully from " + JSON_STORE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }


    // MODIFIES: this
    // EFFECTS: saves the current state to the JSON file
    public void saveData() {
        try {
            for (Course course : courses) {
                System.out.println("Saving course: " + course.getCourseName());
                System.out.println("Enrolled students IDs: " + course.getEnrolledStudentsID());
                System.out.println("Student grades: " + course.getStudentGrades());
            }

            jsonWriter.open();
            jsonWriter.write(students, courses);
            jsonWriter.close();
            System.out.println("Data saved successfully to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    // Requires: name should be string, ID should be integer
    // Modifies: this
    // Effects: adds student with the given name, ID and list of enrolled courses.
    private void doAddStudent() {
        System.out.println("Enter the student's name:");
        String name = input.nextLine();
        Integer id = getCredits("Enter the student's ID:", "Invalid ID format. Please enter a number.");
        if (id == null) {
            return;
        }
        if (findStudentById(id) != null) {
            System.out.println("A student with this ID already exists.");
            return;
        }
        Student newStudent = new Student(name, id);
        boolean addingCourses = true;
        while (addingCourses) {
            System.out.println("Enter the course code the student is enrolling in, or 'done' to finish:");
            String inputCourseCode = input.nextLine();
            addingCourses = addCoursesByCode(newStudent, addingCourses, inputCourseCode);
        }

        students.add(newStudent);
        System.out.println("New student added: " + name + " with ID " + id);
    }

    // Requires: there should be a course
    // Modifies: this
    // Effects: adds the course to the student's list of enrolled courses.
    private boolean addCoursesByCode(Student newStudent, boolean addingCourses, String inputCourseCode) {
        if ("done".equalsIgnoreCase(inputCourseCode)) {
            return false;
        } else {
            Course course = findCourseByCode(inputCourseCode);
            if (course != null) {
                newStudent.addCourse(course);
                course.enrollStudent(newStudent);
                System.out.println("Student enrolled in course: " + course.getCourseName());
            } else {
                System.out.println("Course not found with code: " + inputCourseCode);
            }
        }
        return true;
    }


    // Requires: name should be string, code should be String, description should be String, credits should be integer,
    //           percentage should a number and courseId should be integer.
    // Modifies: this
    // Effects: adds course with the given name, code, description, credits, percentage grade and courseId.
    private void doAddCourse() {
        System.out.println("Enter the course name:");
        String name = input.nextLine();

        System.out.println("Enter the course code:");
        String code = input.nextLine();

        System.out.println("Enter the course description:");
        String description = input.nextLine();

        Integer credits = getCredits("Enter the number of credits for the course:",
                "Invalid credits format. Please enter a number.");
        if (credits == null) {
            return;
        }
        Double percentageGrade = getGrade();
        if (percentageGrade == null) {
            return;
        }
        if (findCourseByCode(code) != null) {
            System.out.println("A course with this code already exists.");
            return;
        }
        Integer courseID = getId();
        extractedInfo(name, code, description, credits, percentageGrade, courseID);
    }

    // Effects: add the new course and prints "New course added:" followed by the name of the course and
    //          code (in brackets).
    private void extractedInfo(String name, String code, String description, Integer credits, Double percentageGrade,
                               Integer courseID) {
        if (courseID == null) {
            return;
        }

        Course newCourse = new Course(name, code, description, courseID, credits, percentageGrade);
        courses.add(newCourse);
        System.out.println("New course added: " + name + " (" + code + ")");
    }


    // Effects: returns the courseID.
    private Integer getId() {
        System.out.println("Enter the courseID:");
        int courseID;
        try {
            courseID = Integer.parseInt(input.nextLine());
            // Check if a course with this ID already exists to prevent duplicates
            if (findCourseById(courseID) != null) {
                System.out.println("A course with this ID already exists.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a number.");
            return null;
        }
        return courseID;
    }

    // Effects: returns the maximum course grade.
    private Double getGrade() {
        System.out.println("Enter the maximum percentage grade allowed for the course:");
        double percentageGrade;
        try {
            percentageGrade = Double.parseDouble(input.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid percentage format. Please enter a decimal number.");
            return null;
        }
        return percentageGrade;
    }

    // Effects: returns the total credits of the course.
    private Integer getCredits(String x, String x1) {
        System.out.println(x);
        int credits;
        try {
            credits = Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(x1);
            return null;
        }
        return credits;
    }


    // Requires: there should be a course and a student
    // Modifies: this
    // Effects: adds grade to the corresponding course in the selected student.
    private void doEnterGrades() {
        try {
            System.out.println("Enter student ID:");
            int studentId = Integer.parseInt(input.nextLine());
            Student student = findStudentById(studentId);
            if (student == null) {
                System.out.println("Student not found!");
                return;
            }

            System.out.println("Enter course code:");
            String courseCode = input.nextLine();
            Course course = findCourseByCode(courseCode);
            if (course == null) {
                System.out.println("Course not found!");
                return;
            }

            System.out.println("Enter grade:");
            double grade = Double.parseDouble(input.nextLine());
            course.addGrade(student, grade);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. PLease enter a valid number.");
        }
    }

    // Requires: there should be a student and a course.
    // Modifies: this
    // Effects: calculates GPA for the given student over his all enrolled courses.
    private void doCalculateGPA() {
        System.out.println("Enter student ID to calculate GPA:");
        int studentId = input.nextInt();
        input.nextLine();

        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("No student found with ID: " + studentId);
            return;
        }

        List<Course> grades = new ArrayList<>();
        for (Integer courseId : student.getEnrolledCourses()) {
            Course course = findCourseById(courseId);
            if (course != null) {
                grades.add(course);
            }
        }

        double gpa = grade.calculateGPA(grades);
        System.out.printf("The GPA for student ID %d is: %.2f%n", studentId, gpa);
    }


    // Requires: there should be a student and a course.
    // Modifies: this
    // Effects: generates report of the given student.
    private void doGenerateReport() {
        System.out.println("Enter student ID:");
        int studentId = Integer.parseInt(input.nextLine());
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        String report = generateReport(student);
        System.out.println(report);
    }


    // Requires: there should be a student and a course.
    // Modifies: this
    // Effects: Shows the summary view of grade distributions for a all courses
    private void doSummaryView() {
        // Prints the header for the summary
        System.out.println("Summary of Courses:");
        System.out.println(String.format("%-10s %-30s %-10s %-15s %-15s %-15s", "Course ID", "Course Name", "Credits",
                "Average Grade","Median Grade", "Std Deviation"));

        // Loops through all courses and print their details
        for (Course course : courses) {
            double averageGrade = course.calculateAverageGrade();
            double medianGrade = course.calculateMedianGrade();
            double stdDeviation = course.calculateStandardDeviation();

            // Format the output for better readability
            System.out.println(String.format("%-10d %-30s %-10d %-15.2f %-15.2f %-15.2f",
                    course.getCourseID(),
                    course.getCourseName(),
                    course.getCredits(),
                    averageGrade,
                    medianGrade,
                    stdDeviation));
        }
    }


    // HELPERS:

    // Effects: finds the student using ID
    private Student findStudentById(int id) {
        for (Student student : students) {
            if (student.getStudentID() == id) {
                return student;
            }
        }
        return null;
    }

    // Effects: finds the course using course ID
    private Course findCourseById(int id) {
        for (Course course : courses) {
            if (course.getCourseID() == id) {
                return course;
            }
        }
        return null;
    }

    // Effects: finds the student using course code
    private Course findCourseByCode(String code) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(code)) {
                return course;
            }
        }
        return null;
    }

    // Effects: Generates a collective report on the given student.
    private String generateReport(Student student) {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Report for Student ID: ").append(student.getStudentID()).append("\n");
        reportBuilder.append("Name: ").append(student.getName()).append("\n\n");
        reportBuilder.append("Courses Enrolled:\n");

        for (Integer courseId : student.getEnrolledCourses()) {
            Course currentCourse = findCourseById(courseId);
            Double courseGrade = currentCourse.getGrade(student);
            if (currentCourse != null) {
                if (courseGrade != null) {
                    getInformation(reportBuilder, currentCourse, courseGrade);
                } else {
                    reportBuilder.append(currentCourse.getCourseName())
                            .append(" (")
                            .append(currentCourse.getCourseCode())
                            .append(") - Grade: Not available\n");
                }

            }
        }

        double gpa = grade.calculateGPA(getStudentCourses(student));
        reportBuilder.append("\nCumulative GPA: ").append(String.format("%.2f", gpa)).append("\n");

        return reportBuilder.toString();
    }

    // Modifies: reportBuilder
    // Effects: appends course information and letter grade to reportBuilder
    private void getInformation(StringBuilder reportBuilder, Course course, double grade) {
        String letterGrade = this.grade.percentageToLetterGrade(grade);
        reportBuilder.append(course.getCourseName())
                .append(" (")
                .append(course.getCourseCode())
                .append(") - Grade: ")
                .append(letterGrade)
                .append("\n");
    }

    // Effects: get the List of Course objects from a student's enrolled courses IDs
    private List<Course> getStudentCourses(Student student) {
        List<Course> studentCourses = new ArrayList<>();
        for (Integer courseId : student.getEnrolledCourses()) {
            Course course = findCourseById(courseId);
            if (course != null) {
                studentCourses.add(course);
            }
        }
        return studentCourses;
    }

}
