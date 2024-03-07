package persistence;

import model.Course;
import model.Student;
import org.json.JSONArray;
import org.json.JSONObject;
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
        List<Course> courses = new ArrayList<>();
        Course course1 = new Course("Intro to Java", "210", "An introductory course.", 101,
                4, 100.0);
        courses.add(course1);

        List<Student> students = new ArrayList<>();
        Student student1 = new Student("Alice", 123);
        student1.addCourse(course1);
        students.add(student1);

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

    @Test
    void testReaderGeneralGradeTracker_1() {
        try {
            // Act
            JsonReader.Pair<List<Student>, List<Course>> data = reader.read();

            // Assert that the course with the specific ID is present
            Course course = data.second.stream()
                    .filter(c -> c.getCourseID() == 101)
                    .findFirst()
                    .orElse(null);
            assertNotNull(course, "Course with ID 101 should be found");

            // Assert that a course with a non-existent ID is not present
            Course nonExistentCourse = data.second.stream()
                    .filter(c -> c.getCourseID() == 999)
                    .findFirst()
                    .orElse(null);
            assertNull(nonExistentCourse, "Course with ID 999 should not be found");

        } catch (IOException e) {
            fail("Couldn't read from test file due to IOException");
        }
    }

    @Test
    void testReadAndParseStudent() {
        try {
            JsonReader.Pair<List<Student>, List<Course>> data = reader.read();
            Student student = data.first.get(0); // Assuming Alice is the first student
            assertEquals("Alice", student.getName());
            assertEquals(123, student.getStudentID());

            // Get the list of enrolled courses for the student
            List<Integer> enrolledCourseIds = student.getEnrolledCourses();
            assertNotNull(enrolledCourseIds, "Enrolled courses should not be null");

            // Now check if the specific course ID is in the enrolled courses
            boolean containsCourse = enrolledCourseIds.contains(101);
            assertTrue(containsCourse, "Enrolled courses should contain course ID 101, but it does not; actual list: " + enrolledCourseIds);

        } catch (IOException e) {
            fail("Couldn't read from test file due to IOException");
        }
    }

    @Test
    void testParseStudentWithNonExistentCourse() {
        // Assume JsonReader has a public constructor and parseStudent is made package-private for testing
        JsonReader jsonReader = new JsonReader(""); // The path is irrelevant for this test

        String studentJsonString = "{"
                + "\"name\":\"Test Student\","
                + "\"studentID\":123,"
                + "\"enrolledCourses\":[999]" // Use a non-existent course ID to trigger the branch
                + "}";
        JSONObject studentJson = new JSONObject(studentJsonString);

        List<Course> courses = new ArrayList<>(); // empty list simulating no courses found

        // Act
        Student parsedStudent = jsonReader.parseStudent(studentJson, courses);

        // Assert
        assertNotNull(parsedStudent, "Parsed student should not be null");
        assertTrue(parsedStudent.getEnrolledCourses().isEmpty(), "Enrolled courses list should be empty since the course ID doesn't exist");
    }

    @Test
    void testExtractStudentGrades() {
        try {
            // Setup
            Path testFilePath = tempDir.resolve(TEST_FILE);

            // Create a Course with JSON array having Integer and Double grades
            JSONObject courseObject = new JSONObject();
            courseObject.put("courseName", "Test Course");
            courseObject.put("courseCode", "TC101");
            courseObject.put("courseDescription", "Test Description");
            courseObject.put("courseID", 101);
            courseObject.put("credits", 4);
            courseObject.put("percentageGrade", 85.0);

            // Add a JSONArray with an Integer and a Double grade
            JSONArray gradesArray = new JSONArray();
            gradesArray.put(85); // Integer grade
            gradesArray.put(90.5); // Double grade
            courseObject.put("studentGrades", gradesArray);

            // Create a Course object
            Course course = new Course("Test Course", "TC101", "Test Description", 101, 4, 85.0);

            // Call the method extractStudentGrades from JsonReader
            JsonReader.extractStudentGrades(courseObject, course);

            // Assert that both grades are correctly added
            List<Double> grades = course.getStudentGrades();
            assertEquals(2, grades.size(), "Should have 2 grades");
            assertEquals(85.0, grades.get(0), 0.001, "First grade should be 85.0 as Double");
            assertEquals(90.5, grades.get(1), 0.001, "Second grade should be 90.5");

            // Add an unsupported grade type to the JSON array and expect an exception
            gradesArray.put("A"); // Unsupported grade type
            courseObject.put("studentGrades", gradesArray);

            // Assert that IllegalArgumentException is thrown for unsupported grade type
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                JsonReader.extractStudentGrades(courseObject, course);
            }, "IllegalArgumentException expected for unsupported grade type");

            String expectedMessage = "Invalid grade type in JSON: java.lang.String";
            String actualMessage = exception.getMessage();
            assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain the expected message");

        } catch (Exception e) {
            fail("An unexpected exception occurred: " + e.getMessage());
        }
    }

    @Test
    void testExtractStudentGradesWithInvalidType() {
        JSONObject courseJson = new JSONObject();
        JSONArray gradesArray = new JSONArray();
        // Add an invalid grade type to trigger the IllegalArgumentException
        gradesArray.put("A+");
        courseJson.put("studentGrades", gradesArray);

        Course course = new Course("Test Course", "TC101", "Test Description", 101, 4, 85.0);

        // Expect the IllegalArgumentException when a non-numeric grade is processed
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            JsonReader.extractStudentGrades(courseJson, course);
        });

        // Verify that the exception message is as expected
        String expectedMessage = "Invalid grade type in JSON";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain the expected text");
    }

    @Test
    void testExtractStudentGradesWhenJsonArrayIsNull() {
        // Set up a course JSON object without the studentGrades array
        JSONObject courseJson = new JSONObject();
        // Don't put a studentGrades array to simulate the null scenario

        // Create a course to be used in the test
        Course course = new Course("Test Course", "TC101", "Test Description", 101,
                4, 85.0);

        // Call the static method extractStudentGrades
        JsonReader.extractStudentGrades(courseJson, course);

        // Verify that the student grades list is empty
        assertTrue(course.getStudentGrades().isEmpty(), "Student grades list should be empty when studentGradesJsonArray is null");
    }

    @Test
    void testExtractEnrolledStudentsID() {
        try {
            // Setup
            Path testFilePath = tempDir.resolve(TEST_FILE);

            // Create a Course with JSON array of enrolled student IDs
            JSONObject courseObject = new JSONObject();
            courseObject.put("courseName", "Test Course");
            courseObject.put("courseCode", "TC101");
            courseObject.put("courseDescription", "Test Description");
            courseObject.put("courseID", 101);
            courseObject.put("credits", 4);
            courseObject.put("percentageGrade", 85.0);

            // Add a JSONArray with student IDs
            JSONArray enrolledStudentsIdArray = new JSONArray();
            enrolledStudentsIdArray.put(123); // Integer student ID
            enrolledStudentsIdArray.put(456); // Integer student ID
            courseObject.put("enrolledStudentsID", enrolledStudentsIdArray);

            // Create a Course object
            Course course = new Course("Test Course", "TC101", "Test Description", 101, 4, 85.0);

            // Call the method extractEnrolledStudentsID from JsonReader
            JsonReader.extractEnrolledStudentsID(courseObject, course);

            // Assert that both IDs are correctly added
            List<Integer> enrolledIDs = course.getEnrolledStudentsID();
            assertEquals(2, enrolledIDs.size(), "Should have 2 enrolled student IDs");
            assertTrue(enrolledIDs.contains(123), "Enrolled student IDs should contain 123");
            assertTrue(enrolledIDs.contains(456), "Enrolled student IDs should contain 456");

            // Add an unsupported data type to the JSON array
            enrolledStudentsIdArray.put("NotAnID"); // Unsupported ID type
            courseObject.put("enrolledStudentsID", enrolledStudentsIdArray);

            // Attempt to extract enrolled students and expect an exception
            assertThrows(ClassCastException.class, () -> {
                JsonReader.extractEnrolledStudentsID(courseObject, course);
            });
        } catch (Exception e) {
            // Output the actual exception message
            e.printStackTrace(); // This prints the stack trace including the message
            String actualMessage = e.getMessage();
            System.out.println("Actual exception message: " + actualMessage);

            // Assert that the message contains a specific text
            String expectedMessage = "Cannot cast java.lang.String to java.lang.Integer";
            System.out.println("Actual exception message: " + actualMessage);
            assertTrue(actualMessage.contains(expectedMessage), "Exception message should contain the expected message");
        }

    }

    @Test
    void testExtractEnrolledStudentsID_1() {
        // Set up JSON object to mimic the structure you expect to read
        JSONObject courseJson = new JSONObject();
        JSONArray enrolledStudentsIdJsonArray = new JSONArray();
        // Adding valid student IDs
        enrolledStudentsIdJsonArray.put(101);
        enrolledStudentsIdJsonArray.put(102);
        courseJson.put("enrolledStudentsID", enrolledStudentsIdJsonArray);

        // Create a course to be used in the test
        Course course = new Course("Test Course", "TC101", "Test Description", 101, 4, 85.0);

        // Call the static method extractEnrolledStudentsID
        JsonReader.extractEnrolledStudentsID(courseJson, course);

        // Verify that the student IDs have been added to the course
        assertEquals(2, course.getEnrolledStudentsID().size());
        assertTrue(course.getEnrolledStudentsID().contains(101));
        assertTrue(course.getEnrolledStudentsID().contains(102));
    }

    @Test
    void testExtractEnrolledStudentsIDWhenJsonArrayIsNull() {
        JSONObject courseJson = new JSONObject(); // No "enrolledStudentsID" key/value added to simulate null array
        Course course = new Course("Test Course", "TC101", "Test Description", 101, 4, 85.0);

        // Assuming extractEnrolledStudentsID has package-private access for testing, otherwise use reflection
        JsonReader.extractEnrolledStudentsID(courseJson, course);

        // The list of enrolled student IDs should be empty because the JSON array was null
        assertTrue(course.getEnrolledStudentsID().isEmpty(), "Enrolled students ID list should be empty when JSON array is null");
    }

    @Test
    void testFindCourseByIdNonExistent() {
        // Assume JsonReader has a public constructor and findCourseById is made package-private for testing
        JsonReader jsonReader = new JsonReader(""); // The path is irrelevant for this test

        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Test Course", "TC101", "Test Description", 101, 4, 85.0));

        // Act
        // Call the method with a courseID that does not exist in the list
        Course course = jsonReader.findCourseById(999, courses);

        // Assert
        assertNull(course, "Should return null for a non-existent courseID");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete test files if they exist
        Files.deleteIfExists(tempDir.resolve(TEST_FILE));
        Files.deleteIfExists(tempDir.resolve("empty.json"));
    }
}
