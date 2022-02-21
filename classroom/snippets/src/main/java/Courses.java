import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseAlias;
import com.google.api.services.classroom.model.ListCourseAliasesResponse;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.api.services.classroom.model.Student;
import com.google.api.services.classroom.model.Teacher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Courses {
    public static Course createCourse(Classroom service) throws IOException {
        // [START createCourse]
        Course course = new Course()
                .setName("10th Grade Biology")
                .setSection("Period 2")
                .setDescriptionHeading("Welcome to 10th Grade Biology")
                .setDescription("We'll be learning about about the structure of living creatures "
                        + "from a combination of textbooks, guest lectures, and lab work. Expect "
                        + "to be excited!")
                .setRoom("301")
                .setOwnerId("me")
                .setCourseState("PROVISIONED");
        course = service.courses().create(course).execute();
        System.out.printf("Course created: %s (%s)\n", course.getName(), course.getId());
        // [END createCourse]
        return course;
    }

    public static Course getCourse(Classroom service, String _courseId) throws IOException {
        // [START getCourse]
        String courseId = "123456";
        // [START_EXCLUDE silent]
        courseId = _courseId;
        // [END_EXCLUDE]
        Course course = null;
        try {
            course = service.courses().get(courseId).execute();
            System.out.printf("Course '%s' found.\n", course.getName());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Course with ID '%s' not found.\n", courseId);
            } else {
                throw e;
            }
        }
        // [END getCourse]
        return course;
    }

    public static List<Course> listCourses(Classroom service) throws IOException {
        // [START listCourses]
        String pageToken = null;
        List<Course> courses = new ArrayList<Course>();

        do {
            ListCoursesResponse response = service.courses().list()
                    .setPageSize(100)
                    .setPageToken(pageToken)
                    .execute();
            courses.addAll(response.getCourses());
            pageToken = response.getNextPageToken();
        } while (pageToken != null);

        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            System.out.println("Courses:");
            for (Course course : courses) {
                System.out.printf("%s (%s)\n", course.getName(), course.getId());
            }
        }
        // [END listCourses]
        return courses;
    }

    public static Course updateCourse(Classroom service, String _courseId) throws IOException {
        // [START updateCourse]
        String courseId = "123456";
        // [START_EXCLUDE silent]
        courseId = _courseId;
        // [END_EXCLUDE]
        Course course = service.courses().get(courseId).execute();
        course.setSection("Period 3");
        course.setRoom("302");
        course = service.courses().update(courseId, course).execute();
        System.out.printf("Course '%s' updated.\n", course.getName());
        // [END updateCourse]
        return course;
    }

    public static Course patchCourse(Classroom service, String _courseId) throws IOException {
        // [START patchCourse]
        String courseId = "123456";
        // [START_EXCLUDE silent]
        courseId = _courseId;
        // [END_EXCLUDE]
        Course course = new Course()
                .setSection("Period 3")
                .setRoom("302");
        course = service.courses().patch(courseId, course)
                .setUpdateMask("section,room")
                .execute();
        System.out.printf("Course '%s' updated.\n", course.getName());
        // [END patchCourse]
        return course;
    }

    public static CourseAlias createCourseAlias(Classroom service, String _courseId, String _alias)
            throws IOException {
        // [START createCourseAlias]
        String courseId = "123456";
        String alias = "p:bio10p2";
        // [START_EXCLUDE silent]
        courseId = _courseId;
        alias = _alias;
        // [END_EXCLUDE]
        CourseAlias courseAlias = new CourseAlias().setAlias(alias);
        try {
            courseAlias = service.courses().aliases().create(courseId, courseAlias).execute();
            System.out.printf("Alias '%s' created.\n", courseAlias.getAlias());
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 409) {
                System.out.printf("Alias '%s' is already in use.\n", alias);
            } else {
                throw e;
            }
        }
        // [END createCourseAlias]
        return courseAlias;
    }

    public static List<CourseAlias> listCourseAliases(Classroom service, String _courseId)
            throws IOException {
        // [START listCourseAliases]
        String courseId = "123456";
        // [START_EXCLUDE silent]
        courseId = _courseId;
        // [END_EXCLUDE]
        String pageToken = null;
        List<CourseAlias> courseAliases = new ArrayList<CourseAlias>();

        do {
            ListCourseAliasesResponse response = service.courses().aliases().list(courseId)
                    .setPageSize(100)
                    .setPageToken(pageToken)
                    .execute();
            courseAliases.addAll(response.getAliases());
            pageToken = response.getNextPageToken();
        } while (pageToken != null);

        if (courseAliases.isEmpty()) {
            System.out.println("No aliases found.");
        } else {
            System.out.println("Aliases:");
            for (CourseAlias courseAlias : courseAliases) {
                System.out.println(courseAlias.getAlias());
            }
        }
        // [END listCourseAliases]
        return courseAliases;
    }

    public static Teacher addTeacher(Classroom service, String _courseId, String _teacherEmail)
            throws IOException {
        // [START addTeacher]
        String courseId = "123456";
        String teacherEmail = "alice@example.edu";
        // [START_EXCLUDE silent]
        courseId = _courseId;
        teacherEmail = _teacherEmail;
        // [END_EXCLUDE]
        Teacher teacher = new Teacher().setUserId(teacherEmail);
        try {
            teacher = service.courses().teachers().create(courseId, teacher).execute();
            System.out.printf("User '%s' was added as a teacher to the course with ID '%s'.\n",
                    teacher.getProfile().getName().getFullName(), courseId);
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 409) {
                System.out.printf("User '%s' is already a member of this course.\n", teacherEmail);
            } else {
                throw e;
            }
        }
        // [END addTeacher]
        return teacher;
    }

    public static Student enrollAsStudent(Classroom service, String _courseId,
            String _enrollmentCode) throws IOException {
        // [START enrollAsStudent]
        String courseId = "123456";
        String enrollmentCode = "abcdef";
        // [START_EXCLUDE silent]
        courseId = _courseId;
        enrollmentCode = _enrollmentCode;
        // [END_EXCLUDE]
        Student student = new Student().setUserId("me");
        try {
            student = service.courses().students().create(courseId, student)
                    .setEnrollmentCode(enrollmentCode)
                    .execute();
            System.out.printf("User '%s' was enrolled as a student in the course with ID '%s'.\n",
                    student.getProfile().getName().getFullName(), courseId);
        } catch (GoogleJsonResponseException e) {
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 409) {
                System.out.println("You are already a member of this course.");
            } else {
                throw e;
            }
        }
        // [END enrollAsStudent]
        return student;
    }

    public static void batchAddStudents(Classroom service, String _courseId,
            List<String> _studentEmails) throws IOException{
        // [START batchAddStudents]
        String courseId = "123456";
        List<String> studentEmails = Arrays.asList("alice@example.edu", "bob@example.edu");
        // [START_EXCLUDE silent]
        courseId = _courseId;
        studentEmails = _studentEmails;
        // [END_EXCLUDE]
        BatchRequest batch = service.batch();
        JsonBatchCallback<Student> callback = new JsonBatchCallback<Student>() {
            public void onSuccess(Student student, HttpHeaders responseHeaders) {
                System.out.printf("User '%s' was added as a student to the course.\n",
                    student.getProfile().getName().getFullName());
            }

            public void onFailure(GoogleJsonError error, HttpHeaders responseHeaders)
                    throws IOException {
                System.out.printf("Error adding student to the course: %s\n", error.getMessage());
            }
        };
        for (String studentEmail : studentEmails) {
            Student student = new Student().setUserId(studentEmail);
            service.courses().students().create(courseId, student).queue(batch, callback);
        }
        batch.execute();
        // [END batchAddStudents]
    }
}
