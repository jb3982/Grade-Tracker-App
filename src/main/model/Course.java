package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/*
 * Represents course. A course has a name, code, description, courseId, credits, and percentageGrade.
 */
public class Course {

    private String courseCode;
    private String courseName;
    private String courseDescription;
    private int courseID;
    private int credits;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Integer> enrolledStudentsID;
    private List<Double> studentGrades;
    private double percentageGrade;
    private Grade grade;


    // Effects : Constructs a Course with name, code, description, courseId, credits, and percentageGrade.
    public Course(String name, String code, String description, int courseID, int credits, double percentageGrade) {
        this.courseCode = code;
        this.courseName = name;
        this.courseDescription = description;
        this.courseID = courseID;
        this.credits = credits;
        this.percentageGrade = percentageGrade;
        this.studentGrades = new ArrayList<>();
        this.enrolledStudentsID = new ArrayList<>();
        this.grade = new Grade();
    }

    // Getters

    // Effects: returns the code of the course.
    public String getCourseCode() {
        return courseCode;
    }

    // Effects: returns the name of the course.
    public String getCourseName() {
        return courseName;
    }

    // Effects: returns the Description of the course.
    public String getCourseDescription() {
        return courseDescription;
    }

    // Effects: returns the number of credits of the course.
    public int getCredits() {
        return credits;
    }

    // Effects: returns the maximum grade (in percentage) attainable of the course.
    public double getPercentageGrade() {
        return percentageGrade;
    }

    // Effects: returns the unique ID of the course.
    public int getCourseID() {
        return courseID;
    }

//    public LocalDate getStartDate() {
//        return startDate;
//    }

//    public LocalDate getEndDate() {
//        return endDate;
//    }

    // Effects: returns list of students enrolled in the course.
    public List<Integer> getEnrolledStudentsID() {
        return enrolledStudentsID;
    }

    // Effects: returns list of received grade of the enrolled students in the course.
    public List<Double> getStudentGrades() {
        return studentGrades;
    }


    // Setters

//    public void setCourseCode(String courseCode) {
//        this.courseCode = courseCode;
//    }
//
//    public void setCourseName(String courseName) {
//        this.courseName = courseName;
//    }
//
//    public void setCourseDescription(String courseDescription) {
//        this.courseDescription = courseDescription;
//    }
//
//    public void setCourseID(int courseID) {
//        this.courseID = courseID;
//    }
//
//    public void setCredits(int credits) {
//        this.credits = credits;
//    }

    // Effects: returns the starting date of the course.
    public void setStartDate(LocalDate getStartDate) {
        this.startDate = getStartDate;
    }

    // Effects: returns the ending date of the course.
    public void setEndDate(LocalDate getEndDate) {
        this.endDate = getEndDate;
    }







    // Methods:

    // Modifies: this
    // Effects: enrolls student into the course if not already in the list
    public void enrollStudent(Student student) {
        if (!enrolledStudentsID.contains(student.getStudentID())) {
            enrolledStudentsID.add(student.getStudentID());
        }
    }


    // Modifies: this
    // Effects: removes student from the course if present in the list.
    public void removeStudent(Student student) {
        Integer studentIdToRemove = student.getStudentID();
        for (int i = 0; i < enrolledStudentsID.size(); i++) {
            if (Objects.equals(enrolledStudentsID.get(i), studentIdToRemove)) {
                enrolledStudentsID.remove(studentIdToRemove);
            }
        }
    }

    // Modifies: this
    // Effects: if student is in the list, adds corresponding grade to the student.
    public void addGrade(Student student, double grade) {
        Integer studentIdToUpdate = student.getStudentID();
        for (int i = 0; i < enrolledStudentsID.size(); i++) {
            if (Objects.equals(enrolledStudentsID.get(i), studentIdToUpdate)) {
                this.studentGrades.add(i, grade);
            }
        }
    }

    // Modifies: this
    // Effects: removes the corresponding grade of the student.
    public void removeGrade(Student student) {
        Integer studentIdToUpdate = student.getStudentID();
        for (int i = 0; i < enrolledStudentsID.size(); i++) {
            if (Objects.equals(enrolledStudentsID.get(i), studentIdToUpdate)) {
                studentGrades.add(i,0.0);
            }
        }
    }


