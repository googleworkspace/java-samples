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

// [START classroom_list_student_submissions_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.ListStudentSubmissionsResponse;
import com.google.api.services.classroom.model.StudentSubmission;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Class to demonstrate the use of Classroom List StudentSubmissions API. */
public class ListStudentSubmissions {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES = new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));

  /**
   * Retrieves a specific student's submissions for the specified course work.
   *
   * @param courseId - identifier of the course.
   * @param courseWorkId - identifier of the course work.
   * @param userId - identifier of the student whose work to return.
   * @return - list of student submissions.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static List<StudentSubmission> listStudentSubmissions(String courseId, String courseWorkId, String userId)
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

    // [START classroom_list_student_submissions_code_snippet]

    List<StudentSubmission> studentSubmissions = new ArrayList<>();
    String pageToken = null;

    try {
      do {
        // Set the userId as a query parameter on the request.
        ListStudentSubmissionsResponse response =
            service
                .courses()
                .courseWork()
                .studentSubmissions()
                .list(courseId, courseWorkId)
                .setPageToken(pageToken)
                .set("userId", userId)
                .execute();

        /* Ensure that the response is not null before retrieving data from it to avoid errors. */
        if (response.getStudentSubmissions() != null) {
          studentSubmissions.addAll(response.getStudentSubmissions());
          pageToken = response.getNextPageToken();
        }
      } while (pageToken != null);

      if (studentSubmissions.isEmpty()) {
        System.out.println("No student submission found.");
      } else {
        for (StudentSubmission submission : studentSubmissions) {
          System.out.printf("Student submission: %s.\n", submission.getId());
        }
      }
    } catch (GoogleJsonResponseException e) {
      // TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf(
            "The courseId (%s), courseWorkId (%s), or userId (%s) does " + "not exist.\n",
            courseId, courseWorkId, userId);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
    return studentSubmissions;

    // [END classroom_list_student_submissions_code_snippet]

  }
}
// [END classroom_list_student_submissions_class]
