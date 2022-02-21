import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseAlias;
import com.google.api.services.classroom.model.Student;
import com.google.api.services.classroom.model.Teacher;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Tests for the courses snippets.
 */
public class CoursesTest extends BaseTest {
    private Classroom service;
    private Course testCourse;
    private String otherUser = "erics@homeroomacademy.com";

    public CoursesTest() throws IOException {
        this.service = this.getService();
    }

    @Before
    public void setUp() throws IOException {
        this.testCourse = createTestCourse("me");
    }

    @After
    public void tearDown() throws IOException {
        deleteCourse(this.testCourse.getId());
        this.testCourse = null;
    }

    @Test
    public void testCreateCourse() throws IOException {
        Course course = Courses.createCourse(this.service);
        Assert.assertNotNull("Course not returned.", course);
        deleteCourse(course.getId());
    }

    @Test
    public void testGetCourse() throws IOException {
        Course course = Courses.getCourse(this.service, this.testCourse.getId());
        Assert.assertNotNull("Course not returned.", course);
        Assert.assertEquals("Wrong course returned.", this.testCourse.getId(), course.getId());
    }

    @Test
    public void testListCourses() throws IOException {
        List<Course> courses = Courses.listCourses(this.service);
        Assert.assertTrue("No courses returned.", courses.size() > 0);
    }

    @Test
    public void testUpdateCourse() throws IOException {
        Course course = Courses.updateCourse(this.service, this.testCourse.getId());
        Assert.assertNotNull("Course not returned.", course);
        Assert.assertEquals("Wrong course returned.", this.testCourse.getId(), course.getId());
    }

    @Test
    public void testPatchCourse() throws IOException {
        Course course = Courses.patchCourse(this.service, this.testCourse.getId());
        Assert.assertNotNull("Course not returned.", course);
        Assert.assertEquals("Wrong course returned.", this.testCourse.getId(), course.getId());
    }

    @Test
    public void testCreateCourseAlias() throws IOException {
        String alias = "p:" + UUID.randomUUID().toString();
        CourseAlias courseAlias =
                Courses.createCourseAlias(this.service, this.testCourse.getId(), alias);
        Assert.assertNotNull("Course alias not returned.", courseAlias);
        Assert.assertEquals("Wrong course alias returned.", alias, courseAlias.getAlias());
    }

    @Test
    public void testListCourseAliases() throws IOException {
        List<CourseAlias> courseAliases =
                Courses.listCourseAliases(this.service, this.testCourse.getId());
        Assert.assertTrue("Incorrect number of course aliases returned.", courseAliases.size() == 1);
    }

    @Test
    public void testAddTeacher() throws IOException {
        Teacher teacher = Courses.addTeacher(this.service, this.testCourse.getId(), this.otherUser);
        Assert.assertNotNull("Teacher not returned.", teacher);
        Assert.assertEquals("Teacher added to wrong course.", this.testCourse.getId(),
                teacher.getCourseId());
    }

    @Test
    public void testEnrollAsStudent() throws IOException {
        Course course = this.createTestCourse(this.otherUser);
        Student student =
                Courses.enrollAsStudent(this.service, course.getId(), course.getEnrollmentCode());
        this.deleteCourse(course.getId());
        Assert.assertNotNull("Student not returned.", student);
        Assert.assertEquals("Student added to wrong course.", course.getId(),
                student.getCourseId());
    }

    @Test
    public void testBatchAddStudents() throws IOException {
        List<String> studentEmails =
                Arrays.asList("erics@homeroomacademy.com", "zach@homeroomacademy.com");
        Courses.batchAddStudents(this.service, this.testCourse.getId(), studentEmails);
    }

    private Course createTestCourse(String ownerId) throws IOException {
        String alias = "p:" + UUID.randomUUID().toString();
        Course course = new Course().setId(alias).setName("Test Course").setSection("Section")
                .setOwnerId(ownerId);
        return this.service.courses().create(course).execute();
    }

    private void deleteCourse(String courseId) throws IOException {
        this.service.courses().delete(courseId).execute();
    }
}