    // Effects: if student grade is present, get the corresponding course grade
    public Double getGrade(Student student) {
        Integer gradeFrom = student.getStudentID();
        for (int i = 0; i < enrolledStudentsID.size(); i++) {
            if (Objects.equals(enrolledStudentsID.get(i), gradeFrom)) {
                return studentGrades.get(i);
            }
        }
        return null;
    }


    // Effects: Calculates the average grade of the course.
    public double calculateAverageGrade() {
        if (!studentGrades.isEmpty()) {
            double sum = 0.0;
            for (Double grade : studentGrades) {
                sum += grade;
            }
            return sum / studentGrades.size();
        } else {
            return 0.0;
        }
    }

    // Effects: Calculates the median grade of the course.
    public double calculateMedianGrade() {
        if (!studentGrades.isEmpty()) {

            List<Double> sortedGrades = new ArrayList<>(studentGrades);
            Collections.sort(sortedGrades);
            int middle = sortedGrades.size() / 2;

            if (sortedGrades.size() % 2 == 1) {
                return sortedGrades.get(middle);
            } else {
                return (sortedGrades.get(middle - 1) + sortedGrades.get(middle)) / 2.0;
            }
        }
        return 0;
    }


    // Effects: Calculates standard deviation of grades in the course.
    public double calculateStandardDeviation() {
        if (!studentGrades.isEmpty()) {
            double mean = calculateAverageGrade();
            double sumSquaredDifferences = 0.0;
            for (double num : studentGrades) {
                sumSquaredDifferences += Math.pow(num - mean, 2);
            }
            return Math.sqrt(sumSquaredDifferences / (studentGrades.size() - 1));
        }
        return 0;
    }

//    // Effects: Find students needing improvement (grade below a certain threshold)
//    public List<Student> findStudentsNeedingImprovement(double threshold) {
//        List<Integer> studentsNeedingImprovement = new ArrayList<>();
//        studentGrades.forEach((student, grade) -> {
//            if (grade < threshold) {
//                studentsNeedingImprovement.add(student);
//            }
//        });
//        for (int studentID : studentGrades) {
//
//        }
//        return studentsNeedingImprovement;
//    }

    // Effects: if the current date falls within the start and end dates of course returns true.
    public boolean isCourseActive(LocalDate currentDate) {
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }

    // Modifies: this
    // Effects: Converts percentage grade to GPA point grades.
    public double percentageToGradePoints(List<Double> studentGrades) {
        if (!studentGrades.isEmpty()) {
            double gradePoint = 0.0;
            for (double grade : studentGrades) {
                String letterGrade = this.grade.percentageToLetterGrade(grade);
                gradePoint = this.grade.letterGradeToGradePoints(letterGrade);
            }
            return gradePoint;
        }
        return 0.0;
    }


    //Effects: returns a list of letter grades, converted from percentage grade.
    public List<String> calculateGradeDistribution(List<Double> studentGrades) {
        List<String> gradeDistribution = new ArrayList<>();
        for (double grade : studentGrades) {
            String letterGrade = this.grade.percentageToLetterGrade(grade);
            gradeDistribution.add(letterGrade);
        }
        return gradeDistribution;
    }

    // Modifies: this
    // EFFECTS: returns this as JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("courseCode", courseCode);
        json.put("courseName", courseName);
        json.put("courseDescription", courseDescription);
        json.put("courseID", courseID);
        json.put("credits", credits);
        json.put("percentageGrade", percentageGrade);
        JSONArray enrolledStudentsJsonArray = new JSONArray();
        for (Integer studentID : enrolledStudentsID) {
            enrolledStudentsJsonArray.put(studentID);
        }
        json.put("enrolledStudentsID", enrolledStudentsJsonArray);
        JSONArray studentGradesJsonArray = new JSONArray();
        for (Double grade : studentGrades) {
            studentGradesJsonArray.put(grade);
        }
        json.put("studentGrades", studentGradesJsonArray);
        return json;
    }
}




