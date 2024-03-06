package model;

import java.util.List;

public class Grade {

    // Modifies: this
    // Effects: returns a string after converting percentage grade to letter grade.
    public String percentageToLetterGrade(double percentage) {
        if (percentage >= 90) {
            return "A+";
        } else if (percentage >= 85) {
            return "A";
        } else if (percentage >= 80) {
            return "A-";
        } else if (percentage >= 75) {
            return "B+";
        } else if (percentage >= 70) {
            return "B";
        } else if (percentage >= 65) {
            return "C+";
        } else if (percentage >= 60) {
            return "C";
        } else if (percentage >= 55) {
            return "D";
        } else if (percentage >= 50) {
            return "E";
        } else {
            return "F";
        }
    }

    // Modifies: this
    // Effects: converts and returns grade point from percentage grade.
    public double letterGradeToGradePoints(String letterGrade) {
        switch (letterGrade) {
            case "A+": return 4.0;
            case "A": return 3.7;
            case "A-": return 3.3;
            case "B+": return 3.0;
            case "B": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            case "D+": return 1.7;
            case "D": return 1.3;
            case "E": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }



    // Effects: Calculates the GPA of list of courses.
    public double calculateGPA(List<Course> courses) {
        double totalPoints = 0.0;
        int totalCredits = 0;
        for (Course course : courses) {
            double gradePoint = course.percentageToGradePoints(course.getStudentGrades());
            int credits = course.getCredits();
            totalPoints += gradePoint * credits;
            totalCredits += credits;

        }
        return (totalPoints / totalCredits);
    }

}
