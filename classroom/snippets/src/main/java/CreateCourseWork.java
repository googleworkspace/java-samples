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


// [START classroom_create_coursework]
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.CourseWork;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;

/* Class to demonstrate the use of Classroom Create CourseWork API. */
public class CreateCourseWork {
  /**
   * Creates course work.
   *
   * @param courseId - id of the course to create coursework in.
   * @return - newly created CourseWork object
   * @throws IOException - if credentials file not found.
   */
  public static CourseWork createCourseWork(String courseId) throws IOException {
    /* Load pre-authorized user credentials from the environment.
       TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Classroom samples")
        .build();

    CourseWork courseWork = null;
    try {
      // Create new CourseWork object
      CourseWork content = new CourseWork()
          .setTitle("Midterm Quiz")
          .setWorkType("ASSIGNMENT")
          .setState("PUBLISHED");
      courseWork = service.courses().courseWork().create(courseId, content)
          .execute();
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      throw e;
    } catch (Exception e) {
      throw e;
    }
    return courseWork;
  }
}
// [END classroom_create_coursework]