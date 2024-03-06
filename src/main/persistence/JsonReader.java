package persistence;

import model.Course;
import model.Student;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Represents a reader that reads the grade tracker data from JSON data stored in file
public class JsonReader {

    private final String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads grade tracker data from file and returns it as a list of students and courses;
    // throws IOException if an error occurs reading data from file
    public Pair<List<Student>, List<Course>> read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseGradeTracker(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        return new String(Files.readAllBytes(Paths.get(source)), StandardCharsets.UTF_8);
    }

    // EFFECTS: parses workroom from JSON object and returns it
    private Pair<List<Student>, List<Course>> parseGradeTracker(JSONObject jsonObject) {
        JSONArray studentsJsonArray = jsonObject.getJSONArray("students");
        JSONArray coursesJsonArray = jsonObject.getJSONArray("courses");

        List<Course> courses = parseCourses(coursesJsonArray);
        List<Student> students = parseStudents(studentsJsonArray, courses);

        return new Pair<>(students, courses);
    }


    // EFFECTS: parses students from JSON array and returns it as a list
    private List<Student> parseStudents(JSONArray studentsJsonArray, List<Course> courses) {
        List<Student> students = new ArrayList<>();
        for (Object json : studentsJsonArray) {
            JSONObject studentJson = (JSONObject) json;
            Student student = parseStudent(studentJson, courses);
            students.add(student);
        }
        return students;
    }

    // EFFECTS: parses student from JSON object and returns it
    private Student parseStudent(JSONObject studentJson, List<Course> courses) {
        String name = studentJson.getString("name");
        int studentID = studentJson.getInt("studentID");
        Student student = new Student(name, studentID);

        JSONArray enrolledCoursesJsonArray = studentJson.getJSONArray("enrolledCourses");
        for (Object courseIdObj : enrolledCoursesJsonArray) {
            int courseId = (Integer) courseIdObj;
            Course course = findCourseById(courseId, courses);
            if (course != null) {
                student.addCourse(course);
                course.enrollStudent(student);
            }
        }
        return student;
    }

    // EFFECTS: parses courses from JSON array and returns it as a list
    private List<Course> parseCourses(JSONArray coursesJsonArray) {
        List<Course> courses = new ArrayList<>();
        for (Object json : coursesJsonArray) {
            JSONObject courseJson = (JSONObject) json;
            Course course = parseCourse(courseJson);
            courses.add(course);
        }
        return courses;
    }


    // Effects: finds course using the course ID in the list of Courses
    private Course findCourseById(int courseId, List<Course> courses) {
        for (Course course : courses) {
            if (course.getCourseID() == courseId) {
                return course;
            }
        }
        return null;
    }


    // EFFECTS: parses course from JSON object and returns it
    private Course parseCourse(JSONObject courseJson) {
        String courseName = courseJson.getString("courseName");
        String courseCode = courseJson.getString("courseCode");
        String courseDescription = courseJson.getString("courseDescription");
        int courseID = courseJson.getInt("courseID");
        int credits = courseJson.getInt("credits");
        double percentageGrade = courseJson.getDouble("percentageGrade");

        Course course = new Course(courseName, courseCode, courseDescription, courseID, credits, percentageGrade);

        // Enrolled students ID list parsing
        extractEnrolledStudentsID(courseJson, course);

        // Student grades list parsing
        extractStudentGrades(courseJson, course);
        // Print the loaded course details
        System.out.println("Loaded course: " + course.getCourseName());
        System.out.println("Enrolled students after load: " + course.getEnrolledStudentsID());
        System.out.println("Student grades after load: " + course.getStudentGrades());


        return course;
    }

    private static void extractStudentGrades(JSONObject courseJson, Course course) {
        JSONArray studentGradesJsonArray = courseJson.optJSONArray("studentGrades");
        System.out.println("Student grades: "
                + (studentGradesJsonArray != null ? studentGradesJsonArray.toString() : "null"));

        if (studentGradesJsonArray != null) {
            for (Object gradeObj : studentGradesJsonArray) {
                double grade;
                if (gradeObj instanceof Integer) {
                    grade = ((Integer) gradeObj).doubleValue(); // Convert Integer to Double
                } else if (gradeObj instanceof Double) {
                    grade = (Double) gradeObj;
                } else {
                    throw new IllegalArgumentException("Invalid grade type in JSON: " + gradeObj.getClass().getName());
                }
                course.getStudentGrades().add(grade);
            }
        }
    }

    private static void extractEnrolledStudentsID(JSONObject courseJson, Course course) {
        JSONArray enrolledStudentsIdJsonArray = courseJson.optJSONArray("enrolledStudentsID");
        System.out.println("Enrolled students IDs: "
                + (enrolledStudentsIdJsonArray != null ? enrolledStudentsIdJsonArray.toString() : "null"));

        if (enrolledStudentsIdJsonArray != null) {
            for (Object studentIdObj : enrolledStudentsIdJsonArray) {
                int studentId = (Integer) studentIdObj;
                course.getEnrolledStudentsID().add(studentId);
            }
        }
    }

    // A simple Pair class
    public static class Pair<K, V> {
        public final K first;
        public final V second;

        public Pair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }
}
