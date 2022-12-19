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


// [START classroom_list_submissions_class]

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.ListStudentSubmissionsResponse;
import com.google.api.services.classroom.model.StudentSubmission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Classroom List StudentSubmissions API. */
public class ListSubmissions {
  /**
   * Retrieves submissions for all students for the specified course work in a course.
   *
   * @param courseId - identifier of the course.
   * @param courseWorkId - identifier of the course work.
   * @return - list of student submissions.
   * @throws IOException - if credentials file not found.
   */
  public static List<StudentSubmission> listSubmissions(String courseId, String courseWorkId)
      throws IOException {
    /* Load pre-authorized user credentials from the environment.
     TODO(developer) - See https://developers.google.com/identity for
      guides on implementing OAuth2 for your application. */
    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        .createScoped(Collections.singleton(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));
    HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(
        credentials);

    // Create the classroom API client.
    Classroom service = new Classroom.Builder(new NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
        .setApplicationName("Classroom samples")
        .build();

    // [START classroom_list_submissions_code_snippet]

    List<StudentSubmission> studentSubmissions = new ArrayList<>();
    String pageToken = null;

    try {
      do {
        ListStudentSubmissionsResponse response = service.courses().courseWork().studentSubmissions()
            .list(courseId, courseWorkId)
            .execute();
        if (response.getStudentSubmissions() != null) {
          studentSubmissions.addAll(response.getStudentSubmissions());
        }
      } while (pageToken != null);

      if (studentSubmissions.isEmpty()) {
        System.out.println("No student submission found.");
      } else {
        for (StudentSubmission submission : studentSubmissions) {
          System.out.printf("Student id (%s), student submission id (%s)\n", submission.getUserId(),
              submission.getId());
        }
      }
    } catch (GoogleJsonResponseException e) {
      //TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf("The courseId (%s) or courseWorkId (%s) does not exist.\n", courseId,
            courseWorkId);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return studentSubmissions;

    // [END classroom_list_submissions_code_snippet]

  }
}
// [END classroom_list_submissions_class]