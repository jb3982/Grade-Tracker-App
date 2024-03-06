package persistence;

import model.Course;
import model.Student;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.List;

// Represents a writer that writes JSON representation of grade tracker data to file
public class JsonWriter {

    private static final int TAB = 4;
    private PrintWriter writer;
    private String destination;

    // EFFECTS: constructs writer to write to destination file
    public JsonWriter(String destination) {
        this.destination = destination;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(destination));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of grade tracker data to file
    public void write(List<Student> students, List<Course> courses) {
        JSONObject json = new JSONObject();
        JSONArray studentsJsonArray = new JSONArray();
        for (Student student : students) {
            studentsJsonArray.put(student.toJson());
        }
        json.put("students", studentsJsonArray);

        JSONArray coursesJsonArray = new JSONArray();
        for (Course course : courses) {
            coursesJsonArray.put(course.toJson());
        }
        json.put("courses", coursesJsonArray);

        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this
    // EFFECTS: closes writer
    public void close() {
        writer.close();
    }

    // MODIFIES: this
    // EFFECTS: writes string to file
    private void saveToFile(String json) {
        writer.print(json);
    }
}