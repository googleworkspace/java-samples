// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


// [START classroom_create_course_with_alias_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/* Class to demonstrate how to create a course with an alias. */
public class CreateCourseWithAlias {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
    tokens/ folder. */
  static ArrayList<String> SCOPES = new ArrayList<>(
      Arrays.asList(ClassroomScopes.CLASSROOM_COURSES));

  /**
   * Create a new course with an alias. Set the new course id to the desired alias.
   *
   * @return - newly created course.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static Course createCourseWithAlias() throws GeneralSecurityException, IOException {

    // Create the classroom API client.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Classroom service =
        new Classroom.Builder(
            HTTP_TRANSPORT,
            GsonFactory.getDefaultInstance(),
            ClassroomCredentials.getCredentials(HTTP_TRANSPORT, SCOPES))
            .setApplicationName("Classroom samples")
            .build();

    // [START classroom_create_course_with_alias_code_snippet]

    Course course = null;

    /* Create a new Course with the alias set as the id field. Project-wide aliases use a prefix
    of "p:" and can only be seen and used by the application that created them. */
    Course content = new Course()
        .setId("p:history_4_2022")
        .setName("9th Grade History")
        .setSection("Period 4")
        .setDescriptionHeading("Welcome to 9th Grade History.")
        .setOwnerId("me")
        .setCourseState("PROVISIONED");

    try {
      course = service.courses().create(content).execute();
      // Prints the new created course id and name
      System.out.printf("Course created: %s (%s)\n", course.getName(), course.getId());
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 409) {
        System.out.printf("The course alias already exists: %s.\n", content.getId());
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return course;

    // [END classroom_create_course_with_alias_code_snippet]

  }
}
// [END classroom_create_course_with_alias_class]
