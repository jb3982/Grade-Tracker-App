package model;

import java.time.LocalDate;
import java.util.*;

import static model.Grade.letterGradeToGradePoints;
import static model.Grade.percentageToLetterGrade;

public class Course {

    private final String courseCode;
    private final String courseName;
    private final String courseDescription;
    private final int courseID;
    private final int credits;
    private LocalDate startDate;
    private LocalDate endDate;
    private final List<Integer> enrolledStudentsID;
    private final List<Double> studentGrades;
    private final double percentageGrade;



    public Course(String name, String code, String description, int courseID, int credits, double percentageGrade) {
        this.courseCode = code;
        this.courseName = name;
        this.courseDescription = description;
        this.courseID = courseID;
        this.credits = credits;
        this.percentageGrade = percentageGrade;
        this.studentGrades = new ArrayList<>();
        this.enrolledStudentsID = new ArrayList<>();
    }

    // Getters

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public int getCredits() {
        return credits;
    }

    public double getPercentageGrade() {
        return percentageGrade;
    }

    public int getCourseID() {
        return courseID;
    }

//    public LocalDate getStartDate() {
//        return startDate;
//    }
//
//    public LocalDate getEndDate() {
//        return endDate;
//    }

    public List<Integer> getEnrolledStudentsID() {
        if (!enrolledStudentsID.isEmpty()) {
            return enrolledStudentsID;
        }
        return null;
    }

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

    public void setStartDate(LocalDate getStartDate) {
        this.startDate = getStartDate;
    }

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


    // Modifies: This
    // Effects: removes student from the course if present in the list.
    public void removeStudent(Student student) {
        Integer studentIdToRemove = student.getStudentID();
        for (int i = 0; i < enrolledStudentsID.size(); i++) {
            if (Objects.equals(enrolledStudentsID.get(i), studentIdToRemove)) {
                enrolledStudentsID.remove(studentIdToRemove);
            }
        }
    }

    // Effects: if student is in the list, adds corresponding grade to the student.
    public void addGrade(Student student, double grade) {
        Integer studentIdToUpdate = student.getStudentID();
        for (int i = 0; i < enrolledStudentsID.size(); i++) {
            if (Objects.equals(enrolledStudentsID.get(i), studentIdToUpdate)) {
                studentGrades.add(i, grade);
            }
        }
    }

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

    // Calculate the median grade for the course
    public double calculateMedianGrade() {
        if (!studentGrades.isEmpty()) {
            int middle = studentGrades.size() / 2;
            if (studentGrades.size() % 2 == 1) {
                return studentGrades.get(middle);
            } else {
                return ((studentGrades.get(middle) + 1) + studentGrades.get(middle)) / 2.0;
            }
        }
        return 0;
    }

    // Calculate standard deviation of grades
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

//    // Find students needing improvement (grade below a certain threshold)
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

//    }
    public double percentageToGradePoints(List<Double> studentGrades) {
        if (!studentGrades.isEmpty()) {
            double totalGradePoints = 0.0;
            for (double grade : studentGrades) {
                String letterGrade = percentageToLetterGrade(grade);
                totalGradePoints += letterGradeToGradePoints(letterGrade);
            }
            return totalGradePoints / studentGrades.size();
        }
        return 0.0;
    }


    public List<String> calculateGradeDistribution(List<Double> studentGrades) {
        List<String> gradeDistribution = new ArrayList<>();
        for (double grade : studentGrades) {
            String letterGrade = percentageToLetterGrade(grade);
            gradeDistribution.add(letterGrade);
        }
        return gradeDistribution;
    }
}




