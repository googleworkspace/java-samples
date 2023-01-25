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

// [START classroom_add_alias_to_course_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.CourseAlias;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/* Class to demonstrate the use of Classroom Create Alias API. */
public class AddAliasToCourse {
  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES = new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_COURSES));

  /**
   * Add an alias on an existing course.
   *
   * @param courseId - id of the course to add an alias to.
   * @return - newly created course alias.
   * @throws IOException - if credentials file not found.
   */
  public static CourseAlias addAliasToCourse(String courseId)
      throws GeneralSecurityException, IOException {

    // Create the classroom API client.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Classroom service =
        new Classroom.Builder(
            HTTP_TRANSPORT,
            GsonFactory.getDefaultInstance(),
            ClassroomCredentials.getCredentials(HTTP_TRANSPORT, SCOPES))
            .setApplicationName("Classroom samples")
            .build();

    // [START classroom_add_alias_to_course_code_snippet]

    /* Create a new CourseAlias object with a project-wide alias. Project-wide aliases use a prefix
    of "p:" and can only be seen and used by the application that created them. */
    CourseAlias content = new CourseAlias()
        .setAlias("p:biology_10");
    CourseAlias courseAlias = null;

    try {
      courseAlias = service.courses().aliases().create(courseId, content)
          .execute();
      System.out.printf("Course alias created: %s \n", courseAlias.getAlias());
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 409) {
        System.out.printf("The course alias already exists: %s.\n", content);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return courseAlias;

    // [END classroom_add_alias_to_course_code_snippet]

  }
}
// [END classroom_add_alias_to_course_class]
