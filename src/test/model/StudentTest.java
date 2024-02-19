package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StudentTest {

    private Student student;
    private Course mathCourse;
    private Course englishCourse;
    private Course philosophyCourse;

    @BeforeEach
    public void setUp() {
        student = new Student("John Doe", 123);
        mathCourse = new Course("Math", "MATH101", "Algebra", 101, 3,
                90);
        englishCourse = new Course("English", "ENG101", "Literature", 102, 4,
                85);
        philosophyCourse = new Course("Philosophy", "PHIL220", "Logic", 103, 3,
                95);
    }

    @Test
    public void testAddCourse() {
        student.addCourse(mathCourse);

        assertTrue(student.getEnrolledCourses().contains(mathCourse.getCourseID()));
    }

    @Test
    public void testDropCourse() {
        student.addCourse(mathCourse);
        student.addCourse(englishCourse);

        assertTrue(student.getEnrolledCourses().contains(mathCourse.getCourseID()));

        student.dropCourse(mathCourse);
        assertFalse(student.getEnrolledCourses().contains(mathCourse.getCourseID()));

        int size = student.getEnrolledCourses().size();
        student.dropCourse(philosophyCourse);
        assertEquals(size, student.getEnrolledCourses().size());
    }

    @Test
    public void testGetEnrolledCourses() {
        student.addCourse(mathCourse);
        student.addCourse(englishCourse);
        assertEquals(2, student.getEnrolledCourses().size());
        assertTrue(student.getEnrolledCourses().contains(mathCourse.getCourseID()));
        assertTrue(student.getEnrolledCourses().contains(englishCourse.getCourseID()));
    }
}
