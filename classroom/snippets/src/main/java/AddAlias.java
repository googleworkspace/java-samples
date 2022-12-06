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


// [START classroom_add_alias]

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.CourseAlias;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Classroom Create Alias API. */
public class AddAlias {
  /**
   * Add an alias on an existing course.
   *
   * @param courseId - id of the course to add an alias to.
   * @return - newly created course alias.
   * @throws IOException - if credentials file not found.
   */
  public static CourseAlias addCourseAlias(String courseId) throws IOException {
    /* Load pre-authorized user credentials from the environment.
       TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_COURSES));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client.
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Classroom samples")
        .build();

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
  }
}
// [END classroom_add_alias]
