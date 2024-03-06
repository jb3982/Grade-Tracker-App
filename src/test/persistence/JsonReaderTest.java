package persistence;

import model.Course;
import model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    private static final String TEST_FILE = "testdata.json"; // path to a test JSON file
    private JsonReader reader;
    private JsonWriter writer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        // Create temporary test data for reading
        Path testFilePath = tempDir.resolve(TEST_FILE);
        writer = new JsonWriter(testFilePath.toString());
        writer.open();

        // Setup some test data
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 123));
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Intro to Java", "210", "An introductory course.", 101,
                4, 100.0));

        writer.write(students, courses);
        writer.close();

        reader = new JsonReader(testFilePath.toString());
    }

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("nonexistent.json");
        assertThrows(IOException.class, reader::read);
    }

    @Test
    void testReaderEmptyGradeTracker() {
        JsonWriter emptyWriter = new JsonWriter(tempDir.resolve("empty.json").toString());
        try {
            emptyWriter.open();
            emptyWriter.write(new ArrayList<>(), new ArrayList<>());
            emptyWriter.close();

            JsonReader emptyReader = new JsonReader(tempDir.resolve("empty.json").toString());
            JsonReader.Pair<List<Student>, List<Course>> data = emptyReader.read();
            assertTrue(data.first.isEmpty());
            assertTrue(data.second.isEmpty());
        } catch (IOException e) {
            fail("Couldn't write to empty test file");
        }
    }

    @Test
    void testReaderGeneralGradeTracker() {
        try {
            JsonReader.Pair<List<Student>, List<Course>> data = reader.read();
            assertEquals(1, data.first.size());
            assertEquals("Alice", data.first.get(0).getName());
            assertEquals(123, data.first.get(0).getStudentID());

            assertEquals(1, data.second.size());
            Course course = data.second.get(0);
            assertEquals("Intro to Java", course.getCourseName());
            assertEquals("210", course.getCourseCode());
            assertEquals("An introductory course.", course.getCourseDescription());
            assertEquals(101, course.getCourseID());
            assertEquals(4, course.getCredits());
            assertEquals(100.0, course.getPercentageGrade(), 0.001);
        } catch (IOException e) {
            fail("Couldn't read from test file");
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete test files if they exist
        Files.deleteIfExists(tempDir.resolve(TEST_FILE));
        Files.deleteIfExists(tempDir.resolve("empty.json"));
    }
}
