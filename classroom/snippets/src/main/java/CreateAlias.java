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


// [START classroom_create_alias]
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate how to create a course with an alias. */
public class CreateAlias {
  /**
   * Create a new course with an alias. Set the new course id to the desired alias.
   *
   * @return - newly created course.
   * @throws IOException - if credentials file not found.
   */
  public static Course createAlias() throws IOException {
    /* Load pre-authorized user credentials from the environment.
       TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_COURSES));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Classroom samples")
        .build();

    Course course = null;
    try {
      // Create the new Course
      Course content = new Course()
          .setId("p:history_4_2022")
          .setName("9th Grade History")
          .setSection("Period 4")
          .setDescriptionHeading("Welcome to 9th Grade History.")
          .setOwnerId("me")
          .setCourseState("PROVISIONED");
      course = service.courses().create(content).execute();
      // Prints the new created course id and name
      System.out.printf("Course created: %s (%s)\n", course.getName(), course.getId());
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      throw e;
    } catch (Exception e) {
      throw e;
    }
    return course;
  }
}
// [END classroom_create_alias]
