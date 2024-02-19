package ui;

import model.Course;
import model.Grade;
import model.Student;

import java.util.*;

public class GradeTrackerApp {

    private final List<Student> students;
    private Scanner input;
    private final List<Course> courses; // Assuming you have a list of courses
    private Grade grade;

    public GradeTrackerApp() {
        students = new ArrayList<>();
        courses = new ArrayList<>();
        grade = new Grade();
        runGradeTracker();
    }

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

    private void displayMenu() {
        System.out.println("\nSelect from:");
        System.out.println("\ta -> add student");
        System.out.println("\tc -> add course");
        System.out.println("\te -> enter grades");
        System.out.println("\tg -> calculate GPA");
        System.out.println("\tr -> generate report");
        System.out.println("\ts -> summary view");
        System.out.println("\tq -> quit");
    }


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
            case "q":
                break;
            default:
                extracted();
        }
    }

    private static void extracted() {
        System.out.println("Selection not valid. Please try again.");
        return;
    }

    private void doAddStudent() {
        System.out.println("Enter the student's name:");
        String name = input.nextLine();
        Integer id = getCredits("Enter the student's ID:", "Invalid ID format. Please enter a number.");
        if (id == null) {
            return;
        }

        // Check if a student with this ID already exists to prevent duplicates
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

    private boolean addCoursesByCode(Student newStudent, boolean addingCourses, String inputCourseCode) {
        if ("done".equalsIgnoreCase(inputCourseCode)) {
            return false; // No more courses to add
        } else {
            Course course = findCourseByCode(inputCourseCode);
            if (course != null) {
                newStudent.addCourse(course);
                System.out.println("Student enrolled in course: " + course.getCourseName());
            } else {
                System.out.println("Course not found with code: " + inputCourseCode);
            }
        }
        return true; // Continue adding courses
    }


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

        // The percentage grade needs to be defined or obtained from input
        Double percentageGrade = getGrade();
        if (percentageGrade == null) {
            return;
        }

        // Check if a course with this code already exists to prevent duplicates
        if (findCourseByCode(code) != null) {
            System.out.println("A course with this code already exists.");
            return;
        }

        Integer courseID = getId();
        extractedInfo(name, code, description, credits, percentageGrade, courseID);
    }

    private void extractedInfo(String name, String code, String description, Integer credits, Double percentageGrade,
                               Integer courseID) {
        if (courseID == null) {
            return;
        }

        Course newCourse = new Course(name, code, description, courseID, credits, percentageGrade);
        courses.add(newCourse);
        System.out.println("New course added: " + name + " (" + code + ")");
    }

    private Integer getId() {
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

    private Double getGrade() {
        System.out.println("Enter the overall percentage grade for the course:");
        double percentageGrade;
        try {
            percentageGrade = Double.parseDouble(input.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid percentage format. Please enter a decimal number.");
            return null;
        }
        return percentageGrade;
    }

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


    private void doEnterGrades() {
        System.out.println("Enter student ID:");
        int studentId = Integer.parseInt(input.nextLine()); // Handle NumberFormatException appropriately
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
        double grade = Double.parseDouble(input.nextLine()); // Handle NumberFormatException appropriately
        course.addGrade(student, grade);
    }


    private void doCalculateGPA() {
        System.out.println("Enter student ID to calculate GPA:");
        int studentId = input.nextInt();
        input.nextLine();

        // Retrieve the student and their courses
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



    // Generates a report of grades for a student
    private void doGenerateReport() {
        System.out.println("Enter student ID:");
        int studentId = Integer.parseInt(input.nextLine()); // Handle NumberFormatException appropriately
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        // Assume generateReport method exists in Student or Course class
        String report = generateReport(student);
        System.out.println(report);
    }



    // Shows a summary view of grade distributions for a course
    private void doSummaryView() {
        // Prints the header for the summary
        System.out.println("Summary of Grades:");
        System.out.println(String.format("%-10s %-30s %-10s %-10s%n", "Course ID", "Course Name", "Credits",
                "Average Grade","Median Grade", "Std Deviation"));

        // Loops through all courses and print their details
        for (Course course : courses) {
            double averageGrade = course.calculateAverageGrade();
            double medianGrade = course.calculateMedianGrade();
            double stdDeviation = course.calculateStandardDeviation();

            // Format the output for better readability
            System.out.println(String.format("%-10d %-30s %-10d %-10.2f%n",
                    course.getCourseID(),
                    course.getCourseName(),
                    course.getCredits(),
                    averageGrade,
                    medianGrade,
                    stdDeviation));
        }
    }









    private Student findStudentById(int id) {
        for (Student student : students) {
            if (student.getStudentID() == id) {
                return student;
            }
        }
        return null;
    }

    private Course findCourseById(int id) {
        for (Course course : courses) {
            if (course.getCourseID() == id) {
                return course;
            }
        }
        return null;
    }

    private Course findCourseByCode(String code) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(code)) {
                return course;
            }
        }
        return null;
    }
    
    
    
    

//    private String generateReport(Student student) {
//        StringBuilder reportBuilder = new StringBuilder();
//        reportBuilder.append("Report for Student ID: ").append(student.getStudentID()).append("\n");
//        reportBuilder.append("Name: ").append(student.getName()).append("\n\n");
//        reportBuilder.append("Courses Enrolled:\n").append(student.getEnrolledCourses()).append("\n\n");
//
//
//        List<Course> grades = new ArrayList<>();
//        double gpa = calculateGPA(grades);
//
//        reportBuilder.append("\nCumulative GPA: ").append(String.format("%.2f", gpa));
//
//        return reportBuilder.toString();
//    }

    private String generateReport(Student student) {
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Report for Student ID: ").append(student.getStudentID()).append("\n");
        reportBuilder.append("Name: ").append(student.getName()).append("\n\n");
        reportBuilder.append("Courses Enrolled:\n");


        double gpa = grade.calculateGPA(courses);
        reportBuilder.append("\nCumulative GPA: ").append(String.format("%.2f", gpa)).append("\n");

        return reportBuilder.toString();
    }

    private String getInformation(StringBuilder reportBuilder, Course course, double grade) {
        String letterGrade = this.grade.percentageToLetterGrade(grade);
        reportBuilder.append(course.getCourseName())
                .append(" (")
                .append(course.getCourseCode())
                .append(") - Grade: ")
                .append(letterGrade)
                .append("\n");
        return letterGrade;
    }


}
