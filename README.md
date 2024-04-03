# Grade Tracker Application


### **Overview**

The Student Grade Tracker is an upcoming Java desktop application designed to simplify the task of tracking and documenting student performances across multiple courses. The goal is to equip educators with a straightforward platform that streamlines the entry, modification, and review of student grades. With capabilities like auto-calculating GPAs and generating tailored reports, the application aims to become an all-encompassing tool for educational records management, ensuring transparency and order in the depiction of student achievements.

---

### **Targeted Audience**

Educators and academic administrators are the intended primary users of the Student Grade Tracker, as they handle the critical task of monitoring and evaluating students' academic results. This application is tailored to become a vital resource for them, simplifying the management of academic records with accuracy and convenience. Furthermore, students will also find value in this application as it will allow them to meticulously oversee their grades, which supports self-monitoring and fosters an active role in their educational progress.

---

### **Reasons for Pursuing this Project**

My personal connection to this project is deeply rooted in the difficulties I've encountered with academic performance tracking. The intricate task of handling and deciphering multiple grading systems has motivated me to create a streamlined solution for others facing similar challenges. The drive behind the Student Grade Tracker is my commitment to deliver a transparent and straightforward approach to managing educational outcomes, aiming to significantly enhance the learning and teaching experience.

---

###  **Key Features:**
-  **Grade Input**: Simplified interface to enter and update grades.
- **GPA Calculation**: Automated calculation of overall and course-specific GPA.
- **Reports**: Generation of detailed reports for individual students or classes.

---

### **User Stories:**
- As a user, I need the capability to enter, store, and later retrieve the coursework and examination grades for each class to ensure that no academic data is lost between sessions. 
- As a user, I require a system that allows me to record student grades immediately following the completion of a course to maintain up-to-date academic records.
- As a user, I seek the functionality to automatically calculate and update a student's GPA as soon as new grades are entered to reflect their current academic standing.
- As a user, I wish to generate comprehensive reports of student grades, enabling a detailed overview of individual academic performance.
- As a user, I desire access to a summarized view of grade distributions to effectively monitor and adhere to academic standards.
- As a user, I would like to be prompted to save the application's state to a file when I choose to save, giving me the control to decide whether or not to save the changes made during the session.
- As a user, upon launching the application, I wish to be presented with the option to load the grade tracker's state from a previously saved file, allowing me to resume work seamlessly from where I last left off.
- As a user, I should be given a set of buttons to add a student, course and enter grades.

---

## **Phase 4: Task 2**
---- Application Event Log Start ---- \
Wed Apr 03 12:38:21 PDT 2024 \
Added Course Name:Java \
Student Name: JAI \
Wed Apr 03 12:38:21 PDT 2024 \
Added Course Name:BSL \
Student Name: JAI \
Wed Apr 03 12:38:21 PDT 2024 \
Added Course Name:Java \
Student Name: raghu \
Wed Apr 03 12:38:21 PDT 2024 \
Added Course Name:BSL \
Student Name: raghu \
Wed Apr 03 12:38:39 PDT 2024 \
Added Course Name:BSL \
Student Name: Harry \
Wed Apr 03 12:38:39 PDT 2024 \
Student Name: Harry \
Enrolled in course: BSL \
Wed Apr 03 12:38:50 PDT 2024 \
Added grade for student: Harry \
For course: BSL \
---- Application Event Log End ----

---

## **Phase 4: Task 3**
Reflecting on the UML class diagram and the project design, I feel that making the project more clear and distinct would
benefit. Presently, the GradeTrackerGUI class bears all the weight of direct data operations such as with JsonReader and
JsonWriter. I believe, by introducing  a dedicated layer responsible solely for data transaction would enhance the 
severance between the presentation and data access. This refactoring would lead to a more streamlined and evolved code.

Additionally, the Course class, as it stands, extends beyond its primary domain. Not only it is managing the course 
details but also student enrolment and grade processing. This multifaceted responsibility could be simplified by
representing the grade related concerns to a single class. Such division would not only simplify the Course class but 
also give meaning to the Grade class and develop the code within the domain of the course management.