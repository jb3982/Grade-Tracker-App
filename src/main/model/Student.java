package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Student {

    private String name;
    private int studentID;
    private final List<Integer> enrolledCourses;

    // Represents a student having a name, id, and listOfCourses
    public Student(String name, int id) {
        this.name = name; // student name
        this.studentID = id; // student Id
        this.enrolledCourses = new ArrayList<>(); // list of enrolled courses
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getName() {
        return name;
    }

    public int getStudentID() {
        return studentID;
    }

    public List<Integer> getEnrolledCourses() {
        return enrolledCourses;
    }


    //Modifies: this
    //Effects: Enrolls the student in a given course.
    public void addCourse(Course course) {
        enrolledCourses.add(course.getCourseID());
        EventLog.getInstance().logEvent(new Event("Added Course Name:" + course.getCourseName()
                + "\n" + "Student Name: " + this.name));
    }

    // Modifies: this
    // Effects: removes the given course from list.
    public void dropCourse(Course course) {
        Integer courseToRemove = course.getCourseID();
        for (int i = 0; i < enrolledCourses.size(); i++) {
            if (Objects.equals(enrolledCourses.get(i), courseToRemove)) {
                enrolledCourses.remove(i);
                EventLog.getInstance().logEvent(new Event("Dropped Course Name: " + course.getCourseName()
                        + "\n" + "Student Name: " + this.name));
                break;
            }
        }
    }

    public void clearCourses() {
        enrolledCourses.clear();
    }



    // Modifies: this
    // EFFECTS: returns this as JSON object
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("studentID", studentID);
        JSONArray enrolledCoursesJsonArray = new JSONArray();
        for (Integer courseID : enrolledCourses) {
            enrolledCoursesJsonArray.put(courseID);
        }
        json.put("enrolledCourses", enrolledCoursesJsonArray);
        return json;
    }


}
