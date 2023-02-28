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

// [START classroom_return_student_submissions_class]

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

/* Class to demonstrate the use of Classroom Return StudentSubmissions API. */
public class ReturnStudentSubmission {

  /* Scopes required by this API call. If modifying these scopes, delete your previously saved
  tokens/ folder. */
  static ArrayList<String> SCOPES =
      new ArrayList<>(Arrays.asList(ClassroomScopes.CLASSROOM_COURSEWORK_STUDENTS));
  /**
   * Return a student submission back to the student which updates the submission state to
   * `RETURNED`.
   *
   * @param courseId - identifier of the course.
   * @param courseWorkId - identifier of the course work.
   * @param id - identifier of the student submission.
   * @throws IOException - if credentials file not found.
   * @throws GeneralSecurityException - if a new instance of NetHttpTransport was not created.
   */
  public static void returnSubmission(String courseId, String courseWorkId, String id)
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

    // [START classroom_return_student_submissions_code_snippet]

    try {
      service
          .courses()
          .courseWork()
          .studentSubmissions()
          .classroomReturn(courseId, courseWorkId, id, null)
          .execute();
    } catch (GoogleJsonResponseException e) {
      // TODO (developer) - handle error appropriately
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == 404) {
        System.out.printf(
            "The courseId (%s), courseWorkId (%s), or studentSubmissionId (%s) does "
                + "not exist.\n",
            courseId, courseWorkId, id);
      } else {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }

    // [END classroom_return_student_submissions_code_snippet]

  }
}
// [END classroom_return_student_submissions_class]
