package persistence;

import model.Course;
import model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    private List<Student> students;
    private List<Course> courses;
    private final String testFile = "testdata.json"; // Replace with your test file path
    private JsonWriter writer;

    @BeforeEach
    void setUp() {
        students = new ArrayList<>();
        courses = new ArrayList<>();
        students.add(new Student("Alice", 123));
        courses.add(new Course("Intro to Java", "210", "An introductory course.", 101,
                4, 100.0));
        writer = new JsonWriter(testFile);
    }

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./path/does/not/exist/testWriter.json");
            writer.open();
            fail("IOException was expected");
        } catch (FileNotFoundException e) {
            // Expected result
        }
    }

    @Test
    void testWriterEmptyGradeTracker() {
        try {
            writer.open();
            writer.write(new ArrayList<>(), new ArrayList<>());
            writer.close();

            JsonReader reader = new JsonReader(testFile);
            var result = reader.read();
            assertTrue(result.first.isEmpty());
            assertTrue(result.second.isEmpty());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralGradeTracker() {
        try {
            writer.open();
            writer.write(students, courses);
            writer.close();

            JsonReader reader = new JsonReader(testFile);
            var result = reader.read();
            assertEquals(1, result.first.size());
            assertEquals(1, result.second.size());
            assertEquals("Alice", result.first.get(0).getName());
            assertEquals("Intro to Java", result.second.get(0).getCourseName());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @AfterEach
    void tearDown(@TempDir Path tempDir) {
        // Use the @TempDir annotation to create temporary directories for each test,
        // and delete the test file after each test
        try {
            Files.deleteIfExists(tempDir.resolve(testFile));
        } catch (IOException e) {
            fail("Could not delete test file");
        }
    }
}
