/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseAlias;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.util.UUID;

// Base class for integration tests.
public class BaseTest {
  protected Classroom service;
  protected Course testCourse;

  /**
   * Creates a default authorization Classroom client service.
   *
   * @return an authorized Classroom client service
   * @throws IOException - if credentials file not found.
   */
  protected Classroom buildService() throws Exception {
    /* Scopes required by this API call. If modifying these scopes, delete your previously saved
    tokens/ folder. */
    final List<String> SCOPES = Collections.singletonList(ClassroomScopes.CLASSROOM_COURSES);

    // Create the classroom API client
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Classroom service =
        new Classroom.Builder(
                HTTP_TRANSPORT,
                GsonFactory.getDefaultInstance(),
                ClassroomCredentials.getCredentials(HTTP_TRANSPORT, SCOPES))
            .setApplicationName("Classroom samples")
            .build();

    return service;
  }

  @Before
  public void setup() throws Exception {
    this.service = buildService();
    this.testCourse = CreateCourse.createCourse();
    createAlias(this.testCourse.getId());
  }

  @After
  public void tearDown() throws IOException {
    deleteCourse(this.testCourse.getId());
    this.testCourse = null;
  }

  public CourseAlias createAlias(String courseId) throws IOException {
    String alias = "p:" + UUID.randomUUID();
    CourseAlias courseAlias = new CourseAlias().setAlias(alias);
    courseAlias = this.service.courses().aliases().create(courseId, courseAlias).execute();
    return courseAlias;
  }

  public void deleteCourse(String courseId) throws IOException {
    // updating the course state to be archived so the course can be deleted.
    Course course = service.courses().get(courseId).execute();
    course.setCourseState("ARCHIVED");
    this.service.courses().update(courseId, course).execute();
    this.service.courses().delete(courseId).execute();
  }
}
