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

    @BeforeEach
    public void setUp() {
        course1 = new Course("JAVA", "CS210", "Intro to Java", 210_01, 4,
                100.0);
        course2 = new Course("ENG", "ENG100", "Intro to Literature", 100_01, 3,
                80.0);
        student1 = new Student("Jack", 1);
        student2 = new Student("Jones", 2);
        student3 = new Student("Jim", 3);
    }


    @Test
    public void testPercentageToLetterGrade() {
        assertEquals("A+", Grade.percentageToLetterGrade(95));
        assertEquals("B", Grade.percentageToLetterGrade(70));
        assertEquals("F", Grade.percentageToLetterGrade(49));
        assertEquals("C", Grade.percentageToLetterGrade(60));
        assertEquals("E", Grade.percentageToLetterGrade(50));
    }

    @Test
    public void testLetterGradeToGradePoints() {
        assertEquals(4.0, Grade.letterGradeToGradePoints("A+"));
        assertEquals(2.0, Grade.letterGradeToGradePoints("C"));
        assertEquals(0.0, Grade.letterGradeToGradePoints("F"));
        assertEquals(3.3, Grade.letterGradeToGradePoints("A-"));
        assertEquals(2.7, Grade.letterGradeToGradePoints("B"));
        assertEquals(1.7, Grade.letterGradeToGradePoints("D+"));
        assertEquals(1.0, Grade.letterGradeToGradePoints("E"));
        assertEquals(0.0, Grade.letterGradeToGradePoints(""));
    }

    @Test
    public void testCalculateGPA() {
        List<Course> courses = new ArrayList<>();

        assertEquals(0.0, Grade.calculateGPA(courses));

        course1.enrollStudent(student1);
        course1.enrollStudent(student2);
        course2.enrollStudent(student1);
        course2.enrollStudent(student3);

        course1.addGrade(student1, 90);
        course1.addGrade(student2, 80);
        course2.addGrade(student1, 70);
        course2.addGrade(student3, 90);



        assertEquals(0.0,Grade.calculateGPA(courses));

        courses.add(course1);

        assertEquals(3.65, Grade.calculateGPA(courses));
    }
}
