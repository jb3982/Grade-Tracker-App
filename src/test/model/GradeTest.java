package model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GradeTest {

    @Test
    public void testPercentageToLetterGrade() {
        assertEquals("A+", Grade.percentageToLetterGrade(95));
        assertEquals("B", Grade.percentageToLetterGrade(70));
        assertEquals("F", Grade.percentageToLetterGrade(49));
    }

    @Test
    public void testLetterGradeToGradePoints() {
        assertEquals(4.0, Grade.letterGradeToGradePoints("A+"));
        assertEquals(2.0, Grade.letterGradeToGradePoints("C"));
        assertEquals(0.0, Grade.letterGradeToGradePoints("F"));
    }

//    @Test
//    public void testCalculateGPA() {
//        Course course1 = new Course("Math", "MATH101", "Algebra", 101, 3,
//                90);
//        Course course2 = new Course("English", "ENG101", "Literature", 102, 4,
//                80);
//
//        List<Course> courses = new ArrayList<>();
//        courses.add(course1);
//        courses.add(course2);
//
//        assertEquals(3.575, Grade.calculateGPA(courses));
//    }
}
