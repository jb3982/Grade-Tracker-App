package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradeTest {
    private Course course1;
    private Course course2;
    private Student student1;
    private Student student2;
    private Student student3;
    private Grade grade1;

    @BeforeEach
    public void setUp() {
        course1 = new Course("JAVA", "CS210", "Intro to Java", 210_01, 4,
                100.0);
        course2 = new Course("ENG", "ENG100", "Intro to Literature", 100_01, 3,
                80.0);
        student1 = new Student("Jack", 1);
        student2 = new Student("Jones", 2);
        student3 = new Student("Jim", 3);
        grade1 = new Grade();
    }

    @Test
    public void testPercentageToLetterGrade() {
        assertEquals("A+", grade1.percentageToLetterGrade(95));
        assertEquals("B", grade1.percentageToLetterGrade(70));
        assertEquals("F", grade1.percentageToLetterGrade(49));
        assertEquals("C", grade1.percentageToLetterGrade(60));
        assertEquals("E", grade1.percentageToLetterGrade(50));
    }

    @Test
    public void testLetterGradeToGradePoints() {
        assertEquals(4.0, grade1.letterGradeToGradePoints("A+"));
        assertEquals(3.7, grade1.letterGradeToGradePoints("A"));
        assertEquals(3.3, grade1.letterGradeToGradePoints("A-"));
        assertEquals(3.0, grade1.letterGradeToGradePoints("B+"));
        assertEquals(2.7, grade1.letterGradeToGradePoints("B"));
        assertEquals(2.3, grade1.letterGradeToGradePoints("C+"));
        assertEquals(2.0, grade1.letterGradeToGradePoints("C"));
        assertEquals(1.7, grade1.letterGradeToGradePoints("D+"));
        assertEquals(1.3, grade1.letterGradeToGradePoints("D"));
        assertEquals(1.0, grade1.letterGradeToGradePoints("E"));
        assertEquals(0.0, grade1.letterGradeToGradePoints("F"));
        assertEquals(0.0, grade1.letterGradeToGradePoints("F"));
        assertEquals(0.0, grade1.letterGradeToGradePoints("G"));
    }

    @Test
    public void testCalculateGPA() {
        List<Course> courses = new ArrayList<>();
        course1.enrollStudent(student1);
        course1.enrollStudent(student2);
        course2.enrollStudent(student1);
        course2.enrollStudent(student3);

        course1.addGrade(student1, 90);
        course1.addGrade(student2, 80);
        course2.addGrade(student1, 70);
        course2.addGrade(student3, 90);

        courses.add(course1);

        assertEquals(3.3, grade1.calculateGPA(courses));
    }
}
